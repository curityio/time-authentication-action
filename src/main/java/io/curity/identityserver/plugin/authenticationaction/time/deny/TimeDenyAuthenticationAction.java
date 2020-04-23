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

package io.curity.identityserver.plugin.authenticationaction.time.deny;

import io.curity.identityserver.plugin.authenticationaction.util.TimeZoneDisplay;
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

import java.time.LocalTime;
import java.util.TimeZone;

public class TimeDenyAuthenticationAction implements AuthenticationAction
{
    private final static Logger _logger = LoggerFactory.getLogger(TimeDenyAuthenticationAction.class);

    public final static String SESSION_KEY = "ATTRIBUTE_VIEW";
    private final SessionManager _sessionManager;
    private final TimeDenyAuthenticationActionConfiguration _configuration;

    public TimeDenyAuthenticationAction(TimeDenyAuthenticationActionConfiguration configuration)
    {
        _sessionManager = configuration.getSessionManager();
        _configuration = configuration;
    }

    @Override
    public AuthenticationActionResult apply(AuthenticationAttributes authenticationAttributes,
                                            AuthenticatedSessions authenticatedSessions,
                                            String authenticationTransactionId,
                                            AuthenticatorDescriptor authenticatorDescriptor)
    {
        @Nullable Attribute attributeView = _sessionManager.get(SESSION_KEY);

        TimeZoneDisplay configTimeZone = _configuration.getTimeZone();

        TimeZone timeZone;
        if(configTimeZone.getTimeZone().equals("SYSTEM_TIME"))
        {
            timeZone = TimeZone.getDefault();
            _logger.debug("Using server system time");
        }
        else
        {
            timeZone = TimeZone.getTimeZone(configTimeZone.getTimeZone());
            _logger.debug("Using configured timezone " + timeZone.getID());
        }

        LocalTime notBeforeLT = convertToLocalTime(_configuration.getNoAccessBefore()); //Convert String config of 'No access before' time to LocalTime
        LocalTime notAfterLT = convertToLocalTime(_configuration.getNoAccessAfter()); //Convert String config of 'No access after' time to LocalTime

        LocalTime timeNowLT = LocalTime.now(timeZone.toZoneId()); //Current time with timezone

        //Check if the current time is between the start and stop time
        Boolean isBetweenStartAndStop = ( ! timeNowLT.isBefore( notBeforeLT ) && timeNowLT.isBefore( notAfterLT ) ) ;

        if(isBetweenStartAndStop)
        {
            _logger.debug("Access allowed based on time based action");
            return AuthenticationActionResult.successfulResult(authenticationAttributes);
        }
        else
        {
            _logger.debug("Access denied based on time based action");
            return AuthenticationActionResult.failedResult("Access denied based on time based action");
        }
    }

    //Convert the String representation of time from the config to a LocalTime object
    private LocalTime convertToLocalTime(String time)
    {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }
}