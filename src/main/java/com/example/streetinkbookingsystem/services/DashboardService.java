package com.example.streetinkbookingsystem.services;

import com.example.streetinkbookingsystem.models.Booking;
import com.example.streetinkbookingsystem.models.TattooArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    BookingService bookingService;

    @Autowired
    TattooArtistService tattooArtistService;

    public int calculateAmtBookingsADay(String username) {
        return bookingService.getBookingCountForDate(LocalDate.now(), username);
    }

    public int calculateAmtBookingsAWeek(String username) {
        return bookingService.getBookingCountForWeek(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), getWeekNumber(LocalDate.now()), username);
    }

    /**
     * THANK YOU CHATTY
     * Retrieves the week number of the given date based on the ISO-8601 standard.
     * ISO week numbering defines the week as starting on Monday and ending on Sunday,
     * and assigns each week a number within the year based on this scheme.
     *
     * @param date The LocalDate for which to determine the week number.
     * @return The week number of the given date according to the ISO-8601 standard.
     */

    public static int getWeekNumber(LocalDate date) {
        // Get the WeekFields for ISO definition (Monday as the start of the week)
        WeekFields weekFields = WeekFields.ISO;

        // Get the week number using ISO week-of-week-based-year
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());

        return weekNumber;
    }


    public int calculateBookingPercentageOfMonth(String username) {
        LocalDate currentDate = LocalDate.now();
        // not counting weekends - so only week days
        int totalWeekdaysInMonth = calculateWeekdaysInMonth(currentDate);
        // havew to get th ebookings for the month
        List<Booking> bookingsForMonth = bookingService.getBookingsForMonth(currentDate.getYear(), currentDate.getMonthValue(), username);
        // the tattoo artist aswell to get his avg amnt of hours/day
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        double averageWorkHoursPerDay = tattooArtist.getAvgWorkHours();
        // from there : total available hours a month based on avg hours a day
        int totalWorkHoursForMonth = (int) Math.round(averageWorkHoursPerDay * totalWeekdaysInMonth);

        // calc how many hours for eahc booking and add them to get a total hours of booked time
        int totalBookedHoursForMonth = calculateTotalBookedHours(bookingsForMonth);


        // now the booking percentage of the available hours of the month
        // based on the avg hours a day
        double bookingPercentage = ((double) totalBookedHoursForMonth / totalWorkHoursForMonth) * 100;
        return (int) Math.round(bookingPercentage);
    }


    public int calculateMonthProgressPercentage() {
        LocalDate currentDate = LocalDate.now();
        int totalWeekdaysInMonth = calculateWeekdaysInMonth(currentDate);
        int currentDayOfMonth = currentDate.getDayOfMonth();

        double progressPercentage = ((double) currentDayOfMonth / totalWeekdaysInMonth) * 100;
        return (int) Math.round(progressPercentage);
    }

    private int calculateWeekdaysInMonth(LocalDate date) {
        int weekdaysInMonth = 0;
        for (int day = 1; day <= date.lengthOfMonth(); day++) {
            LocalDate currentDate = LocalDate.of(date.getYear(), date.getMonthValue(), day);
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                weekdaysInMonth++;
            }
        }
        return weekdaysInMonth;
    }

    public int calculateTotalBookedHours(List<Booking> bookings) {
        int totalBookedHours = 0;
        for (Booking booking : bookings) {
            LocalTime startTime = booking.getStartTimeSlot();
            LocalTime endTime = booking.getEndTimeSlot();

            // duration between start time and end time -- mega smart Duration class
            Duration duration = Duration.between(startTime, endTime);

            // total hours from duration -- so  smart toHours method
            long hours = duration.toHours();

            // Add total hours to total booked hours
            totalBookedHours += hours;
        }
        return totalBookedHours;
    }
}
