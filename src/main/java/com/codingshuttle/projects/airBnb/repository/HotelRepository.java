package com.codingshuttle.projects.airBnb.repository;
import com.codingshuttle.projects.airBnb.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
