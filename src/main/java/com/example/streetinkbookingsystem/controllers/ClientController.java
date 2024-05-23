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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class ClientController {

    @Autowired
    ClientService clientService;
    @Autowired
    LoginService loginService;
    @Autowired
    BookingService bookingService;

    @Autowired
    TattooArtistService tattooArtistService;


    /**
     * @Author Munazzah
     * @param model
     * @param session
     * @return String - View of the client-list page
     * @summary Gets the sorted list og Clients from the service layer, and then uses Map to
     * group the Clients based on the first letter in name
     */
    @GetMapping("/client-list")
    public String clientList(Model model, HttpSession session) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (loggedIn) {
            model.addAttribute("loggedIn", loggedIn);
        } else {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        model.addAttribute("tattooArtist", tattooArtist);

        List<Client> sortedClients = clientService.getSortedListOfClients();
        //ADT Map is the result here, where the key is a character (first letter) and value is List<Client>
        //Uses TreeMap to maintain the natural order of the keys (that are sorted beforehand)
        //Uses stream to handle everything simulationaly
        //Uses collect (Collectors.groupingBy) to group the elements of teh stream based on first letter in first name
        Map<Character, List<Client>> groupedClients = sortedClients.stream()
                .collect(Collectors.groupingBy(client -> client.getFirstName().charAt(0),
                        TreeMap::new, Collectors.toList()));

        model.addAttribute("groupedClients", groupedClients);
        return "home/client-list";
    }

    /**
     * @Author Munazzah
     * @param searchQuery
     * @param model
     * @param redirectAttributes
     * @param session
     * @return String - View of search-results
     * @summary Search for a Client based on phone number or first name. The if-statement
     * checks if it is a number or name and acts accordingly
     */
    @PostMapping("/search")
    public String search(@RequestParam("search") String searchQuery, Model model,
                         RedirectAttributes redirectAttributes, HttpSession session) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (loggedIn) {
            model.addAttribute("loggedIn", loggedIn);
        } else {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        model.addAttribute("tattooArtist", tattooArtist);

        model.addAttribute("searchQuery", searchQuery);
        //Checks (via regex) if there are only numbers, letters or a mix of both and acts accordingly
        if (searchQuery.matches("[0-9]+")) {
            model.addAttribute("searchType", "phoneNumber");
            List<Client> clientByNumber = clientService.getClientsByPhoneNumber(Integer.parseInt(searchQuery));
            model.addAttribute("results", clientByNumber);
        } else if (searchQuery.matches("[A-Za-z]+")) {
            model.addAttribute("searchType", "firstName");
            List<Client> clientByName = clientService.getClientsByFistName(searchQuery);
            model.addAttribute("results", clientByName);
        } else {
            redirectAttributes.addFlashAttribute("message", "Please enter a valid number or first name");
            return "redirect:/client-list";
        }
        return "home/search-result";
    }


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

    /**
     * @Author Tara
     * @param bookingId
     * @param clientId
     * @param model
     * @param session Used to determine if the user is logged in or not. User will be redirected
     *                to index page if not logged in.
     * @return String - add-client view
     * @Summary Creates the new client, that the user wants to add to the booking that the user is creating
     */
    @GetMapping("/add-client")
    public String addClient(@RequestParam int bookingId,
                            @RequestParam int clientId,
                            Model model,
                            HttpSession session) {

        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (!loggedIn) {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);

        //int clientId = bookingService.getBookingDetail(bookingId).getClient().getId();
        Client client = clientService.getClientFromClientId(clientId);

        if (client.getId() == 1){
            client = new Client();
        }

        model.addAttribute("tattooArtist", tattooArtist);
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("username", username);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("client", client);
        return "home/add-client";

    }

    /**
     * @Author Tara
     * @param bookingId
     * @param clientId
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param description
     * @param session Used to determine if the user is logged in or not. User will be redirected
     *                to index page if not logged in.
     * @param redirectAttributes
     * @return
     * @Summary Saves the new client to the booking that the user is creating
     */
    @PostMapping("/save-client")
    public String saveClient(@RequestParam int bookingId,
                             @RequestParam(required = false, defaultValue = "1") int clientId,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam int phoneNumber,
                             @RequestParam String description,
                             HttpSession session,
                             RedirectAttributes redirectAttributes){

        String username = (String) session.getAttribute("username");

        if (username == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Your session ran out, log in again.");
            return "redirect:/";
        }

        Client client = new Client();
        client.setId(clientId);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhoneNumber(phoneNumber);
        client.setDescription(description);
        client = clientService.saveClient(client);

        clientService.updateClientOnBooking(bookingId, client.getId());


       // return "redirect:/booking-preview?bookingId=" + bookingId + "&username=" + username + "&clientId=" + clientId;
        //return "redirect:/booking-preview?bookingId=" + bookingId + "&username=" + username;


        return "redirect:/booking?bookingId=" + bookingId + "&username=" + username;
    }

    /**
     * @Author Tara
     * @param model
     * @param session
     * @param bookingId
     * @return
     */
    @GetMapping("/choose-client")
    public String chooseClient(Model model, HttpSession session,
                               @RequestParam int bookingId) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (loggedIn) {
            model.addAttribute("loggedIn", loggedIn);
        } else {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        model.addAttribute("tattooArtist", tattooArtist);

        //Værdier som skal videre til view
        //Integer bookingId = (Integer) session.getAttribute("bookingId");
        model.addAttribute("bookingId", bookingId);

        System.out.println("choose client controller");
        System.out.println("booking ID: " + bookingId);
        System.out.println("Username: " + username);



        List<Client> sortedClients = clientService.getSortedListOfClients();
        //ADT Map is the result here, where the key is a character (first letter) and value is List<Client>
        //Uses TreeMap to maintain the natural order of the keys (that are sorted beforehand)
        //Uses stream to handle everything simulationaly
        //Uses collect (Collectors.groupingBy) to group the elements of teh stream based on first letter in first name
        Map<Character, List<Client>> groupedClients = sortedClients.stream()
                .collect(Collectors.groupingBy(client -> client.getFirstName().charAt(0),
                        TreeMap::new, Collectors.toList()));

        model.addAttribute("groupedClients", groupedClients);
        return "home/choose-client";
    }

    /**
     * @Author Tara
     * @param session
     * @param searchQuery
     * @param bookingId
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/search-for-existing-client")
    public String searchForExistingClient(HttpSession session,
                                          @RequestParam String searchQuery,
                                          @RequestParam int bookingId,
                                        /*  @RequestParam(required = false) int clientId, //clientId er ikke et must*/
                                          Model model,
                         RedirectAttributes redirectAttributes) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (!loggedIn) {
            return "redirect:/";
        }


        String username = (String) session.getAttribute("username");

        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        model.addAttribute("tattooArtist", tattooArtist);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("loggedIn", loggedIn);

        System.out.println("search for existing client controller");
        System.out.println("booking ID: " + bookingId);
        System.out.println("Username: " + username);

        model.addAttribute("searchQuery", searchQuery);
        //Checks (via regex) if there are only numbers, letters or a mix of both and acts accordingly
        if (searchQuery.matches("[0-9]+")) {
            model.addAttribute("searchType", "phoneNumber");
            List<Client> clientByNumber = clientService.getClientsByPhoneNumber(Integer.parseInt(searchQuery));
            model.addAttribute("results", clientByNumber);
        } else if (searchQuery.matches("[A-Za-z]+")) {
            model.addAttribute("searchType", "firstName");
            List<Client> clientByName = clientService.getClientsByFistName(searchQuery);
            model.addAttribute("results", clientByName);
        } else {
            redirectAttributes.addFlashAttribute("message", "Please enter a valid number or first name");
            return "redirect:/choose-client";
        }
        // return "redirect:/search-result2/" + clientId + "?bookingId" + bookingId +  "&username=" + username;
        return "home/search-result2";
    }

}
