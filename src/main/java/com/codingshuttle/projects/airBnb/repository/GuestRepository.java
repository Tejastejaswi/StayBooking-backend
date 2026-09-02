package com.codingshuttle.projects.airBnb.repository;

import com.codingshuttle.projects.airBnb.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}