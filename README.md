# FINT NAMerator

This operator provides the ability integrate NetIQ Access Manager Identity Provider with Kubernetes.

This Operator manages `NamOAuthClientApplicationResource` Custom Resource Definitions (CRDs).
The `NamOAuthClientApplicationResource` CRD, when created, will be used to create a OAuth Client Applicaiton in
NetIQ Access Manager and compose a Kubernetes Secret containing the `clientId` and the `clientSecret`.

# What does the operator do?

When a `NamOAuthClientApplicationResource` CRD is **created** the operator:

- Creates a OAuth Client Application on the NetIQ Access Manager Identity Provider. The name of the client has the
  following
  format `<value from fint.operator.nam.oauth-client.name-prefix>-<name of the CRD>-<uniq id>`.
- Creates a secret containing the client id and client secret for the OAuth Client Application. The name of the secret
  is the same as the name of the CRD. The secret is created in the same namespace as the CRD.

When a `NamOAuthClientApplicationResource` CRD is **updated** the operator:

- Updates the OAuth Client Application on the NetIQ Access Manager Identity Provider with the values from the spec in
  the CRD.

When a `NamOAuthClientApplicationResource` CRD is **deleted** the operator:

- Deletes the OAuth Client Application on the NetIQ Access Manager Identity Provider.
- Deletes the secret.

# Setup

TODO

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

### Example NamOAuthClientApplicationResource

```yaml
apiVersion: "fintlabs.no/v1alpha1"
kind: NamOAuthClientApplicationResource
metadata:
  name: my-client
  namespace: demo
spec:
  grantTypes:
    - client_credentials
  corsDomains:
    - https://test1.no
    - https://test2.no
  idTokenSignedResponseAlg: none
```

### Specification parameters

| Parameters               | Default value                             |
|:-------------------------|:------------------------------------------|
| grantTypes               | `["authorization_code", "refresh_token"]` |
| redirectUris             | `[]`                                      |
| corsDomains              | `[]`                                      |
| responseTypes            | `["code", "id_token", "token"]`           |
| idTokenSignedResponseAlg | `RS256`                                   |

See [NetIQ documentation](https://www.microfocus.com/documentation/access-manager/developer-documentation-5.0/oauth-application-developer-guide/registration-endpoint-details.html#mod-client-app)
for valid values for the parameters.
