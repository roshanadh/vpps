# Virtual Power Plant System - REST API
![VPPS](https://github.com/roshanadh/vpps/actions/workflows/maven.yml/badge.svg?branch=main)
[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/summary/new_code?id=np.com.roshanadhikary%3Avpps)
## Tools and Frameworks Used
* Java 11
* Spring Boot
* Spring Data JPA
* H2 in-memory database
* Lombok - *for reducing the amount of boilerplate constructors, getters, and setters in POJO entities* 
* JUnit
* Mockito - *for mocking the database layer when testing the service layer*
* MockMvc - *for writing integration tests*
* AssertJ
* GitHub Actions
* SonarQube
* JaCoCo

## Setup Project
1. Clone the repository
```sh
git clone https://github.com/roshanadh/vpps.git
```
2. Run the application
```sh
cd vpps && .\mvnw spring-boot:run
```

This sets up the project and runs the application on port 8080.

## API Reference

### 1. Index

#### Request
```http
GET /
```

#### Response

The API returns a simple `Hello World!` message as a response.

### 2. Fetch a single battery resource

#### Request
```http
GET /batteries/{ID}
```

| Path Variable | Type  | Description              |
|:--------------|:------|:-------------------------|
| `ID`          | `int` | ID of a battery resource |

#### Response

Upon successful fetching of the resource, the API returns a JSON response with the following structure:

```javascript
{
  "id" : int,
  "name" : string,
  "postcode" : string,
  "capacity": int
}
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database. 

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

### 3. Fetch all battery resources

#### Request
```http
GET /batteries/
```

#### Response

Upon successful fetching of the resources, the API returns a list of JSON objects with the following structure:

```javascript
[
    {
      "id" : int,
      "name" : string,
      "postcode" : string,
      "capacity": int
    },
    ...
]
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database.

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

### 4. Fetch all battery resources within a range of postcode

To get a list of the names of batteries having postcode within the range provided in the request.
#### Request
```http
GET /batteries/postcode?start={}&end={}
```

| Query Parameter | Type                    | Description                    |
|:----------------|:------------------------|:-------------------------------|
| `start`         | `integer` (e.g., 01002) | start of the range |
| `end`           | `integer` (e.g., 01002) | end of the range    |

Both, `start` and `end`, parameter values are included in the range.
Both parameter values must be numeric string. If the provided range values cannot be parsed into an integer value, a `400 BAD REQUEST` response is returned.

#### Response

Upon successful fetching of the resources, the API returns a JSON response with the following structure:

```javascript
{
  "totalCapacity" : long,
  "avgCapacity" : double,
  "batteries" : [],
}
```

The `totalCapacity` attribute contains a long-valued sum of the capacities of battery resources found in the given postcode range.

The `avgCapacity` attribute contains a double-valued average of the capacities of battery resources found in the given postcode range.

The `batteries` attribute contains the list of names of battery resources found in the given postcode range.

### 5. Persist a list of battery resources

To persist a list of the battery resources in the in-memory H2 database.

#### Request
```http
POST /batteries/
```

The payload for this POST request is an array of JSON objects, each object being a battery resource to be persisted.
The payload has the following structure:
```javascript
[
    {
      "name" : string,
      "postcode" : string,
      "capacity" : int,
    },
    ...
]
```

#### Response

Upon successful persisting of the resources, the API returns a JSON response similar to the payload, but with an `id` attribute included in each JSON object:

```javascript
[
    {
        "id" : int,
        "name" : string,
        "postcode" : string,
        "capacity": int
    },
    ...
]
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database.

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

### 6. Persist a single battery resource

To persist a single battery resource in the in-memory H2 database.

#### Request
```http
POST /batteries/new
```

The payload for this POST request is a JSON object, representing the battery resource to be persisted.
The payload has the following structure:
```javascript
{
    "name" : string,
    "postcode" : string,
    "capacity" : int,
}
```

#### Response

Upon successful persisting of the resource, the API returns a JSON response similar to the payload, but with an `id` attribute included in the JSON object:

```javascript
{
    "id" : int,
    "name" : string,
    "postcode" : string,
    "capacity": int
}
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database.

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

### 7. Update a battery resource

To update a battery resource in the in-memory H2 database.

#### Request
```http
PUT /batteries/
```

The payload for this POST request is a JSON object, with a mandatory `id` field. If the resource identified by `id` does not exist in the database, a 404 response is made.
The payload has the following structure:
```javascript
{
    "id" : int, 
    "name" : String,
    "postcode" : String,
    "capacity" : int
}
```

#### Response

Upon successful update of the resource, the API returns a JSON response containing the resource that was updated (identical to the request payload):

```javascript
{
    "id" : int,
    "name" : string,
    "postcode" : string,
    "capacity": int
}
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database.

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

### 8. Delete a battery resource

#### Request
```http
DELETE /batteries/{ID}
```

| Path Variable | Type  | Description              |
|:--------------|:------|:-------------------------|
| `ID`          | `int` | ID of a battery resource |

#### Response

Upon successful deletion of the resource, the API returns a JSON response containing the resource that was just deleted:

```javascript
{
  "id" : int,
  "name" : string,
  "postcode" : string,
  "capacity": int
}
```

The `id` attribute contains an integer ID auto-generated when a battery resource is persisted in the database.

The `name` attribute contains the name of the battery resource.

The `postcode` attribute contains the postcode information of the battery resource.

The `capacity` attribute contains the watt-capacity information of the battery resource.

## Status Codes

The API can respond with the following status codes:

| Status Code | Description             | Example Triggering Scenario                        |
|:------------|:------------------------|:---------------------------------------------------|
| 200         | `OK`                    | `List of batteries fetched successfully`           |
| 201         | `CREATED`               | `List of batteries persisted successfully`         |
| 400         | `BAD REQUEST`           | `Invalid range of postcode given in request`       |
| 404         | `NOT FOUND`             | `No battery exists with given ID`                  |
| 416         | `RANGE NOT SATISFIABLE` | `No battery exists within given range of postcode` |
| 500         | `INTERNAL SERVER ERROR` | `Internal error in API`                            |
