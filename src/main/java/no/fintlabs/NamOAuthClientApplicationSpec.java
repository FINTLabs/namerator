package no.fintlabs;

import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NamOAuthClientApplicationSpec {
    //private String applicationName;
    @Builder.Default
    private List<String> grantTypes = Arrays.asList("client_credentials", "refresh_token");

    @Builder.Default
    private List<String> redirectUris = Collections.singletonList("https://dummy.com");
    @Builder.Default
    private List<String> corsDomains = Collections.emptyList();
    @Builder.Default
    private List<String> responseTypes = Arrays.asList(	"code", "id_token", "token");
    @Builder.Default
    private String idTokenSignedResponseAlg = "RS256";

    @Builder.Default
    private String clientIdProperty = "spring.security.oauth2.client.registration.fint.clientId";

    @Builder.Default
    private String clientSecretProperty = "spring.security.oauth2.client.registration.fint.clientSecret";
}
