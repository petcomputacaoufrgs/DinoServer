package br.ufrgs.inf.pet.dinoapi.model.auth.google.auth;

import br.ufrgs.inf.pet.dinoapi.model.auth.AuthResponseDataModel;
import br.ufrgs.inf.pet.dinoapi.model.auth.google.GoogleScopeDataModel;
import java.time.ZonedDateTime;
import java.util.List;

public class GoogleAuthResponseDataModel extends AuthResponseDataModel {
    private String googleAccessToken;

    private ZonedDateTime googleExpiresDate;

    private List<GoogleScopeDataModel> scopes;

    public String getGoogleAccessToken() {
        return googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public ZonedDateTime getGoogleExpiresDate() {
        return googleExpiresDate;
    }

    public void setGoogleExpiresDate(ZonedDateTime googleExpiresDate) {
        this.googleExpiresDate = googleExpiresDate;
    }

    public List<GoogleScopeDataModel> getScopes() {
        return scopes;
    }

    public void setScopes(List<GoogleScopeDataModel> scopes) {
        this.scopes = scopes;
    }
}