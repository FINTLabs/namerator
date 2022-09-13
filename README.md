# FINT Kubernetes Operator for NetIQ Access Manager
This operator provides the ability integrate NetIQ Access Manager Identity Provider with Kubernetes. 

This Operator manages `NamOAuthClientApplicationResource` Custom Resource Definitions (CRDs).
The `NamOAuthClientApplicationResource` CRD, when created, will be used to create a OAuth Client Applicaiton in
NetIQ Access Manager and compose a Kubernetes Secret containing the `clientId` and the `clientSecret`.

# Usage
## NamOAuthClientApplicationResource

```yaml
apiVersion: "fintlabs.no/v1alpha1"
kind: NamOAuthClientApplicationResource
metadata:
  name: <some name>
spec:
  <parameters as needed>
```

| Parameters               | Default value                             |
|--------------------------|-------------------------------------------|
| grantTypes               | `["client_credentials", "refresh_token"]` |
| redirectUris             | `["https://dummy.com"]`                   |
| corsDomains              | `[]`                                      |
| responseTypes            | `["code", "id_token", "token"]`           |
| idTokenSignedResponseAlg | `RS256`                                   |

