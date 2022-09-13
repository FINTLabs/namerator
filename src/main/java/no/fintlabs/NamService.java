package no.fintlabs;

import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.oauth.OAuthClientApplication;
import no.fintlabs.oauth.OidcRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class NamService {

    @Value("${fint.operator.nam.oauth-client.name-prefix}")
    private String nameOauthClientNamePrefix;
    private final OidcRepository repository;

    public NamService(OidcRepository repository) {
        this.repository = repository;
    }

    public OAuthClientApplication addClient(NamOAuthClientApplicationResource resource) {
        OAuthClientApplication oAuthClientApplication = OAuthClientApplication.builder()
                .clientName(nameOauthClientNamePrefix + resource.getMetadata().getName() + "_" + uniqId())
                .grantTypes(resource.getSpec().getGrantTypes())
                .redirectUris(resource.getSpec().getRedirectUris())
                .corsDomains(resource.getSpec().getCorsDomains())
                .responseTypes(resource.getSpec().getResponseTypes())
                .idTokenSignedResponseAlg(resource.getSpec().getIdTokenSignedResponseAlg())
                .build();

        log.debug("Adding client {}", oAuthClientApplication);

        return repository.addClient(oAuthClientApplication);
    }

    public void createClientIfNeeded(Optional<String> clientId, NamOAuthClientApplicationResource resource, Consumer<OAuthClientApplication> onAdd) {
        if (clientId.isEmpty()) {
            OAuthClientApplication oAuthClientApplication = addClient(resource);
            onAdd.accept(oAuthClientApplication);
        }
    }

    public UpdateControl<NamOAuthClientApplicationResource> updateClientIfNeeded(String clientId, NamOAuthClientApplicationResource resource, Consumer<OAuthClientApplication> onUpdate) {

        OAuthClientApplication oAuthClientApplication = getClientById(clientId).orElseThrow();

        log.debug("Check if resource specs changed.");
        if (oAuthClientApplication.needsUpdate(resource.getSpec())) {
            log.debug("Resource specs changed so we need to update the OAuth client application.");
            oAuthClientApplication.update(resource.getSpec());
            OAuthClientApplication updatedOAuthClientApplication = repository.updateClient(oAuthClientApplication);
            onUpdate.accept(updatedOAuthClientApplication);
            log.debug("Updated client {}", updatedOAuthClientApplication);
            return UpdateControl.updateStatus(resource);
        }

        log.debug("No need to update OAuth client application!");

        return UpdateControl.noUpdate();
    }


    public Optional<OAuthClientApplication> getClientById(String clientId) {
        return Optional.ofNullable(repository.getClientById(clientId));
    }

    public void deleteClientById(String clientId) {
        repository.deleteClientById(clientId);

        log.debug("Deleted client with id {}", clientId);
    }

    private String uniqId() {
        return RandomStringUtils.randomAlphanumeric(6);
    }
}
