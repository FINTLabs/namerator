package no.fintlabs.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fintlabs.NamOAuthClientApplicationSpec;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthClientApplication {

    @JsonProperty("grant_types")
    private List<String> grantTypes;

    @JsonProperty("application_type")
    private String applicationType;

    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("response_types")
    private List<String> responseTypes;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("corsdomains")
    private List<String> corsDomains;

    @JsonProperty("id_token_signed_response_alg")
    private String idTokenSignedResponseAlg;

    @JsonProperty("registration_client_uri")
    private String registrationClientUri;

    public boolean needsUpdate(NamOAuthClientApplicationSpec spec) {
        return !(idTokenSignedResponseAlg.equals(spec.getIdTokenSignedResponseAlg())
                && grantTypes.equals(spec.getGrantTypes())
                && redirectUris.equals(spec.getRedirectUris())
                && responseTypes.equals(spec.getResponseTypes())
                && corsDomains.equals(spec.getCorsDomains()));
    }

    public void update(NamOAuthClientApplicationSpec spec) {
        idTokenSignedResponseAlg = spec.getIdTokenSignedResponseAlg();
        grantTypes = spec.getGrantTypes();
        redirectUris = spec.getRedirectUris();
        responseTypes = spec.getResponseTypes();
        corsDomains = spec.getCorsDomains();

    }
}