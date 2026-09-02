package com.codingshuttle.projects.airBnb.services;

import com.codingshuttle.projects.airBnb.dto.BookingDto;
import com.codingshuttle.projects.airBnb.dto.BookingRequest;
import com.codingshuttle.projects.airBnb.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    //BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
