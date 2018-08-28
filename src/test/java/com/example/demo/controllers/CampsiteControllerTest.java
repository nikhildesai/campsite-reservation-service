package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.DemoApplication;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationConfirmation;
import com.example.demo.model.ReservationRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CampsiteControllerTest {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testReservationCRUD() throws JSONException {
		// Make a reservation (POST /reservations)
		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setFromDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5))));
		reservationRequest.setToDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8))));
		reservationRequest.setName("Nikhil Desai");
		reservationRequest.setEmail("a@b.com");

		HttpEntity<ReservationRequest> postEntity = new HttpEntity<ReservationRequest>(reservationRequest, headers);

		ResponseEntity<ReservationConfirmation> postResponse = restTemplate.exchange(createURLWithPort("/reservations"),
				HttpMethod.POST, postEntity, ReservationConfirmation.class);

		String reservationId = postResponse.getBody().getId();
		Assert.assertNotNull(reservationId);
		Assert.assertTrue(postResponse.getBody().getIsSuccessFul());

		// Fetch the reservations (GET /reservations)
		HttpEntity<String> getEntity = new HttpEntity<String>(null, headers);
		ResponseEntity<Reservation[]> getResponse = restTemplate.exchange(createURLWithPort("/reservations"),
				HttpMethod.GET, getEntity, Reservation[].class);
		Assert.assertEquals(reservationId, getResponse.getBody()[0].getId());

		// Delete the reservation (DELETE /reservations/{id})
		HttpEntity<String> deleteEntity = new HttpEntity<String>(null, headers);
		restTemplate.exchange(createURLWithPort("/reservations/" + reservationId), HttpMethod.DELETE, deleteEntity,
				String.class);
		Assert.assertEquals(reservationId, getResponse.getBody()[0].getId());

		// Fetch again and check if deleted (GET /reservations)
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<Reservation[]> response = restTemplate.exchange(createURLWithPort("/reservations"),
				HttpMethod.GET, entity, Reservation[].class);
		Assert.assertTrue(response.getBody().length == 0);
	}

	@Test
	public void testConcurrentReservationRequests() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(20);

		Callable<String> callable = () -> {
			// Make a reservation (POST /reservations)
			ReservationRequest reservationRequest = new ReservationRequest();
			reservationRequest.setFromDate(new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5))));
			reservationRequest.setToDate(new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8))));
			reservationRequest.setName("Nikhil Desai");
			reservationRequest.setEmail("a@b.com");

			HttpEntity<ReservationRequest> postEntity = new HttpEntity<ReservationRequest>(reservationRequest, headers);

			ResponseEntity<ReservationConfirmation> postResponse = restTemplate.exchange(
					createURLWithPort("/reservations"), HttpMethod.POST, postEntity, ReservationConfirmation.class);

			System.out
					.println("made request, got response message: " + postResponse.getBody().getConfirmationMessage());

			return postResponse.getBody().getId();
		};

		// make 20 parallel requests to make a reservation
		List<Future<String>> futures = new ArrayList<Future<String>>();
		for (int i = 0; i < 50; i++) {
			futures.add(executorService.submit(callable));
		}

		futures.stream().forEach((f) -> {
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});

		executorService.shutdown();

		// Fetch the reservations (GET /reservations) and verify there is only 1
		// reservation made
		HttpEntity<String> getEntity = new HttpEntity<String>(null, headers);
		ResponseEntity<Reservation[]> getResponse = restTemplate.exchange(createURLWithPort("/reservations"),
				HttpMethod.GET, getEntity, Reservation[].class);
		Assert.assertTrue(getResponse.getBody().length == 1);
	}

	@Test
	public void testReservationLengthOfStay() throws JSONException {
		// Make a reservation (POST /reservations)
		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setFromDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5))));
		reservationRequest.setToDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(9))));
		reservationRequest.setName("Nikhil Desai");
		reservationRequest.setEmail("a@b.com");

		HttpEntity<ReservationRequest> postEntity = new HttpEntity<ReservationRequest>(reservationRequest, headers);

		ResponseEntity<String> postResponse = restTemplate.exchange(createURLWithPort("/reservations"), HttpMethod.POST,
				postEntity, String.class);

		Assert.assertEquals("The campsite can be reserved for min 1 day and max 3 days", postResponse.getBody());
	}

	@Test
	public void testReservationLeadTime() throws JSONException {
		// Make a reservation (POST /reservations)
		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setFromDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(31))));
		reservationRequest.setToDate(new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(33))));
		reservationRequest.setName("Nikhil Desai");
		reservationRequest.setEmail("a@b.com");

		HttpEntity<ReservationRequest> postEntity = new HttpEntity<ReservationRequest>(reservationRequest, headers);

		ResponseEntity<String> postResponse = restTemplate.exchange(createURLWithPort("/reservations"), HttpMethod.POST,
				postEntity, String.class);

		Assert.assertEquals(
				"The campsite reservation can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.",
				postResponse.getBody());
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}