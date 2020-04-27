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

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultEnum;
import se.curity.identityserver.sdk.config.annotation.DefaultInteger;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.config.annotation.RangeConstraint;

public interface DateDenyAuthenticationActionConfiguration extends Configuration
{
    @DefaultEnum("SYSTEM_TIME")
    @Description("Choose the time zone where the deny action should apply. System time will use time zone of the server.")
    TimeZoneDisplay getTimeZone();

    @Description("Year in which access should be allowed or denied")
    @RangeConstraint(min = 1970, max = 2100)
    int getYear();

    @Description("Month (one for January, etc.) in which access should be allowed or denied")
    @RangeConstraint(min = 1, max = 12)
    int getMonth();

    @Description("Day (one for the first, etc.) in which access should be allowed or denied")
    @RangeConstraint(min = 1, max = 31)
    int getDay();

    @Description("Hour at which access should be allowed or denied")
    @DefaultInteger(0)
    @RangeConstraint(min = 0, max = 24)
    int getHour();

    @Description("Minute at which access should be allowed or denied")
    @DefaultInteger(0)
    @RangeConstraint(min = 0, max = 60)
    int getMinute();

    @Description("Choose if access should be denied before or after the configured date")
    @DefaultEnum("BEFORE")
    When getDenyBeforeOrAfter();

    enum When
    {
        BEFORE,
        AFTER
    }
}
