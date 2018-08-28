# Summary

This code base is for a "Campsite Reservation Service" that exposes certain HTTP endpoints to make reservations at a campsite, get reservations etc. The requirements are captured in the "Requirements" section below.

The service has been built using spring-boot and H2 (in-memory database)

# Main pre-requisites to run the app

maven (tested with 3.5.4)

jdk 1.8+

# Build & Run
```mvn clean spring-boot:run```

# Run automated tests
```mvn clean test```

# Sample Usage

## Create a reservation
```$ curl -X POST http://localhost:8080/reservations -H 'Content-Type: application/json' -d '{ "fromDate": "2018-09-06", "toDate": "2018-09-07", "email": "a@b.com", "name": "a b" }'```

Output:
```{"confirmationMessage":"Reservation Successful","isSuccessFul":true,"id":"3b9d821d-ac9d-449f-bf04-1c150c94be83"}```

## Update a reservation
```$ curl -X PUT http://localhost:8080/reservations/3b9d821d-ac9d-449f-bf04-1c150c94be83 -H 'Content-Type: application/json' -d '{ "fromDate": "2018-09-06", "toDate": "2018-09-07", "email": "a@b.com", "name": "Nikhil Desai" }'```

Output:
```{"confirmationMessage":"Reservation Successfully Updated.","isSuccessFul":true,"id":"3b9d821d-ac9d-449f-bf04-1c150c94be83"}```

## Get all reservations
```$ curl http://localhost:8080/reservations```

Output:
```[{"id":"3b9d821d-ac9d-449f-bf04-1c150c94be83","fromDate":"2018-09-06","toDate":"2018-09-07","email":"a@b.com","name":"Nikhil Desai"}]```

## Get a reservation
```$ curl http://localhost:8080/reservations/995f09c7-e9ab-45bb-9133-69db592cb05d```

# Requirements

An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was decided to open it up for the general public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST API service that will manage the campsite reservations.

To streamline the reservations a few constraints need to be in place:

* The campsite will be free for all.
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance. Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM

## System Requirements

* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful. 
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow modification/cancellation of an existing reservation
* Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite. 
* Provide appropriate error messages to the caller to indicate the error cases.
* In general, the system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as as long as system constraints are not violated.
