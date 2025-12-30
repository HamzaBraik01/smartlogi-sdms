package com.smartlogi.sdms.security;

import com.smartlogi.sdms.dto.auth.*;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.exception.OAuth2AuthenticationProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour les classes OAuth2.
 */
@DisplayName("Tests OAuth2")
class OAuth2UserInfoTest {

    @Nested
    @DisplayName("GoogleOAuth2UserInfo")
    class GoogleTests {

        @Test
        @DisplayName("Devrait extraire correctement les informations Google")
        void shouldExtractGoogleUserInfo() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "123456789");
            attributes.put("email", "user@gmail.com");
            attributes.put("name", "John Doe");
            attributes.put("given_name", "John");
            attributes.put("family_name", "Doe");
            attributes.put("picture", "https://lh3.googleusercontent.com/photo.jpg");
            attributes.put("email_verified", true);

            // When
            GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getId()).isEqualTo("123456789");
            assertThat(userInfo.getEmail()).isEqualTo("user@gmail.com");
            assertThat(userInfo.getName()).isEqualTo("John Doe");
            assertThat(userInfo.getFirstName()).isEqualTo("John");
            assertThat(userInfo.getLastName()).isEqualTo("Doe");
            assertThat(userInfo.getImageUrl()).isEqualTo("https://lh3.googleusercontent.com/photo.jpg");
            assertThat(userInfo.isEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("Devrait gérer les valeurs nulles Google")
        void shouldHandleNullValuesGoogle() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "123");
            attributes.put("email", "test@gmail.com");

            // When
            GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getId()).isEqualTo("123");
            assertThat(userInfo.getEmail()).isEqualTo("test@gmail.com");
            assertThat(userInfo.getName()).isNull();
            assertThat(userInfo.getImageUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("FacebookOAuth2UserInfo")
    class FacebookTests {

        @Test
        @DisplayName("Devrait extraire correctement les informations Facebook")
        void shouldExtractFacebookUserInfo() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "fb123456");
            attributes.put("email", "user@facebook.com");
            attributes.put("name", "Jane Smith");
            attributes.put("first_name", "Jane");
            attributes.put("last_name", "Smith");

            Map<String, Object> pictureData = new HashMap<>();
            pictureData.put("url", "https://graph.facebook.com/photo.jpg");
            Map<String, Object> picture = new HashMap<>();
            picture.put("data", pictureData);
            attributes.put("picture", picture);

            // When
            FacebookOAuth2UserInfo userInfo = new FacebookOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getId()).isEqualTo("fb123456");
            assertThat(userInfo.getEmail()).isEqualTo("user@facebook.com");
            assertThat(userInfo.getName()).isEqualTo("Jane Smith");
            assertThat(userInfo.getFirstName()).isEqualTo("Jane");
            assertThat(userInfo.getLastName()).isEqualTo("Smith");
            assertThat(userInfo.getImageUrl()).isEqualTo("https://graph.facebook.com/photo.jpg");
        }

        @Test
        @DisplayName("Devrait gérer l'absence de photo Facebook")
        void shouldHandleNoPictureFacebook() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "fb123");
            attributes.put("email", "test@facebook.com");

            // When
            FacebookOAuth2UserInfo userInfo = new FacebookOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getImageUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("AppleOAuth2UserInfo")
    class AppleTests {

        @Test
        @DisplayName("Devrait extraire correctement les informations Apple")
        void shouldExtractAppleUserInfo() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "apple.user.123");
            attributes.put("email", "user@privaterelay.appleid.com");

            Map<String, Object> name = new HashMap<>();
            name.put("firstName", "Alice");
            name.put("lastName", "Johnson");
            attributes.put("name", name);

            // When
            AppleOAuth2UserInfo userInfo = new AppleOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getId()).isEqualTo("apple.user.123");
            assertThat(userInfo.getEmail()).isEqualTo("user@privaterelay.appleid.com");
            assertThat(userInfo.getFirstName()).isEqualTo("Alice");
            assertThat(userInfo.getLastName()).isEqualTo("Johnson");
            assertThat(userInfo.getName()).isEqualTo("Alice Johnson");
            // Apple ne fournit pas d'image
            assertThat(userInfo.getImageUrl()).isNull();
        }

        @Test
        @DisplayName("Devrait utiliser email comme nom si pas de nom fourni")
        void shouldUseEmailAsNameWhenNoName() {
            // Given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "apple.123");
            attributes.put("email", "hidden@privaterelay.appleid.com");

            // When
            AppleOAuth2UserInfo userInfo = new AppleOAuth2UserInfo(attributes);

            // Then
            assertThat(userInfo.getName()).isEqualTo("hidden@privaterelay.appleid.com");
        }
    }

    @Nested
    @DisplayName("OAuth2UserInfoFactory")
    class FactoryTests {

        @Test
        @DisplayName("Devrait créer GoogleOAuth2UserInfo pour google")
        void shouldCreateGoogleUserInfo() {
            // Given
            Map<String, Object> attributes = Map.of("sub", "123", "email", "test@gmail.com");

            // When
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes);

            // Then
            assertThat(userInfo).isInstanceOf(GoogleOAuth2UserInfo.class);
        }

        @Test
        @DisplayName("Devrait créer FacebookOAuth2UserInfo pour facebook")
        void shouldCreateFacebookUserInfo() {
            // Given
            Map<String, Object> attributes = Map.of("id", "123", "email", "test@facebook.com");

            // When
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("facebook", attributes);

            // Then
            assertThat(userInfo).isInstanceOf(FacebookOAuth2UserInfo.class);
        }

        @Test
        @DisplayName("Devrait créer AppleOAuth2UserInfo pour apple")
        void shouldCreateAppleUserInfo() {
            // Given
            Map<String, Object> attributes = Map.of("sub", "123", "email", "test@apple.com");

            // When
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("apple", attributes);

            // Then
            assertThat(userInfo).isInstanceOf(AppleOAuth2UserInfo.class);
        }

        @Test
        @DisplayName("Devrait lancer une exception pour un provider non supporté")
        void shouldThrowExceptionForUnsupportedProvider() {
            // Given
            Map<String, Object> attributes = Map.of("id", "123");

            // When/Then
            assertThatThrownBy(() ->
                OAuth2UserInfoFactory.getOAuth2UserInfo("unsupported", attributes))
                .isInstanceOf(OAuth2AuthenticationProcessingException.class)
                .hasMessageContaining("non supporté");
        }

        @Test
        @DisplayName("Devrait lancer une exception pour LOCAL")
        void shouldThrowExceptionForLocal() {
            // Given
            Map<String, Object> attributes = Map.of("id", "123");

            // When/Then
            assertThatThrownBy(() ->
                OAuth2UserInfoFactory.getOAuth2UserInfo("local", attributes))
                .isInstanceOf(OAuth2AuthenticationProcessingException.class)
                .hasMessageContaining("LOCAL ne supporte pas OAuth2");
        }

        @Test
        @DisplayName("Devrait être insensible à la casse")
        void shouldBeCaseInsensitive() {
            // Given
            Map<String, Object> attributes = Map.of("sub", "123", "email", "test@gmail.com");

            // When
            OAuth2UserInfo userInfo1 = OAuth2UserInfoFactory.getOAuth2UserInfo("GOOGLE", attributes);
            OAuth2UserInfo userInfo2 = OAuth2UserInfoFactory.getOAuth2UserInfo("Google", attributes);
            OAuth2UserInfo userInfo3 = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes);

            // Then
            assertThat(userInfo1).isInstanceOf(GoogleOAuth2UserInfo.class);
            assertThat(userInfo2).isInstanceOf(GoogleOAuth2UserInfo.class);
            assertThat(userInfo3).isInstanceOf(GoogleOAuth2UserInfo.class);
        }

        @Test
        @DisplayName("Devrait retourner le bon AuthProvider")
        void shouldReturnCorrectAuthProvider() {
            assertThat(OAuth2UserInfoFactory.getAuthProvider("google")).isEqualTo(AuthProvider.GOOGLE);
            assertThat(OAuth2UserInfoFactory.getAuthProvider("FACEBOOK")).isEqualTo(AuthProvider.FACEBOOK);
            assertThat(OAuth2UserInfoFactory.getAuthProvider("Apple")).isEqualTo(AuthProvider.APPLE);
            assertThat(OAuth2UserInfoFactory.getAuthProvider("okta")).isEqualTo(AuthProvider.OKTA);
        }
    }
}

