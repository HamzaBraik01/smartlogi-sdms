package com.smartlogi.sdms.dto.auth;

import java.util.Map;


@SuppressWarnings("unchecked")
public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        String firstName = getFirstName();
        String lastName = getLastName();

        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        }
        return getEmail();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public String getFirstName() {
        if (attributes.containsKey("name")) {
            Object nameObj = attributes.get("name");
            if (nameObj instanceof Map) {
                Map<String, Object> nameData = (Map<String, Object>) nameObj;
                return (String) nameData.get("firstName");
            }
        }
        return null;
    }

    @Override
    public String getLastName() {
        if (attributes.containsKey("name")) {
            Object nameObj = attributes.get("name");
            if (nameObj instanceof Map) {
                Map<String, Object> nameData = (Map<String, Object>) nameObj;
                return (String) nameData.get("lastName");
            }
        }
        return null;
    }
}

