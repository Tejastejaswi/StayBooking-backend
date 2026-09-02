package com.codingshuttle.projects.airBnb.services;

import com.codingshuttle.projects.airBnb.dto.RoomDto;
import com.codingshuttle.projects.airBnb.entity.Hotel;
import com.codingshuttle.projects.airBnb.entity.Room;
import com.codingshuttle.projects.airBnb.exception.ResourceNotFoundException;
import com.codingshuttle.projects.airBnb.repository.HotelRepository;
import com.codingshuttle.projects.airBnb.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
   private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        //Treads problem 1: If we delete the room first, then the inventory will be deleted by cascade, but if there is a problem with deleting the inventory,
        // then the room will be deleted but the inventory will not be deleted, which will cause data inconsistency.
        // So we need to delete the inventory first and then delete the room.

        //but the current idea of deleting the inventory first and then room fails that's second problem
        //Problem 2 : ⚠️ Why your current logic failed :
        //Possible reasons:
        //
        //Your method deletes only future inventories
        //Old/past inventories still exist
        //DB constraint doesn’t care about "future/past" — it cares about ANY reference
        //withput the room, the old inventories still reference the deleted room, causing a constraint violation (Important point)
        //Solution:
        //Delete all inventories related to the room, regardless of date (not just future ones)

       inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }


}
