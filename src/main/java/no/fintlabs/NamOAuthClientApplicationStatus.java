package no.fintlabs;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamOAuthClientApplicationStatus extends ObservedGenerationAwareStatus {
    private String clientUri;
    private String errorMessage;
}
