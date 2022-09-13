package no.fintlabs;

import no.fintlabs.oauth.OAuthClientApplication;

import java.util.Optional;

public class KubernetesResourceUtilities {

    public static final String ANNOTATION_CLIENT_ID = "fintlabs.no/client-id";

    public static void addClientIdToAnnotation(NamOAuthClientApplicationResource resource, OAuthClientApplication oAuthClientApplication) {
        resource
                .getMetadata()
                .getAnnotations()
                .put(ANNOTATION_CLIENT_ID, oAuthClientApplication.getClientId());
    }

    public static Optional<String> getClientIdFromAnnotation(NamOAuthClientApplicationResource resource) {
        return Optional.ofNullable(resource.getMetadata().getAnnotations().get(ANNOTATION_CLIENT_ID));
    }

    public static void updateStatusObjectOnCreate(NamOAuthClientApplicationResource namOAuthClientApplicationResource, OAuthClientApplication oAuthClientApplication) {
        NamOAuthClientApplicationStatus status = namOAuthClientApplicationResource
                .getStatus();
        status.setClientUri(oAuthClientApplication.getRegistrationClientUri());
        status.setErrorMessage(null);
    }
}
