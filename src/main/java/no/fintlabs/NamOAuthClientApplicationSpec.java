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
    private List<String> grantTypes = Arrays.asList("authorization_code", "refresh_token");

    @Builder.Default
    private List<String> redirectUris = Collections.emptyList();
    @Builder.Default
    private List<String> corsDomains = Collections.emptyList();
    @Builder.Default
    private List<String> responseTypes = Arrays.asList(	"code", "id_token", "token");
    @Builder.Default
    private String idTokenSignedResponseAlg = "RS256";

    @Builder.Default
    private String clientIdProperty = "fint.sso.client-id";

    @Builder.Default
    private String clientSecretProperty = "fint.sso.client-secret";
}
