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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler;
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult;
import se.curity.identityserver.sdk.authenticationaction.completions.IntermediateAuthenticationState;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.util.Optional;

import static io.curity.identityserver.plugin.authenticationaction.time.deny.TimeDenyAuthenticationAction.SESSION_KEY;
import static se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult.complete;


public class DateDenyRequestHandler implements ActionCompletionRequestHandler<Request>
{
    private final IntermediateAuthenticationState _intermediateAuthenticationState;
    private final SessionManager _sessionManager;
//    private final Json _json;

    private final static Logger _logger = LoggerFactory.getLogger(DateDenyRequestHandler.class);


    public DateDenyRequestHandler(IntermediateAuthenticationState intermediateAuthenticationState,
                                  DateDenyAuthenticationActionConfiguration configuration)
    {
        _intermediateAuthenticationState = intermediateAuthenticationState;
        _sessionManager = configuration.getSessionManager();
    }

    @Override
    public Optional<ActionCompletionResult> get(Request request, Response response)
    {
        _sessionManager.put(Attribute.of(SESSION_KEY, true));

        return Optional.of(complete());
    }


    @Override
    public Optional<ActionCompletionResult> post(Request request, Response response)
    {
        _sessionManager.put(Attribute.of(SESSION_KEY, true));

        return Optional.of(complete());
    }

    @Override
    public Request preProcess(Request request, Response response)
    {
        return request;
    }
}
