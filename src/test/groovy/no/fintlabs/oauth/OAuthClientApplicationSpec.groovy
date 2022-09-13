package no.fintlabs.oauth

import no.fintlabs.NamOAuthClientApplicationSpec
import spock.lang.Specification

class OAuthClientApplicationSpec extends Specification {

    def "If client and spec is equal we don't need to update"() {
        given:
        def oAuthClientApplication = OAuthClientApplication.builder()
                .corsDomains(Arrays.asList("test.no"))
                .grantTypes(Arrays.asList("client_credentials", "refresh_token"))
                .idTokenSignedResponseAlg("RS256")
                .responseTypes(Arrays.asList("id_token"))
                .redirectUris(Arrays.asList("test.no"))
                .build()
        def namOAuthClientApplicationSpec = NamOAuthClientApplicationSpec.builder()
                .corsDomains(Arrays.asList("test.no"))
                .grantTypes(Arrays.asList("client_credentials", "refresh_token"))
                .idTokenSignedResponseAlg("RS256")
                .responseTypes(Arrays.asList("id_token"))
                .redirectUris(Arrays.asList("test.no"))
                .build()

        when:
        def needsUpdate = oAuthClientApplication.needsUpdate(namOAuthClientApplicationSpec)

        then:
        needsUpdate
    }

    def "If client and spec is not equal we need to update"() {
        given:
        def oAuthClientApplication = OAuthClientApplication.builder()
                .corsDomains(Arrays.asList("test.no"))
                .grantTypes(Arrays.asList("client_credentials", "refresh_token"))
                .idTokenSignedResponseAlg("RS256")
                .responseTypes(Arrays.asList("id_token"))
                .redirectUris(Arrays.asList("test.no"))
                .build()
        def namOAuthClientApplicationSpec = NamOAuthClientApplicationSpec
                .builder()
                .build()

        when:
        def needsUpdate = oAuthClientApplication.needsUpdate(namOAuthClientApplicationSpec)

        then:
        !needsUpdate
    }
}
