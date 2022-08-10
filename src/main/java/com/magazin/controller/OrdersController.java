package com.magazin.controller;

import com.magazin.dao.ArticleDAO;
import com.magazin.dao.OrderDAO;
import com.magazin.model.Article;
import com.magazin.model.Order;
import com.magazin.model.User;
import com.magazin.utils.CookieUtil;
import com.magazin.utils.OrdersForm;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {
    private final OrderDAO ordersDAO;
    private final ArticleDAO articlesDAO;

    public OrdersController(OrderDAO ordersDAO, ArticleDAO articlesDAO) {
        this.ordersDAO = ordersDAO;
        this.articlesDAO = articlesDAO;
    }

    @GetMapping(value="/cos")
    public String cosPageLink(User client, Model model, HttpServletRequest request, @AuthenticationPrincipal User user) {
        HttpSession session = request.getSession();
        if(user == null) {
            Cookie[] cookies = request.getCookies();
            String userId = CookieUtil.getCookieValue(cookies, "clientId");
            if(userId.equals("")) {
                model.addAttribute("client", client);
                model.addAttribute("msg","Pentru a vizualiza coșul de cumpărături trebuie sa vă logați.");
                return "/login";
            }
        }
        return "/cos";
    }

    @RequestMapping(value="/cos", method = RequestMethod.POST)
    public String cosPageFromDescription(Model model, @RequestParam("idA")Long id,
                                         HttpServletRequest request, User client, @AuthenticationPrincipal User user) {
        HttpSession session = request.getSession();

        if(user==null) {
            Cookie[] cookies = request.getCookies();
            String userId = CookieUtil.getCookieValue(cookies, "clientId");
            if(userId.equals("")) {
                model.addAttribute("client", client);
                model.addAttribute("msg","Pentru a adăuga produse trebuie sa vă logați.");
                return "/login";
            }
        }
        List<Article> articles;
        if(session.getAttribute("products")==null) {
            articles = new ArrayList<>();
        }else {
            articles = (List<Article>)session.getAttribute("products");
        }
        for(Article article:articles) {
            if(article.getId().longValue()==id.longValue()) {
                model.addAttribute("msg", "Articolul se află deja în coș.");
                model.addAttribute("article",articlesDAO.getArticleById(id));
                model.addAttribute("description", articlesDAO.getArticleById(id).getDescription());
                return "/descriere";
            }
        }
        articles.add(articlesDAO.getArticleById(id));
        session.setAttribute("products", articles);
        return "redirect:/cos";
    }
    @RequestMapping(value="/stergeDinCos", method = RequestMethod.GET)
    public String stergeDinCos(Model model, @RequestParam("idA")Long id,
                               @SessionAttribute(name="products")List<Article> articles) {
        for(Article art:articles) {
            if(art.getId().longValue() == id.longValue()) {
                articles.remove(art);
                return "redirect:/cos";
            }
        }
        return "";
    }

    @PostMapping(value="/validareComanda")
    public String validatePage(
            Model model,
            HttpServletRequest request,
            User client,
            OrdersForm ordersForm,
            @AuthenticationPrincipal User user) {

        HttpSession session = request.getSession();

        List<Article> products = (List<Article>)session.getAttribute("products");
        if(products==null) {
            model.addAttribute("msg", "Coșul de cumpărături este gol");
            return "/cos";
        }

        if(user==null) {
            Cookie[] cookies = request.getCookies();
            String userId = CookieUtil.getCookieValue(cookies, "clientId");
            if(userId.equals("")) {
                model.addAttribute("client", client);
                model.addAttribute("msg","Pentru a valida comanda trebuie sa vă logați.");
                return "/login";
            }
        }
        String id="";
        String quantity="";
        List<String> names = new ArrayList<>();
        List<String> prices = new ArrayList<>();
        List<String> quantities = new ArrayList<>();
        List<String> costs = new ArrayList<>();

        for(Article product:products){
            id+= product.getId()+" ";
            quantity+=request.getParameter(product.getId()+"")+" ";;
            names.add(product.getname());
            quantities.add(request.getParameter(product.getId()+""));
            prices.add(product.getPrice()+"");
            try {
                Integer.parseInt(request.getParameter(product.getId()+""));
            }catch(Exception e) {
                model.addAttribute("msg", "Cantitatea nu poate fi zero.");
                return "/cos";
            }
            costs.add((Integer.parseInt(request.getParameter(product.getId()+""))*product.getPrice())+"");
        }
        ordersForm.setQuantity(quantity);
        ordersForm.setArticleId(id);
        ordersForm.setClientId(user.getId()+"");

        Map<String,List<String>> tableSend = new LinkedHashMap<String, List<String>>();
        tableSend.put("names", names);
        tableSend.put("prices", prices);
        tableSend.put("quantities", quantities);
        tableSend.put("costs", costs);
        session.setAttribute("ordersForm", ordersForm);
        session.setAttribute("tableSend", tableSend);
        return "validare";
    }
    @RequestMapping(value = "/trimite", method = RequestMethod.POST)
    public String send(HttpServletRequest request, Model model, Order order, @SessionAttribute(name="ordersForm") OrdersForm ordersForm) {

        String quantity = ordersForm.getQuantity();
        String idArticle = ordersForm.getArticleId();
        String[] quantityArray = quantity.split(" ");
        String[] idArticlesArray = idArticle.split(" ");
        int idClientToEntity = Integer.parseInt(ordersForm.getClientId());
        int i = 0;
        for(String q: quantityArray) {
            int quantityToEntity = Integer.parseInt(q);
            Long idArticlesToEntity = Long.parseLong(idArticlesArray[i]);
            order.setClientId(idClientToEntity);
            order.setArticlesId(idArticlesToEntity);
            order.setQuantity(quantityToEntity);
            ordersDAO.addOrder(order);
            i++;
        }
        HttpSession session = request.getSession();
        session.removeAttribute("products");
        model.addAttribute("msg", "Comanda a fost trimisă. Produsele vor fi disponibile in magazin in 24h.");
        return "/succes";
    }
}