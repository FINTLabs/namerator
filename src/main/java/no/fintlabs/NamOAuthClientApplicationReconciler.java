package no.fintlabs;

import io.fabric8.kubernetes.api.model.Secret;
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static no.fintlabs.KubernetesResourceUtilities.*;

@Slf4j
@Component
@ControllerConfiguration
public class NamOAuthClientApplicationReconciler implements Reconciler<NamOAuthClientApplicationResource>,
        EventSourceInitializer<NamOAuthClientApplicationResource>,
        ErrorStatusHandler<NamOAuthClientApplicationResource>,
        Cleaner<NamOAuthClientApplicationResource> {


    private final NamService namService;
    private final SecretService secretService;


    public NamOAuthClientApplicationReconciler(NamService namService, SecretService secretService) {
        this.namService = namService;
        this.secretService = secretService;
    }

    @Override
    public ErrorStatusUpdateControl<NamOAuthClientApplicationResource> updateErrorStatus(NamOAuthClientApplicationResource resource, Context<NamOAuthClientApplicationResource> context, Exception e) {

        NamOAuthClientApplicationStatus namOAuthClientApplicationStatus = resource.getStatus() ;
        namOAuthClientApplicationStatus.setErrorMessage(getErrorMessage(e));
        resource.setStatus(namOAuthClientApplicationStatus);
        resource.setStatus(namOAuthClientApplicationStatus);
        return ErrorStatusUpdateControl.updateStatus(resource);
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof WebClientResponseException) {
            return ((WebClientResponseException.BadRequest) e).getResponseBodyAsString();
        }
        return e.getMessage();

    }


    @Override
    public UpdateControl<NamOAuthClientApplicationResource> reconcile(NamOAuthClientApplicationResource resource, Context<NamOAuthClientApplicationResource> context) {
        log.debug("Reconciling {}", resource.getMetadata().getName());


        if (getClientIdFromAnnotation(resource).isPresent() && context.getSecondaryResource(Secret.class).isPresent()) {
            log.debug("Client and secret exists for resource {}", resource.getMetadata().getName());

            return namService.updateClientIfNeeded(
                    getClientIdFromAnnotation(resource).get(),
                    resource,
                    oAuthClientApplication -> updateStatusObjectOnUpdate(resource, oAuthClientApplication)
            );

        }

        namService.createClientIfNeeded(
                getClientIdFromAnnotation(resource),
                resource,
                oAuthClientApplication -> {
                    updateStatusObjectOnCreate(resource, oAuthClientApplication);
                    addClientIdToAnnotation(resource, oAuthClientApplication);
                });

        secretService.createSecretIfNeeded(
                context,
                resource,
                namService.getClientById(
                        getClientIdFromAnnotation(resource)
                                .orElseThrow()
                ).orElseThrow()
        );

        return UpdateControl.updateResourceAndStatus(resource);
    }

    @Override
    public DeleteControl cleanup(NamOAuthClientApplicationResource resource, Context<NamOAuthClientApplicationResource> context) {

        log.debug("Cleanup resource {}", resource.getMetadata().getName());

        secretService.deleteSecretIfExists(context);

        getClientIdFromAnnotation(resource)
                .ifPresent(namService::deleteClientById);

        return DeleteControl.defaultDelete();
    }


    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<NamOAuthClientApplicationResource> context) {

        return EventSourceInitializer
                .nameEventSources(
                        new InformerEventSource<>(
                                InformerConfiguration.from(Secret.class, context)
                                        .withSecondaryToPrimaryMapper(Mappers.fromOwnerReference())
                                        .build(),
                                context)
                );
    }
}
