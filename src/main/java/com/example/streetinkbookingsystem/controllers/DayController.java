package com.example.streetinkbookingsystem.controllers;

import com.example.streetinkbookingsystem.models.Booking;
import com.example.streetinkbookingsystem.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DayController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/day")
    public String seeDay(Model model, @RequestParam LocalDate date, @RequestParam(required = false) String username){
        if (username == null){
            //        HttpSession session = get session, if session is null then redirect to index.
            // else set username to session.getAttribute(username);
            return "redirect:/";}

        List<Booking> bookingList = bookingService.getBookingsForDay(date,username);
        model.addAttribute("bookingList", bookingList);
        model.addAttribute("date", date);
        return "home/day";
    }
}
