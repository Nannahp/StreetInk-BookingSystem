package com.example.streetinkbookingsystem.services;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.mail.SimpleMailMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
    public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * @param clientEmail used to send the email to this address
     * @param context     used to set variables in the email template
     * @author Nanna
     * @summary Sends a confirmation mail to the client with the relevant booking details.
     */
    public void sendConfirmationMail(String clientEmail, Context context) {
        String processedHTMLTemplate = templateEngine.process("home/confirmation-mail", context);
        // Start preparing the email
        MimeMessagePreparator preparator = message -> {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom("nannahofgaard@gmail.com"); //change to correct email
            helper.setTo(clientEmail);
            helper.setSubject("Booking Confirmation");
            helper.setText(processedHTMLTemplate, true);
        };

        javaMailSender.send(preparator); //send the email
        System.out.println("sent");
    }


    public void sendEmail(String recipient, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    //Used regex to verify that the email format is correct
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
