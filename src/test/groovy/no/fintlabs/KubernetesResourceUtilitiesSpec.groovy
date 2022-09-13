package no.fintlabs

import no.fintlabs.oauth.OAuthClientApplication
import spock.lang.Specification

import static no.fintlabs.KubernetesResourceUtilities.*

class KubernetesResourceUtilitiesSpec extends Specification {

    def "Calling addClientIdToAnnotation should add annotation with clientId as value"() {
        given:
        def resource = new NamOAuthClientApplicationResource()
        def oAuthClientApplication = OAuthClientApplication.builder()
                .clientId(UUID.randomUUID().toString())
                .build()
        when:
        addClientIdToAnnotation(resource, oAuthClientApplication)
        then:
        resource.getMetadata().getAnnotations().get(ANNOTATION_CLIENT_ID) == oAuthClientApplication.getClientId()
    }

    def "Calling getClientIdFromAnnotation should return clientId from annotations"() {
        given:
        def resource = new NamOAuthClientApplicationResource()
        def oAuthClientApplication = OAuthClientApplication.builder()
                .clientId(UUID.randomUUID().toString())
                .build()
        addClientIdToAnnotation(resource, oAuthClientApplication)

        when:
        def clientIdFromAnnotation = getClientIdFromAnnotation(resource)

        then:
        clientIdFromAnnotation.isPresent()
        clientIdFromAnnotation.get() == oAuthClientApplication.getClientId()
    }

    def "Calling updateStatusObjectOnCreate should set registration client uri to status object"() {
        given:
        def resource = new NamOAuthClientApplicationResource()
        def oAuthClientApplication = OAuthClientApplication.builder()
                .registrationClientUri("https://test.no")
                .build()
        when:
        updateStatusObjectOnCreate(resource, oAuthClientApplication)

        then:
        resource.getStatus().getClientUri() == oAuthClientApplication.getRegistrationClientUri()
    }
}
