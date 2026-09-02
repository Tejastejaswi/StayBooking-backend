package com.codingshuttle.projects.airBnb.repository;

import com.codingshuttle.projects.airBnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
