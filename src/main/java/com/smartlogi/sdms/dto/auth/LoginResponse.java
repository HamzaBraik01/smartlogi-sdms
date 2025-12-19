package com.smartlogi.sdms.dto.auth;

import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;


public class LoginResponse {

    private String token;
    private String type;
    private long expiresIn;
    private String userId;
    private String email;
    private String nom;
    private String prenom;
    private RoleUtilisateur role;

    public LoginResponse() {
        this.type = "Bearer";
    }

    public LoginResponse(String token, long expiresIn, String userId, String email,
                         String nom, String prenom, RoleUtilisateur role) {
        this.token = token;
        this.type = "Bearer";
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    // Builder pattern pour une construction fluide
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private long expiresIn;
        private String userId;
        private String email;
        private String nom;
        private String prenom;
        private RoleUtilisateur role;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public Builder role(RoleUtilisateur role) {
            this.role = role;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(token, expiresIn, userId, email, nom, prenom, role);
        }
    }

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }
}

