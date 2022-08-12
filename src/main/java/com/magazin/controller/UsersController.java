package com.magazin.controller;

import com.magazin.dao.UserDAO;
import com.magazin.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@Controller
public class UsersController {
    private final UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    public UsersController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value="/inregistrare")
    public String registerPage(Model model, User client) {
        model.addAttribute("client", client);
        return "register";
    }

    @PostMapping(value="/inregistrare")
    public String register(Model model, @ModelAttribute("client") User client, HttpServletRequest request) {

        if (client.isNotValid()) {
            model.addAttribute("msg", "Toate câmpurile sunt obligatorii");
            return "register";
        }
        if(!client.getPassword().equals(request.getParameter("confirmation"))) {
            model.addAttribute("msg", "Parolele nu corespund.");
            return "register";
        }
        User clientDb = userDAO.getClientByEmail(client.getEmail());
        if(clientDb != null) {
            model.addAttribute("msg", "Adresa de email există deja.");
            return "register";
        }

        userDAO.saveClient(client);

        client.setPassword(request.getParameter("confirmation"));
        model.addAttribute("client", client);
        model.addAttribute("msg", "Ați fost înregistrat cu succes.");
        return "login";
    }

    @GetMapping(value="/login")
    public String loginPage() {
        return "login";
    }

//    @PostMapping(value="/intra")
//    public String login(Model model, @ModelAttribute("client")User client, HttpServletResponse response,
//                        HttpServletRequest request) {
//        User clientDb = userDAO.getClientByEmail(client.getEmail());
//        if(clientDb != null && clientDb.getPassword().equals(client.getPassword())){
////            HttpSession session1 = request.getSession();
////            session1.invalidate();
////            HttpSession session = request.getSession();
////            session.setAttribute("user", clientDb);
////            Cookie cookie = new Cookie("clientId", clientDb.getId()+"");
////            cookie.setMaxAge(300);
////            response.addCookie(cookie);
//            return "redirect:/";
//        }
//        model.addAttribute("msg", "Adresa de email nu corespunde cu parola");
//        return "login";
//    }
    @RequestMapping(value="/recuperare", method = RequestMethod.GET)
    public String recoveryPage(User client, Model model) {
        model.addAttribute("client", client);
        return "recuperare";
    }
    @RequestMapping(value="/recuperare",method = RequestMethod.POST)
    public String rec(Model model, @ModelAttribute("client")User client, User clientForm) {
//        User clientBaza = userDAO.getClientByEmail(client.getEmail());
//        model.addAttribute("client", clientForm);
//        String email = clientBaza.getEmail();
//        if (!email.isEmpty()) {
//
//            final String username = "alextestmail.12@gmail.com";
//            final String password = "parola.12";
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class",
//                    "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.port", "465");
//
//            Session session = Session.getDefaultInstance(props,
//                    new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication(username,password);
//                        }
//                    });
//            try {
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(username));
//                message.setRecipients(Message.RecipientType.TO,
//                        InternetAddress.parse(email));
//                message.setSubject("Parola");
//                message.setText("Parola: " + clientBaza.getPassword());
//                Transport.send(message);
//            } catch (MessagingException e) {
//                throw new RuntimeException(e);
//            }
//
//            model.addAttribute("msg", "Parola a fost trimisă pe adresa de email.");
//            return "login";
//        }
        model.addAttribute("msg", "Adresa de email nu există.");
        return "recuperare";
    }
}
