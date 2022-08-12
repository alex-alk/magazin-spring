package com.magazin.controller;

import com.magazin.dao.*;
import com.magazin.model.Offer;
import com.magazin.utils.ArticlesUpload;
import com.magazin.utils.MainQuery;
import com.magazin.utils.OffersUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class OffersController {

    private final ArticleDAO articlesDAO;
    private final MainQuery mainQuery;
    private final OrderDAO ordersDAO;
    private final UserDAO clientsDAO;
    private final OfferDAO offersDAO;

    public OffersController(ArticleDAO articlesDAO, MainQuery mainQuery, OrderDAO ordersDAO, UserDAO clientsDAO, OfferDAO offersDAO) {
        this.articlesDAO = articlesDAO;
        this.mainQuery = mainQuery;
        this.ordersDAO = ordersDAO;
        this.clientsDAO = clientsDAO;
        this.offersDAO = offersDAO;
    }

    @RequestMapping(value="/admin/optiuni/oferte", method = RequestMethod.GET)
    public String viewOffers(Model model, ArticlesUpload file) {

        model.addAttribute("file", file);
        List<Offer> offers = offersDAO.getAll();
        model.addAttribute("offers", offers);
        return "/admin/optiuni/oferte";
    }

    @PostMapping(value="/admin/optiuni/oferte")
    public String addOffers(Model model,
                            @ModelAttribute(name="file") OffersUpload articlesUpload,
                            ArticlesUpload fileA,
                            @Value("${file-uploads}") String location,
                            Offer offer, HttpServletRequest request)throws IOException {

        MultipartFile file = articlesUpload.getFile();
        if (!file.getOriginalFilename().isEmpty()) {
            String fullFileName = file.getOriginalFilename();
            String fileName = fullFileName.substring(fullFileName.lastIndexOf("\\")+1, fullFileName.length());
            final File folder = new File(location + "/offers/");
            for (final File fileEntry : folder.listFiles()) {
                if(fileEntry.getName().equals(fileName)) {
                    List<Offer> offers = offersDAO.getAll();
                    model.addAttribute("offers", offers);
                    model.addAttribute("file", fileA);
                    model.addAttribute("msg", "Fișierul există deja");
                    return "/admin/optiuni/oferte";
                }
            }
            offer.setUrl(fileName);
            offersDAO.addOffer(offer);

            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    new File(location + "/offers/", fileName)));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
            model.addAttribute("file", fileA);
            model.addAttribute("msg", "Ofertă adăugată");
        }else {
            model.addAttribute("file", fileA);
            model.addAttribute("msg", "Selectează un fișier");
        }
        List<Offer> offers = offersDAO.getAll();
        model.addAttribute("offers", offers);
        model.addAttribute("file", fileA);
        return "/admin/optiuni/oferte";
    }
    @RequestMapping(value="/admin/optiuni/oferte/sterge",method = RequestMethod.GET)
    public String deleteOffer(Model model, @RequestParam("id")Long id,
                              HttpServletRequest request, Offer offer,
                              ArticlesUpload fileA,
                              @Value("${file-uploads}") String location) throws IOException {

        model.addAttribute("mainQuery", mainQuery);
        Offer articol = offersDAO.getOfferById(id);
        File file = new File(location + "/offers/" + articol.getUrl());
        file.delete();
        offer = offersDAO.getOfferById(id);
        offersDAO.deleteOffer(offer);
        model.addAttribute("offers", offersDAO.getAll());
        model.addAttribute("msg","Oferta a fost ștearsă.");
        model.addAttribute("file", fileA);
        return "/admin/optiuni/oferte";
    }
}
