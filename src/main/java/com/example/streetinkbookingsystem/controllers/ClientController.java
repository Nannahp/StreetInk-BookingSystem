package com.example.streetinkbookingsystem.controllers;

import com.example.streetinkbookingsystem.models.Booking;
import com.example.streetinkbookingsystem.models.Client;
import com.example.streetinkbookingsystem.models.TattooArtist;
import com.example.streetinkbookingsystem.services.BookingService;
import com.example.streetinkbookingsystem.services.ClientService;
import com.example.streetinkbookingsystem.services.LoginService;
import com.example.streetinkbookingsystem.services.TattooArtistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClientController {
    @Autowired
    TattooArtistService tattooArtistService;
    @Autowired
    LoginService loginService;
    @Autowired
    ClientService clientService;
    @Autowired
    BookingService bookingService;


    /* EXTRA method to avoid repeating myself over and over -- adds loggedIn, username and tattooArtist*/
    /* If returns false then not loggedIn and don't have info -- can use in getmappings */
    private void addLoggedInUserInfo(Model model, HttpSession session) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (loggedIn) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);
            TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
            model.addAttribute("tattooArtist", tattooArtist);
        } else {
            model.addAttribute("loggedIn", false);
        }
    }

    @GetMapping("/client")
    public String seeClient(HttpSession session, Model model, @RequestParam("clientId") int clientId,
                            @RequestParam(required = false) Integer clientToDelete) {
        addLoggedInUserInfo(model, session);
        if (!loginService.isUserLoggedIn(session)) {
            return "redirect:/";
        }

        if (clientToDelete != null) {
            model.addAttribute("clientToDelete", clientToDelete);
        }

        Client client = clientService.getClientFromClientId(clientId);
        model.addAttribute("client", client);

        List<Booking> clientBookings = bookingService.getBookingsByClientId(clientId);
        model.addAttribute("clientBookings", clientBookings);

        return "home/client";
    }


    @PostMapping("/client")
    public String clientWithWarning(@RequestParam Integer clientToDelete, @RequestParam int clientId, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        addLoggedInUserInfo(model, session);
        if (!loginService.isUserLoggedIn(session)) {
            return "redirect:/";
        }


        Client client = clientService.getClientFromClientId(clientId);
        model.addAttribute("client", client);

        redirectAttributes.addAttribute("clientToDelete", clientToDelete);
        return "redirect:/client?clientId=" + clientId;
    }

    @GetMapping("/edit-client")
    public String editClient(Model model, HttpSession session, @RequestParam("clientId") int clientId) {
        addLoggedInUserInfo(model, session);
        if (!loginService.isUserLoggedIn(session)) {
            return "redirect:/";
        }

        Client client = clientService.getClientFromClientId(clientId);
        model.addAttribute("client", client);

        return "home/edit-client";
    }

    @PostMapping("/edit-client")
    public String updateClient(@RequestParam String firstName, @RequestParam String lastName,
                               @RequestParam String email, @RequestParam int phoneNumber,
                               @RequestParam String description, @RequestParam int clientId,
                               Model model, HttpSession session) {
        addLoggedInUserInfo(model, session);


        clientService.updateClient(firstName, lastName, email, phoneNumber, description, clientId);
        return "redirect:/client?clientId=" + clientId;
    }

    @PostMapping("/delete-client")
    public String deleteClient(Model model, HttpSession session, @RequestParam("clientId") int clientId) {
        addLoggedInUserInfo(model, session);

        clientService.deleteClientInfoByClientId(clientId);
        return "redirect:/client?clientId=" + clientId;
    }
}
