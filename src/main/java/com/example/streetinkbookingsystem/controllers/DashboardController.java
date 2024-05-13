package com.example.streetinkbookingsystem.controllers;

import com.example.streetinkbookingsystem.models.TattooArtist;
import com.example.streetinkbookingsystem.services.BookingService;
import com.example.streetinkbookingsystem.services.DashboardService;
import com.example.streetinkbookingsystem.services.LoginService;
import com.example.streetinkbookingsystem.services.TattooArtistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class DashboardController {

    @Autowired
    TattooArtistService tattooArtistService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    LoginService loginService;

    @GetMapping("/dashboard")
    public String seeDashboard(Model model, HttpSession session) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (!loggedIn) {
            return "redirect:/login";
        }
        model.addAttribute("loggedIn", loggedIn);

        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(session.getAttribute("username").toString());
        model.addAttribute("tattooArtist", tattooArtist);

        ArrayList<TattooArtist> profiles = (ArrayList) tattooArtistService.showTattooArtist();

        // temporary profile
        TattooArtist profile = profiles.get(1);

        int bookingsADay = dashboardService.calculateAmtBookingsADay(profile.getUsername());
        int bookingsAWeek = dashboardService.calculateAmtBookingsAWeek(profile.getUsername());

        int bookingPercentageOfMonth = dashboardService.calculateBookingPercentageOfMonth(profile.getUsername());
        int monthProgressPercentage = dashboardService.calculateMonthProgressPercentage();


        model.addAttribute("profile", profile);
        model.addAttribute("bookingsADay", bookingsADay);
        model.addAttribute("bookingsAWeek", bookingsAWeek);
        model.addAttribute("bookingPercentageOfMonth", bookingPercentageOfMonth);
        model.addAttribute("monthProgressPercentage", monthProgressPercentage);
        return "home/dashboard";
    }
}
