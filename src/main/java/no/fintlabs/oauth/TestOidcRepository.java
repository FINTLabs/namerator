package no.fintlabs.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Repository
@ConditionalOnProperty(prefix = "fint.operator", name = "test-mode", havingValue = "true")
public class TestOidcRepository implements OidcRepository {

    private final Map<String, OAuthClientApplication> clients = new HashMap<>();
    private final ObjectMapper objectMapper;
    private File file;

    public TestOidcRepository() {
        objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {


        log.debug("Starting {}", this.getClass().getSimpleName());

        try {
            file = Paths.get("./clients.json").toFile();
            if (file.exists()) {
                List<OAuthClientApplication> oAuthClientApplications = Arrays.asList(objectMapper.readValue(file, OAuthClientApplication[].class));
                oAuthClientApplications.forEach(oAuthClient -> clients.put(oAuthClient.getClientId(), oAuthClient));
            } else {
                log.debug("Creating client database: {}", file.createNewFile());
                objectMapper.writeValue(file, Collections.emptyList());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OAuthClientApplication addClient(OAuthClientApplication oAuthClientApplication) {

        if (!oAuthClientApplication.getIdTokenSignedResponseAlg().equals("RS256")) {
            throw new RuntimeException("Shit granit!!!!");
        }
        oAuthClientApplication.setClientId(UUID.randomUUID().toString());
        oAuthClientApplication.setClientSecret(RandomStringUtils.randomAlphanumeric(32));
        oAuthClientApplication.setRegistrationClientUri("https://test.no/client/" + oAuthClientApplication.getClientId());
        clients.put(oAuthClientApplication.getClientId(), oAuthClientApplication);

        try {
            objectMapper.writeValue(file, clients.values());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return oAuthClientApplication;
    }

    @Override
    public OAuthClientApplication getClientById(String clientId) {
        return clients.get(clientId);
    }

    @Override
    public void deleteClientById(String clientId) {

        try {
            clients.remove(clientId);
            objectMapper.writeValue(file, clients.values());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OAuthClientApplication updateClient(OAuthClientApplication oAuthClientApplication) {
        clients.put(oAuthClientApplication.getClientId(), oAuthClientApplication);

        try {
            objectMapper.writeValue(file, clients.values());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return oAuthClientApplication;
    }
}
