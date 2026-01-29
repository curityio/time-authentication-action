/*
 * Copyright (C) 2025 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
 */

package io.curity.identityserver.plugin.authenticationaction.date_time;

import se.curity.identityserver.sdk.attribute.AuthenticationActionAttributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticatedSessions;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionContext;
import se.curity.identityserver.sdk.service.authenticationaction.AuthenticatorDescriptor;
import se.curity.identityserver.sdk.service.authentication.TenantId;

public class MockAuthenticationActionContext implements AuthenticationActionContext {
    public static MockAuthenticationActionContext createMockAuthenticationActionContext() {
        return new MockAuthenticationActionContext();
    }

    public MockAuthenticationActionContext() {}

    @Override
    public AuthenticationAttributes getAuthenticationAttributes() {
        return null;
    }

    @Override
    public AuthenticationActionAttributes getActionAttributes() {
        return null;
    }

    @Override
    public AuthenticatedSessions getAuthenticatedSessions() {
        return null;
    }

    @Override
    public String getAuthenticationTransactionId() {
        return "";
    }

    @Override
    public AuthenticatorDescriptor getAuthenticatorDescriptor() {
        return null;
    }

    @Override
    public TenantId getTenantId() {
        return null;
    }
}
