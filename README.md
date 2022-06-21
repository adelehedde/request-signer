# Request Signer
> Authenticate your requests by signing them

## Overview

Inspired by Amazon Signature process.

## Usage

``` java
// Keep your secret api key safe !
RequestSignerParameters requestSignerParameters = new RequestSignerParameters("<api-key>", "<secret-api-key>", "<api-version>");

RequestSigner requestSigner = new RequestSigner();
RequestAuthenticationSchema requestAuthenticationSchema = requestSigner.signRequest("GET", "https://api.com/search?product_id=prd1", requestSignerParameters);
requestAuthenticationSchema.getAuthorizationHeader();
```

Please notice that query parameters must be encoded individually if necessary :
``` java
// "https://api.com/search?product_name=product+name"

// Basic usage
String uri = URI.create("https://api.com/search?product_name=" + URLEncoder.encode("product name", StandardCharsets.UTF_8.toString())).toString();

// UriComponentsBuilder from Spring
String uri = UriComponentsBuilder.fromHttpUrl("https://api.com/search").queryParam("product_name", URLEncoder.encode("product name", StandardCharsets.UTF_8.toString())).build().toString();
```

## Advanced Usage

### Disable host usage in the process signature

``` java
boolean signedHost = false;
RequestSignerParameters requestSignerParameters = new RequestSignerParameters("<api-key>", "<secret-api-key>", "<api-version>", signedHost, RequestAuthenticationSchema.SIGNATURE_AUTHENTICATION_TYPE);
```

## How to sign a request with ...

### HttpClient from Java

``` java
// Sign Request
URI uri = URI.create("https://api.com/search?product_id=prd1");
RequestAuthenticationSchema requestAuthenticationSchema = requestSigner.signRequest("GET", uri.toString(), requestSignerParameters);

// Configure HttpClient
HttpClient httpClient = HttpClient.newHttpClient();

// Configure HttpRequest
HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(uri).setHeader(RequestAuthenticationSchema.AUTHORIZATION_HEADER, requestAuthenticationSchema.getAuthorizationHeader()).build();

// Send Request
HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
```

### RestTemplate from Spring

``` java
// Sign Request
String uri = UriComponentsBuilder.fromHttpUrl("https://api.com/search").queryParam("product_id", "prd1").build().toString();
RequestAuthenticationSchema requestAuthenticationSchema = requestSigner.signRequest(HttpMethod.GET.toString(), uri, requestSignerParameters);

// Configure RestTemplate
RestTemplate restTemplate = new RestTemplate();

// Configure Request
HttpHeaders headers = new HttpHeaders();
headers.set(RequestAuthenticationSchema.AUTHORIZATION_HEADER, requestAuthenticationSchema.getAuthorizationHeader());

HttpEntity<String> entity = new HttpEntity<>(headers);

// Send Request
ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
```

## How to check signature (server side)

You can use this library to process the received request and check if the signature you calculate is the same as the one provided in the request.

```
// Pseudo code :
// Get Authorization header from request
// Extract informations (String to RequestAuthenticationSchema)
// Get secretApiKey from the apiKey
// Check request signature and request expiration
```

## Signing Process Reference Guide

You can provide credentials to your customers to access your Rest API :
- ApiKey
- SecretApiKey

Requests must be authenticated using these settings.   
The API key is a unique identifier that is used to authenticate requests associated with an account for usage and billing purposes.

A digital signature is also required. The digital signature allows your servers to verify that you are at the origin of the request.

> Please keep your SecretApiKey private ! Do not store it in publicly available code or repositories that are accessible to the public.   
It should never be shared with anyone and must remain confidential.

### When Do I Need to Sign Requests ?
When you write custom code to send HTTP requests, you need to include code to sign the requests.  

### Why Requests Are Signed ?
The signing process helps secure requests in the following ways :
- **Verify the identity of the requester**  
  Signing makes sure that the request has been sent by someone with a valid SecretApiKey.
- **Protect data in transit**  
  To prevent tampering with a request while it's in transit, some of the request elements are used to calculate a hash (digest) of the request, and the resulting hash value is included as part of the request. When the server receives the request, it uses the same information to calculate a hash and matches it against the hash value in your request. If the values does not match, the server rejects the request.
- **Protect against potential replay attacks**  
  A request must reach the server within five minutes of the timestamp in the request. Otherwise, server rejects the request.

### Signing Requests Process

To sign a request, you first calculate a hash (digest) of the request.  
Then you use the hash value, some other information from the request, and your SecretApiKey to calculate another hash known as the signature.  
Then you add the signature to the request in the Authorization header.

#### Using the Authorization Header

Using the HTTP Authorization header is the most common method of providing authentication information.

Syntax :
```
Authorization: <type> <credentials>
```
Please note that a `space` is used to separate type and credentials.


The following is an example of the expected Authorization header value :
```
Authorization: <authentication_type> ApiKey=<api_key>,ApiVersion=<api_version>,SignedHost=<signed_host>,Timestamp=<timestamp>,Signature=<signature>
```

The following table describes the various components of the Authorization header value in the preceding example :

| Component           |                                                  Description                                                  |
| :---:               |:-------------------------------------------------------------------------------------------------------------:|
| authentication_type | Specifies the algorithm used to compute the signature <br> Currently supported version is `REQUEST-SIGNATURE` |
| api_key             |                                                 Your Api Key                                                  |
| api_version         |                                               Api Version used                                                |
| signed_host         |                  `boolean` <br> It specifies if host has been used in the signature process                   |
| timestamp           |                        Unix `timestamp` (ms) <br> Time at which the request was signed                        |
| signature           |                                     The signature processed on your side                                      |

Please notice that :
- components are separated by `,` (except for the authentication type)
- component key is separated from its value by `=`
- components are case sensitive

Upon receiving the request, the server re-creates the string to sign using information in the Authorization header. It then verifies the signatures matching.

If the signatures match and the timestamp is valid, the server processes your request; otherwise, your request will fail.

#### Signing Requests

The following table describes the functions that are referenced below. You need to implement code for these functions.

| Function          | Description                                                                      |
| :---:             | :---:                                                                            |
| hash              | Secure Hash Algorithm (SHA) cryptographic hash function <br> `sha256` algorithm  |
| hash_hmac         | Computes HMAC with the signing key provided <br> `sha256` algorithm              |
| encode            | Base64Url encoding with no padding <br> Base64 encoding except that `–` is used instead of `+` and `_` is used instead of `/`, trailing `=` are removed |

Please note that `String` contents are assumed to be `UTF-8`.

##### 1. Create A Canonical Request

To begin the signing process, create a string that includes information from your request in a standardized (canonical) format. This ensures that when the server receives the request, it can calculate the same signature that you calculated.

Pseudocode to create a canonical request :
```
canonicalRequest =
  httpMethod
  + " " + host  // Omit this line if SignedHost=false
  + " " + path 
  + " " + queryParameters // Omit this line if no queryParameters
```

| Key                  | Description                                        |
| :---:                | :---:                                              |
| httpMethod           | HTTP Request method <br> for instance `GET`, `POST`, `PUT`, `HEAD`, `DELETE` |
| host                 | Domain name of the server <br> Add it if `SignedHost=true`                   |                                   
| path                 | Everything starting with the "/" that follows the domain name and up to the end of the string or to the question mark character ('?') | 
| queryParameters      | Query parameters if any <br> Name and values must be encoded individually if necessary |

Example Request :
```
// GET https://api.com/search?product_id=prd1&customer_id=c1
canonicalRequest = "GET api.com /search product_id=prd1&customer_id=c1"

// GET https://api.com/search with `SignedHost=false`
canonicalRequest = "GET /search"
```

Please note that we let you the responsability to encode any fields which need to be escaped.  
For instance : "product_id=my product id" must be encoded like this : "product_id=my+product+id". This encoded string must be used to create the canonical request.

##### 2. Create A String To Sign

The string to sign includes meta information about your request and about the canonical request that you created before.

Pseudocode to create a string to sign :
```
stringToSign =
  authentication_type + " " +
  api_key + " " +
  api_version + " " +
  timestamp + " " +
  encode(hash(canonicalRequest))
```

##### 3. Calculate The Signature

Before you calculate a signature, you have to derive a signing key from your `SecretApiKey` to offer a greater degree of protection.

Pseudocode for deriving a signing key
```
secretKey = "REQUEST_SIGNER" + SecretApiKey
secretApiVersionKey = hash_hmac(api_version, secretKey)
secretTimestampKey = hash_hmac(timestamp, secretApiVersionKey)
signingKey = hash_hmac("REQUEST_SIGNER_REQUEST", secretTimestampKey)
```

Pseudocode to calculate the signature
```
signature = encode(hash_hmac(stringToSign, signingKey))
```

You can add this signature to your http request in the header named `Authorization` according to the specification detailed above.
