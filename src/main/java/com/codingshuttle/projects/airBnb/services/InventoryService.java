package com.codingshuttle.projects.airBnb.services;

import com.codingshuttle.projects.airBnb.dto.HotelDto;
import com.codingshuttle.projects.airBnb.dto.HotelSearchRequest;
import com.codingshuttle.projects.airBnb.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
