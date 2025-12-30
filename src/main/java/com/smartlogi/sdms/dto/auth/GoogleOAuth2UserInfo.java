package com.smartlogi.sdms.dto.auth;

import java.util.Map;


public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getFirstName() {
        String givenName = (String) attributes.get("given_name");
        return givenName != null ? givenName : super.getFirstName();
    }

    @Override
    public String getLastName() {
        String familyName = (String) attributes.get("family_name");
        return familyName != null ? familyName : super.getLastName();
    }


    public boolean isEmailVerified() {
        Object verified = attributes.get("email_verified");
        if (verified instanceof Boolean) {
            return (Boolean) verified;
        }
        return "true".equalsIgnoreCase(String.valueOf(verified));
    }
}

