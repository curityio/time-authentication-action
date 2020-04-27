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
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticatedSessions;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult;
import se.curity.identityserver.sdk.service.authenticationaction.AuthenticatorDescriptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static io.curity.identityserver.plugin.authenticationaction.date_time.ZoneIdUtil.getZoneId;

public final class DateDenyAuthenticationAction implements AuthenticationAction
{
    private final static Logger _logger = LoggerFactory.getLogger(DateDenyAuthenticationAction.class);

    private final DateDenyAuthenticationActionConfiguration _configuration;

    public DateDenyAuthenticationAction(DateDenyAuthenticationActionConfiguration configuration)
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

        ZonedDateTime configuredDateTime = LocalDateTime.of(
                LocalDate.of(_configuration.getYear(), _configuration.getMonth(), _configuration.getDay()),
                LocalTime.of(_configuration.getHour(), _configuration.getMinute()))
                .atZone(zoneId);

        ZonedDateTime currentDateTimeInZone = ZonedDateTime.now(zoneId);

        if (currentDateTimeInZone.isBefore(configuredDateTime) &&
                _configuration.getDenyBeforeOrAfter() == DateDenyAuthenticationActionConfiguration.When.BEFORE &&
                _logger.isDebugEnabled())
        {
            _logger.debug("Access denied because current date {} is before date allowed by configuration {}",
                    currentDateTimeInZone, configuredDateTime);
        }
        else if (currentDateTimeInZone.isAfter(configuredDateTime) &&
                _configuration.getDenyBeforeOrAfter() == DateDenyAuthenticationActionConfiguration.When.AFTER &&
                _logger.isDebugEnabled())
        {
            _logger.debug("Access denied because current date {} is after date allowed by configuration {}",
                    currentDateTimeInZone, configuredDateTime);
        }
        else
        {
            _logger.trace("Access allowed based on date and time");

            return AuthenticationActionResult.successfulResult(authenticationAttributes);
        }

        return AuthenticationActionResult.failedResult("Access denied based on date and time");
    }
}