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

import se.curity.identityserver.sdk.config.annotation.DefaultInteger;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.config.annotation.RangeConstraint;

public interface TimeConfiguration
{
    @Description("Hour at which access should be allowed or denied")
    @DefaultInteger(0)
    @RangeConstraint(min = 0, max = 23)
    int getHour();

    @Description("Minute at which access should be allowed or denied")
    @DefaultInteger(0)
    @RangeConstraint(min = 0, max = 59)
    int getMinute();
}
