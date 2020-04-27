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

import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticationActionPluginDescriptor;

public final class DateDenyAuthenticationActionPluginDescriptor
        implements AuthenticationActionPluginDescriptor<DateDenyAuthenticationActionConfiguration>
{
    @Override
    public Class<? extends AuthenticationAction> getAuthenticationAction()
    {
        return DateDenyAuthenticationAction.class;
    }

    @Override
    public String getPluginImplementationType()
    {
        return "date-time-deny";
    }

    @Override
    public Class<? extends DateDenyAuthenticationActionConfiguration> getConfigurationType()
    {
        return DateDenyAuthenticationActionConfiguration.class;
    }
}
