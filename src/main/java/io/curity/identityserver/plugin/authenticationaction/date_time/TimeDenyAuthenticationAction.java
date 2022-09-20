/*
 *  Copyright 2020 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.authenticationaction.date_time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionContext;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class TimeDenyAuthenticationAction implements AuthenticationAction
{
    private final static Logger _logger = LoggerFactory.getLogger(TimeDenyAuthenticationAction.class);

    private final TimeComparer _timeComparer;

    public TimeDenyAuthenticationAction(TimeDenyAuthenticationActionConfiguration configuration)
    {
        _timeComparer = TimeComparer.create(configuration.getNoAccessBefore(), configuration.getNoAccessAfter(),
                configuration.getTimeZone());
    }

    @Override
    public AuthenticationActionResult apply(AuthenticationActionContext context)
    {
        if (_timeComparer.isNowAfterStartTime())
        {
            if (_timeComparer.isNowBeforeEndTime())
            {
                _logger.debug("Access allowed based on time");

                return AuthenticationActionResult.successfulResult(context.getAuthenticationAttributes());
            }
            else if (_logger.isDebugEnabled())
            {
                ZoneId zoneId = _timeComparer.getZoneId();

                _logger.debug(
                        "Access denied because current time {} in timezone {} is after the configured allowed end time {}",
                        _timeComparer.getNow().atZone(zoneId), zoneId, _timeComparer.getEndTime());
            }
        }
        else if (_logger.isDebugEnabled())
        {
            ZoneId zoneId = _timeComparer.getZoneId();

            _logger.debug(
                    "Access denied because current time {} in timezone {} is after the configured allowed start time {}",
                    _timeComparer.getNow().atZone(zoneId), zoneId, _timeComparer.getStartTime());
        }

        return AuthenticationActionResult.failedResult("Access denied based on time");
    }

    // Visible for testing
    static final class TimeComparer
    {
        private final Instant _now;
        private final Instant _endTime;
        private final Instant _startTime;
        private final ZoneId _zoneId;

        private TimeComparer(Instant startTime, Instant endTime, Instant now, ZoneId zoneId)
        {
            _startTime = startTime;
            _endTime = endTime;
            _now = now;
            _zoneId = zoneId;
        }

        static TimeComparer create(TimeConfiguration noAccessBefore, TimeConfiguration noAccessAfter,
                                   TimeZoneDisplay timezone)
        {
            return create(noAccessBefore, noAccessAfter, timezone, Clock.systemDefaultZone());
        }

        // VisibleForTesting
        static TimeComparer create(TimeConfiguration noAccessBefore, TimeConfiguration noAccessAfter,
                                   TimeZoneDisplay timezone, Clock clock)
        {
            ZoneId zoneId = ZoneIdUtil.getZoneId(timezone);
            LocalDate localDate = LocalDate.now(clock);
            Instant startTime = LocalDateTime.of(localDate,
                    LocalTime.of(noAccessBefore.getHour(), noAccessBefore.getMinute()))
                    .atZone(zoneId)
                    .toInstant();
            Instant endTime = LocalDateTime.of(localDate,
                    LocalTime.of(noAccessAfter.getHour(), noAccessAfter.getMinute()))
                    .atZone(zoneId)
                    .toInstant();
            Instant now = Instant.now(clock);

            return new TimeComparer(startTime, endTime, now, zoneId);
        }

        public boolean isNowAfterStartTime()
        {
            return _now.isAfter(_startTime) ||  _now.equals(_startTime);
        }

        public boolean isNowBeforeEndTime()
        {
            return _now.isBefore(_endTime) || _now.equals(_endTime);
        }

        public Instant getNow()
        {
            return _now;
        }

        public Instant getEndTime()
        {
            return _endTime;
        }

        public Instant getStartTime()
        {
            return _startTime;
        }

        public ZoneId getZoneId()
        {
            return _zoneId;
        }
    }
}