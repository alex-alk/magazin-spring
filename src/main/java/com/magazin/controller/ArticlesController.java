package com.magazin.controller;

import com.magazin.dao.ArticleDAO;
import com.magazin.dao.UserDAO;
import com.magazin.dao.OfferDAO;
import com.magazin.model.Article;
import com.magazin.model.Offer;
import com.magazin.model.User;
import com.magazin.utils.MainQuery;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ArticlesController {

    private final ArticleDAO articlesDAO;
    private final OfferDAO offersDAO;
    private final UserDAO userDAO;

    public ArticlesController(ArticleDAO articlesDAO, OfferDAO offersDAO, UserDAO userDAO) {
        this.articlesDAO = articlesDAO;
        this.offersDAO = offersDAO;
        this.userDAO = userDAO;
    }

    @GetMapping(value="")
    public String home(Model model,
                       MainQuery mainQuery,
                       @AuthenticationPrincipal User user) {

        List<Article> allArticles = articlesDAO.getAll();
        List<Article> articles;

        if (allArticles.size() > mainQuery.getLimit()) {
            articles = allArticles.subList(0, mainQuery.getLimit());
        } else {
            articles = allArticles;
        }

        int pages = (int) Math.ceil((double)allArticles.size()/(double)mainQuery.getLimit());
        List<Offer> offers = offersDAO.getAll();

        model.addAttribute("user", user);
        model.addAttribute("offers", offers);
        model.addAttribute("page", mainQuery.getPage());
        model.addAttribute("pages", pages);
        model.addAttribute("articles", articles);
        model.addAttribute("mainQuery", mainQuery);
        return "index";
    }

    @GetMapping(value="/cauta")
    public String cauta(Model model, @ModelAttribute("mainQuery")MainQuery mainQuery) {
        List<Article> articles = articlesDAO.getArticlesByMainQuery(mainQuery);
        List<Article> allArticles = articlesDAO.getAllArticlesByMainQuery(mainQuery);
        int pages = (int) Math.ceil((double)allArticles.size()/(double)mainQuery.getLimit());
        List<Offer> offers = offersDAO.getAll();
        model.addAttribute("offers", offers);
        model.addAttribute("page", mainQuery.getPage());
        model.addAttribute("pages", pages);
        model.addAttribute("articles", articles);
        model.addAttribute("mainQuery", mainQuery);
        return "index";
    }


    @GetMapping(value="/descriere")
    public String description(Model model, @RequestParam("id") Long id, @AuthenticationPrincipal User user) {
        model.addAttribute("article", articlesDAO.getArticleById(id));
        model.addAttribute("user", user);
        return "descriere";
    }

}
