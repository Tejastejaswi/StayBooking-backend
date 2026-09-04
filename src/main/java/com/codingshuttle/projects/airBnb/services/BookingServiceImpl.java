package com.codingshuttle.projects.airBnb.services;

import com.codingshuttle.projects.airBnb.dto.BookingDto;
import com.codingshuttle.projects.airBnb.dto.BookingRequest;
import com.codingshuttle.projects.airBnb.dto.GuestDto;
import com.codingshuttle.projects.airBnb.entity.*;
import com.codingshuttle.projects.airBnb.entity.enums.BookingStatus;
import com.codingshuttle.projects.airBnb.exception.ResourceNotFoundException;
import com.codingshuttle.projects.airBnb.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories

        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);


        // Create the Booking
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .amount(BigDecimal.TEN)
                .build();

    bookingRepository.save(booking);
        // TODO: calculate dynamic amount


        return modelMapper.map(booking, BookingDto.class);


    }
    @Override
    @Transactional
  public   BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList){
        log.info("Adding guests for booking with id :{}",bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if(hasBookingExperied(booking)){
            throw new IllegalStateException("Booking is already experied for booking with id: " + bookingId);
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state , cannot add guests ");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrentUser());
            guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.Guest_ADDED);
        bookingRepository.save(booking);


        return modelMapper.map(booking, BookingDto.class);
    }

    public boolean hasBookingExperied(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
    public User getCurrentUser(){
        User user = new User();
        user.setId(1L);
        return user;
    }

}
