package com.codingshuttle.projects.airBnb.repository;
import com.codingshuttle.projects.airBnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
