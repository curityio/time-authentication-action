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

import java.time.ZoneId;
import java.util.TimeZone;

public class AbstractDateTimeAuthenticationAction
{
    private final static Logger _logger = LoggerFactory.getLogger(AbstractDateTimeAuthenticationAction.class);

    ZoneId getZoneId(TimeZoneDisplay configTimeZone)
    {
        ZoneId zoneId;

        if (configTimeZone == TimeZoneDisplay.SYSTEM_TIME)
        {
            zoneId = TimeZone.getDefault().toZoneId();

            _logger.debug("Using server system time");
        }
        else
        {
            String timeZone = configTimeZone.getTimeZone();
            zoneId = TimeZone.getTimeZone(timeZone).toZoneId();

            _logger.debug("Using configured timezone {}", timeZone);
        }

        return zoneId;
    }
}
