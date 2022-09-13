package no.fintlabs;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("fintlabs.no")
@Version("v1alpha1")
public class NamOAuthClientApplicationResource extends CustomResource<NamOAuthClientApplicationSpec, NamOAuthClientApplicationStatus> implements Namespaced {
    @Override
    protected NamOAuthClientApplicationStatus initStatus() {
        return new NamOAuthClientApplicationStatus();
    }
}