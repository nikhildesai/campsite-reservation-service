package com.example.demo.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.manager.CampsiteManager;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationConfirmation;
import com.example.demo.model.ReservationRequest;
import com.example.demo.util.DateUtils;

@RestController
public class CampsiteController {

	@Autowired
	CampsiteManager campsiteManager;

	@RequestMapping(method = RequestMethod.GET, path = "/reservations")
	public List<Reservation> getReservations(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to) {

		// Validate and convert dates. Use 30 day date range by default
		Date fromDate;
		Date toDate;
		if (DateUtils.isValidDateString(from) && DateUtils.isValidDateString(to)) {
			// use date range from input
			fromDate = DateUtils.parseDate(from);
			toDate = DateUtils.parseDate(to);
		} else {
			// use 30 days
			Calendar c1 = new GregorianCalendar();
			c1.set(Calendar.HOUR_OF_DAY, 0);
			c1.set(Calendar.MINUTE, 0);
			c1.set(Calendar.SECOND, 0);
			fromDate = c1.getTime();

			Calendar c2 = new GregorianCalendar();
			c2.set(Calendar.HOUR_OF_DAY, 0);
			c2.set(Calendar.MINUTE, 0);
			c2.set(Calendar.SECOND, 0);
			c2.add(Calendar.DATE, 30);
			toDate = c2.getTime();
		}

		return campsiteManager.find(fromDate, toDate);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/reservations")
	public Object makeReservation(@RequestBody ReservationRequest reservationRequest) {

		// Validations
		if (!DateUtils.isValidDateString(reservationRequest.getFromDate())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid start date");
		}

		if (!DateUtils.isValidDateString(reservationRequest.getToDate())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid end date");
		}

		Reservation input = convert(reservationRequest);

		if (input.getFromDate().getTime() < System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
				|| input.getFromDate().getTime() > System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"The campsite reservation can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
		}

		long reservationLengthDays = getNumberOfDaysBetween(input.getFromDate().getTime(),
				input.getToDate().getTime());
		if (!(reservationLengthDays >= 1 && reservationLengthDays <= 3)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The campsite can be reserved for min 1 day and max 3 days");
		}

		// Make a reservation with valid input
		ReservationConfirmation confirmation = campsiteManager.makeReservation(input);

		return confirmation;
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/reservations/{id}")
	public Object makeReservation(@PathVariable String id, @RequestBody ReservationRequest reservationRequest) {

		// Validations
		if (!DateUtils.isValidDateString(reservationRequest.getFromDate())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid start date");
		}

		if (!DateUtils.isValidDateString(reservationRequest.getToDate())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid end date");
		}

		Reservation input = convert(reservationRequest);

		if (input.getFromDate().getTime() < System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
				|| input.getFromDate().getTime() > System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"The campsite reservation can be modified minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
		}

		long reservationLengthDays = getNumberOfDaysBetween(input.getFromDate().getTime(),
				input.getToDate().getTime());
		if (!(reservationLengthDays >= 1 && reservationLengthDays <= 3)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The campsite can be reserved for min 1 day and max 3 days");
		}

		// Make a reservation with valid input
		ReservationConfirmation confirmation = campsiteManager.updateReservation(id, input);

		return confirmation;
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/reservations/{id}")
	public Object deleteReservation(@PathVariable String id) {
		int result = campsiteManager.delete(id);
		if (result == 1) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else if (result == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private long getNumberOfDaysBetween(long startTime, long endTime) {
		long diffInMillies = Math.abs(endTime - startTime);
		return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	private Reservation convert(ReservationRequest reservationRequest) {
		Reservation reservation = new Reservation();
		reservation.setEmail(reservationRequest.getEmail());
		reservation.setName(reservationRequest.getName());
		reservation.setFromDate(DateUtils.parseDate(reservationRequest.getFromDate()));
		reservation.setToDate(DateUtils.parseDate(reservationRequest.getToDate()));
		reservation.setId(UUID.randomUUID().toString());
		return reservation;
	}
}
