package com.smartlogi.sdms.dto.auth;

import java.util.Map;


@SuppressWarnings("unchecked")
public class FacebookOAuth2UserInfo extends OAuth2UserInfo {

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
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
        if (attributes.containsKey("picture")) {
            Object pictureObj = attributes.get("picture");
            if (pictureObj instanceof Map) {
                Map<String, Object> pictureData = (Map<String, Object>) pictureObj;
                if (pictureData.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) pictureData.get("data");
                    if (data.containsKey("url")) {
                        return (String) data.get("url");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getFirstName() {
        String firstName = (String) attributes.get("first_name");
        return firstName != null ? firstName : super.getFirstName();
    }

    @Override
    public String getLastName() {
        String lastName = (String) attributes.get("last_name");
        return lastName != null ? lastName : super.getLastName();
    }
}

