
package com.clevercloud.api;

import com.github.scribejava.core.builder.api.DefaultApi10a;

public class OauthApi extends DefaultApi10a {

    private static final String BASE_URL = "https://api.clever-cloud.com/v2/oauth";

    // Request token endpoint
    @Override
    public String getRequestTokenEndpoint() {
        return BASE_URL + "/request_token";
    }

    // Access token endpoint
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "/access_token";
    }

    // Authorization URL where the user approves the app
    @Override
    public String getAuthorizationBaseUrl() {
        return BASE_URL + "/authorize";
    }

    // Singleton instance (Optional but common practice)
    private static class InstanceHolder {
        private static final OauthApi INSTANCE = new OauthApi();
    }

    public static OauthApi instance() {
        return InstanceHolder.INSTANCE;
    }
}