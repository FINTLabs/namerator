package no.fintlabs.oauth;

public interface OidcRepository {

    OAuthClientApplication addClient(OAuthClientApplication oAuthClientApplication) ;

    OAuthClientApplication getClientById(String clientId);

    void deleteClientById(String clientId);

    OAuthClientApplication updateClient(OAuthClientApplication oAuthClientApplication);
}
