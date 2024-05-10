package com.example.streetinkbookingsystem.services;

import com.example.streetinkbookingsystem.models.Booking;
import com.example.streetinkbookingsystem.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public int getBookingCountForDate(LocalDate specificDate, String username) {
       return bookingRepository.getBookingCountForDate(specificDate, username);
    }

    public int getBookingCountForWeek(int year, int month, int weekNumber, String username) {
        return bookingRepository.getBookingCountForWeek(year, month, weekNumber, username);
    }

    public int getBookingCountForMonth(int year, int month, String username) {
        return bookingRepository.getBookingCountForMonth(year, month, username);
    }


    public List<Booking> getBookingsForMonth(int year, int month, String username) {
        return bookingRepository.getBookingsForMonth(year, month, username);
    }

    public List<Booking> getBookingsForDay(LocalDate date, String username){
        return bookingRepository.getBookingsForDay(date,username);

    }
}
