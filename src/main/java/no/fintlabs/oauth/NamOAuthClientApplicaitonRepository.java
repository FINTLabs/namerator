package no.fintlabs.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Repository
@ConditionalOnProperty(prefix = "fint.operator", name = "test-mode", havingValue = "false", matchIfMissing = true)
public class NamOAuthClientApplicaitonRepository implements OidcRepository {

    private final WebClient webClient;

    private final Map<String, OAuthClientApplication> oAuthClientApplications;

    private final ObjectMapper objectMapper;

    public NamOAuthClientApplicaitonRepository(WebClient webClient, ObjectMapper objectMapper) {

        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.oAuthClientApplications = new HashMap<>();
    }

    @PostConstruct
    public void init() {

        log.debug("Starting {}", this.getClass().getSimpleName());
        refreshClients();
    }

    private void refreshClients() {
        Object o = Objects.requireNonNull(getClients()
                .block());

        if (o instanceof List<?>) {
            Arrays.asList(objectMapper.convertValue(o, OAuthClientApplication[].class))
                    .forEach(oAuthClientApplication ->
                            oAuthClientApplications
                                    .put(oAuthClientApplication.getClientId(), oAuthClientApplication)
                    );
        } else {
            oAuthClientApplications.clear();
        }


    }

    private Mono<Object> getClients() {
        return webClient.get().uri("/nidp/oauth/nam/clients/")
                .retrieve()
                .bodyToMono(Object.class);
    }

    public OAuthClientApplication addClient(OAuthClientApplication oAuthClientApplication) {
        OAuthClientApplication createdOAuthClientApplication = webClient.post().uri("/nidp/oauth/nam/clients/")
                .body(Mono.just(oAuthClientApplication),
                        OAuthClientApplication.class)
                .retrieve()
                .bodyToMono(OAuthClientApplication.class)
                .block();

        refreshClients();

        return createdOAuthClientApplication;
    }

    public OAuthClientApplication updateClient(OAuthClientApplication oAuthClientApplication) {
        OAuthClientApplication createdOAuthClientApplication = webClient.post().uri("/nidp/oauth/nam/clients/" + oAuthClientApplication.getClientId())
                .body(Mono.just(oAuthClientApplication),
                        OAuthClientApplication.class)
                .retrieve()
                .bodyToMono(OAuthClientApplication.class)
                .block();

        refreshClients();

        return createdOAuthClientApplication;
    }

    public OAuthClientApplication getClientById(String clientId) {
        return oAuthClientApplications.get(clientId);
    }


    public void deleteClientById(String clientId) {
        webClient
                .delete()
                .uri("/nidp/oauth/nam/clients/" + clientId)
                .retrieve()
                .bodyToMono(OAuthClientApplication.class)
                .block();

        refreshClients();
    }
}
