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
package io.curity.identityserver.plugin.authenticationaction.date.deny;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultEnum;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import io.curity.identityserver.plugin.authenticationaction.util.TimeZoneDisplay;

public interface DateDenyAuthenticationActionConfiguration extends Configuration
{
    @DefaultEnum("SYSTEM_TIME")
    @Description("Choose the Time Zone where the deny action should apply. System time will use time zone of the server.")
    TimeZoneDisplay getTimeZone();

    String timeRegExp = "([0-1]?[0-9]|2[0-3]):[0-5][0-9]"; //24h times i default

    @Description("Deny access before/after this time (24h time format)")
    String getDateAndTime();

    @Description("Choose if access should be denied before or after the configured date")
    @DefaultEnum("BEFORE")
    when getDenyBeforeOrAfter();

    SessionManager getSessionManager();

    Json getJson();

}

enum when{
    BEFORE,
    AFTER
}