package com.example.demo.manager;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.demo.dao.ReservationDao;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationConfirmation;

public class CampsiteManager {

	@Autowired
	ReservationDao reservationDao;

	public List<Reservation> find(Date fromDate, Date toDate) {
		return reservationDao.find(fromDate, toDate);
	}

	@Transactional
	public ReservationConfirmation makeReservation(Reservation input) {
		List<Reservation> overlappingReservations = reservationDao.findOverLappingReservations(input.getFromDate(),
				input.getToDate(), null);

		ReservationConfirmation confirmation = new ReservationConfirmation();
		if (CollectionUtils.isEmpty(overlappingReservations)) {
			reservationDao.insert(input);
			confirmation.setIsSuccessFul(Boolean.TRUE);
			confirmation.setConfirmationMessage("Reservation Successful");
			confirmation.setId(input.getId());
		} else {
			confirmation.setIsSuccessFul(Boolean.FALSE);
			confirmation.setConfirmationMessage("Could not make reservation. Please try again");
		}

		return confirmation;
	}

	@Transactional
	public ReservationConfirmation updateReservation(String id, Reservation input) {

		List<Reservation> overlappingReservations = reservationDao.findOverLappingReservations(input.getFromDate(),
				input.getToDate(), id);

		ReservationConfirmation confirmation = new ReservationConfirmation();
		confirmation.setId(id);
		if (CollectionUtils.isEmpty(overlappingReservations)) {
			reservationDao.update(id, input);
			confirmation.setIsSuccessFul(Boolean.TRUE);
			confirmation.setConfirmationMessage("Reservation Successfully Updated.");
		} else {
			confirmation.setIsSuccessFul(Boolean.FALSE);
			confirmation.setConfirmationMessage("Reservation could not be updated. Please try again");
		}

		return confirmation;
	}

	public Reservation findById(String id) {
		return reservationDao.findById(id);
	}

	public int delete(String id) {
		return reservationDao.deleteById(id);
	}

}
