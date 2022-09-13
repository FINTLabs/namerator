package no.fintlabs.oauth;

import io.netty.channel.ChannelOption;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "fint.client")
public class OAuthConfiguration {

    private String baseUrl;
    private String username;
    private String password;
    private String registrationId;

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .password()
                .refreshToken()
                .build();

        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
        );

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        authorizedClientManager.setContextAttributesMapper(oAuth2AuthorizeRequest -> Mono.just(Map.of(
                OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username,
                OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password
        )));

        return authorizedClientManager;
    }

    @Bean
    public ClientHttpConnector clientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create(
                        ConnectionProvider
                                .builder("laidback")
                                .maxConnections(100)
                                .maxLifeTime(Duration.ofMinutes(30))
                                .maxIdleTime(Duration.ofMinutes(5))
                                .build())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 600000)
                .responseTimeout(Duration.ofMinutes(10))
        );
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder, ReactiveOAuth2AuthorizedClientManager authorizedClientManager, ClientHttpConnector clientHttpConnector) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(registrationId);

        return builder
                .clientConnector(clientHttpConnector)
                .exchangeStrategies(exchangeStrategies)
                //.filter(logRequest())
                //.filter(logResponse())
                .filter(oauth2Client)
                .baseUrl(baseUrl)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            //log.debug("Response Status: {}", clientResponse.statusCode());
            logStatus(clientResponse);
            logHeaders(clientResponse);
            return logBody(clientResponse);//Mono.just(clientResponse);
        });
    }

    private  Mono<ClientResponse> logBody(ClientResponse response) {
        if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.debug("Body is {}", body);
                        return Mono.just(response);
                    });
        } else {
            return Mono.just(response);
        }
    }

    private  void logStatus(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.debug("Returned staus code {} ({})", status.value(), status.getReasonPhrase());
    }

    private static void logHeaders(ClientResponse response) {
        log.debug("Headers:");
        response.headers().asHttpHeaders().forEach((name, values) -> {
            values.forEach(value -> {
                log.debug("     {}: {}", name, value);
            });
        });
    }
}