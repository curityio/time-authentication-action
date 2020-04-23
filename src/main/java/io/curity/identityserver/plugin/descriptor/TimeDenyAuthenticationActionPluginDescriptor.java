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

package io.curity.identityserver.plugin.descriptor;

import io.curity.identityserver.plugin.authenticationaction.time.deny.TimeDenyAuthenticationAction;
import io.curity.identityserver.plugin.authenticationaction.time.deny.TimeDenyAuthenticationActionConfiguration;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticationActionPluginDescriptor;


public class TimeDenyAuthenticationActionPluginDescriptor
        implements AuthenticationActionPluginDescriptor<TimeDenyAuthenticationActionConfiguration>
{
    @Override
    public Class<? extends AuthenticationAction> getAuthenticationAction()
    {
        return TimeDenyAuthenticationAction.class;
    }

    @Override
    public String getPluginImplementationType()
    {
        return "time-deny";
    }

    @Override
    public Class<? extends TimeDenyAuthenticationActionConfiguration> getConfigurationType()
    {
        return TimeDenyAuthenticationActionConfiguration.class;
    }
}
