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
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticatedSessions;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.authenticationaction.AuthenticatorDescriptor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public final class TimeDenyAuthenticationAction extends AbstractDateTimeAuthenticationAction implements AuthenticationAction
{
    private final static Logger _logger = LoggerFactory.getLogger(TimeDenyAuthenticationAction.class);

    private final TimeDenyAuthenticationActionConfiguration _configuration;

    public TimeDenyAuthenticationAction(TimeDenyAuthenticationActionConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public AuthenticationActionResult apply(AuthenticationAttributes authenticationAttributes,
                                            AuthenticatedSessions authenticatedSessions,
                                            String authenticationTransactionId,
                                            AuthenticatorDescriptor authenticatorDescriptor)
    {
        ZoneId zoneId = getZoneId(_configuration.getTimeZone());
        TimeConfiguration noAccessBefore = _configuration.getNoAccessBefore();
        LocalDate localDate = LocalDate.now();
        Instant startTime = LocalDateTime.of(localDate,
                LocalTime.of(noAccessBefore.getHour(), noAccessBefore.getMinute()))
                .atZone(zoneId)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toInstant();
        Instant now = Instant.now();

        if (now.isAfter(startTime))
        {
            TimeConfiguration noAccessAfter = _configuration.getNoAccessAfter();
            Instant endTime = LocalDateTime.of(localDate,
                    LocalTime.of(noAccessAfter.getHour(), noAccessAfter.getMinute()))
                    .atZone(zoneId)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toInstant();

            if (now.isBefore(endTime))
            {
                _logger.debug("Access allowed based on time");

                return AuthenticationActionResult.successfulResult(authenticationAttributes);
            }
            else if (_logger.isDebugEnabled())
            {
                _logger.debug(
                        "Access denied because current time {} in timezone {} is after the configured allowed end time {}",
                        now.atZone(zoneId), zoneId, endTime.atZone(zoneId));
            }
        }
        else if (_logger.isDebugEnabled())
        {
            _logger.debug(
                    "Access denied because current time {} in timezone {} is after the configured allowed start time {}",
                    now.atZone(zoneId), zoneId, startTime.atZone(zoneId));
        }

        return AuthenticationActionResult.failedResult("Access denied based on time");
    }
}