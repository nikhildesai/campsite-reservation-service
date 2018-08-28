package com.example.demo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Reservation;

@Repository
public class ReservationDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	class ReservationRowMapper implements RowMapper<Reservation> {
		@Override
		public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
			Reservation reservation = new Reservation();
			reservation.setId(rs.getString("id"));
			reservation.setFromDate(rs.getDate("fromDate"));
			reservation.setToDate(rs.getDate("toDate"));
			reservation.setName(rs.getString("name"));
			reservation.setEmail(rs.getString("email"));
			return reservation;
		}

	}

	public Reservation findById(long id) {
		return jdbcTemplate.queryForObject("select * from reservation where id=?", new Object[] { id },
				new ReservationRowMapper());
	}

	public int deleteById(String id) {
		return jdbcTemplate.update("delete from reservation where id=?", new Object[] { id });
	}

	public int insert(Reservation reservation) {
		return jdbcTemplate.update(
				"insert into reservation (id, fromDate, toDate, email, name) " + "values(?, ?, ?, ?, ?)",
				new Object[] { reservation.getId(), reservation.getFromDate(), reservation.getToDate(),
						reservation.getEmail(), reservation.getName() });
	}

	public List<Reservation> findAll() {
		return jdbcTemplate.query("select * from reservation", new ReservationRowMapper());
	}

	public List<Reservation> find(Date fromDate, Date toDate) {
		return jdbcTemplate.query("select * from reservation where fromDate >= ? and toDate <= ?",
				new Object[] { fromDate, toDate }, new ReservationRowMapper());
	}

	public List<Reservation> findOverLappingReservations(Date inputfromDate, Date inputtoDate, String excludeId) {
		return jdbcTemplate.query("select * from reservation where ? >= fromDate and toDate >= ? and id !=?",
				new Object[] { inputtoDate, inputfromDate, excludeId == null ? "" : excludeId },
				new ReservationRowMapper());
	}

	public int update(String id, Reservation reservation) {
		return jdbcTemplate.update("update reservation set fromDate=?, toDate=?, email=?, name=? where id=?",
				new Object[] { reservation.getFromDate(), reservation.getToDate(), reservation.getEmail(),
						reservation.getName(), id });
	}
}
