package com.magazin.controller;

import com.magazin.dao.ArticleDAO;
import com.magazin.dao.OrderDAO;
import com.magazin.dao.UserDAO;
import com.magazin.model.Article;
import com.magazin.model.Order;
import com.magazin.model.User;
import com.magazin.utils.ArticlesUpload;
import com.magazin.utils.MainQuery;
import com.magazin.utils.OrdersTable;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    private final ArticleDAO articlesDAO;
    private final MainQuery mainQuery;
    private final OrderDAO ordersDAO;
    private final UserDAO clientsDAO;

    public AdminController(ArticleDAO articlesDAO, MainQuery mainQuery, OrderDAO ordersDAO, UserDAO clientsDAO) {
        this.articlesDAO = articlesDAO;
        this.mainQuery = mainQuery;
        this.ordersDAO = ordersDAO;
        this.clientsDAO = clientsDAO;
    }

    @GetMapping(value="admin/optiuni")
    public String adminOptionsPage() {
        return "admin/optiuni/index";
    }

    @GetMapping(value="/admin/optiuni/editeaza")
    public String editArticlePage(Model model, ArticlesUpload articlesUpload, @RequestParam("id") Long id) {

        Article article = articlesDAO.getArticleById(id);
        model.addAttribute("articol", article);
        model.addAttribute("articolUpload", articlesUpload);
        return "admin/optiuni/editeaza";
    }
    @PostMapping(value="/admin/optiuni/editeaza")
    public String editArticle(Model model,
                              @ModelAttribute("articolUpload")ArticlesUpload articlesUpload,
                              @Value("${file-uploads}") String location) throws IOException {
        MultipartFile file = articlesUpload.getFile();
        Article article = articlesDAO.getArticleById(articlesUpload.getId());
        model.addAttribute("articol", article);
        if(articlesUpload.isNotValid()) {
            model.addAttribute("msg","Toate câmpurile sunt obligatorii");
            return "admin/optiuni/editeaza";
        }
        if (!file.getOriginalFilename().isEmpty()) {
            String fullFileName = file.getOriginalFilename();

            String fileName = fullFileName.substring(fullFileName.lastIndexOf("\\")+1, fullFileName.length());
            article.setCategory(articlesUpload.getCategory());
            article.setDescription(articlesUpload.getDescription());
            article.setImageURL(fileName);
            article.setName(articlesUpload.getName());
            article.setPrice(articlesUpload.getPrice());
            articlesDAO.updateArticle(article);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    new File(location, fileName)));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
            model.addAttribute("articol", article);
            model.addAttribute("msg", "Articol editat.");
        } else {
            model.addAttribute("msg", "Selectează un fișier");
        }
        return "admin/optiuni/editeaza";
    }

    @GetMapping(value="/admin/optiuni/adauga")
    public String articleUploadPage(Model model, ArticlesUpload articlesUpload) {
        model.addAttribute("articolUpload", articlesUpload);
        return "admin/optiuni/adauga";
    }

    @PostMapping(value="/admin/optiuni/articolUpload")
    public String articlesUpload(Model model, @ModelAttribute("articolUpload")ArticlesUpload articlesUpload,
                                 HttpServletRequest request, Article articol,
                                 @Value("${file-uploads}") String location) throws IOException {

        MultipartFile file = articlesUpload.getFile();
        if(articlesUpload.isNotValid()) {
            model.addAttribute("msg","Toate câmpurile sunt obligatorii");
            return "admin/optiuni/adauga";
        }
        if (!file.getOriginalFilename().isEmpty()) {
            String fullFileName = file.getOriginalFilename();
            String fileName = fullFileName.substring(fullFileName.lastIndexOf("\\")+1, fullFileName.length());

            final File folder = new File(location);
            for (final File fileEntry : folder.listFiles()) {
                if(fileEntry.getName().equals(fileName)) {
                    model.addAttribute("msg", "Fișierul există deja");
                    return "admin/optiuni/adauga";
                }
            }
            articol.setCategory(articlesUpload.getCategory());
            articol.setDescription(articlesUpload.getDescription());
            articol.setImageURL(fileName);
            articol.setName(articlesUpload.getName());
            articol.setPrice(articlesUpload.getPrice());
            articlesDAO.addArticle(articol);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    new File(location, fileName)));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
            model.addAttribute("msg", "Articol adăugat");
        } else {
            model.addAttribute("msg", "Selectează un fișier");
        }
        return "admin/optiuni/adauga";
    }

    //delete article
    @GetMapping(value="/admin/optiuni/stergere")
    public String deleteArticlePage(Model model) {
        model.addAttribute("articles",articlesDAO.getAll());
        model.addAttribute("mainQuery", mainQuery);
        return "admin/optiuni/stergere";
    }
    @RequestMapping(value="/admin/optiuni/stergere/cauta",method = RequestMethod.GET)
    public String deleteArticlePageSearch(Model model, @ModelAttribute("mainQuery")MainQuery mainQuery){

        model.addAttribute("articles", articlesDAO.getArticlesByMainQuery(mainQuery));
        model.addAttribute("mainQuery", mainQuery);
        return "admin/optiuni/stergere";
    }

    @RequestMapping(value="/admin/optiuni/sterge",method = RequestMethod.GET)
    public String deleteArticle(Model model, @RequestParam("id")Long id, HttpServletRequest request) {
        ServletContext sc = request.getServletContext();

        model.addAttribute("mainQuery", mainQuery);
        Article article = articlesDAO.getArticleById(id);
        String fileURL = sc.getRealPath("/") + article.getImageURL();
        File file = new File(fileURL);
        file.delete();
        articlesDAO.deleteArticle(article);
        model.addAttribute("articole",articlesDAO.getAll());
        return "redirect:/admin/optiuni/stergere";
    }

    //display articles to be edited
    @GetMapping(value="/admin/optiuni/editare")
    public String editare(Model model) {
        model.addAttribute("mainQuery", mainQuery);
        model.addAttribute("articles", articlesDAO.getAll());
        return "admin/optiuni/editare";
    }

    @GetMapping(value="/admin/optiuni/editare/cauta")
    public String editareCauta(Model model, @ModelAttribute("mainQuery")MainQuery mainQuery) {
        model.addAttribute("articles", articlesDAO.getArticlesByMainQuery(mainQuery));
        return "admin/optiuni/editare";
    }

    @GetMapping(value="/admin/optiuni/comenzi")
    public String displayComenzi(Model model, HttpServletRequest request) {

        List<Order> orders;
        orders = ordersDAO.getAll();
        OrdersTable ordersTable;
        List<OrdersTable> ordersTableList = new ArrayList<>();
        for(Order order:orders) {
            ordersTable= new OrdersTable();
            User user = clientsDAO.getClientById(order.getClientiId());
            ordersTable.setEmail(user.getEmail());
            ordersTable.setName(user.getFamilyName());
            ordersTable.setTel(user.getTel());
            ordersTable.setArtName(articlesDAO.getArticleById(order.getArticlesId()).getName());
            ordersTable.setQuantity(order.getQuantity()+"");
            ordersTable.setOrderId(order.getId()+"");
            ordersTableList.add(ordersTable);
        }
        model.addAttribute("ordersTableList", ordersTableList);
        return "admin/optiuni/comenzi";
    }
    @GetMapping(value="/admin/optiuni/comenzi/sterge")
    public String deleteComenzi(Model model, @RequestParam("id")Long id, HttpServletRequest request) {
        List<Order> orders;
        ordersDAO.deleteOrder(ordersDAO.getOrderById(id));
        orders = ordersDAO.getAll();
        OrdersTable ordersTable;
        List<OrdersTable> ordersTableList = new ArrayList<>();
        for(Order order:orders) {
            ordersTable= new OrdersTable();
            ordersTable.setEmail(clientsDAO.getClientById(order.getClientiId()).getEmail());
            ordersTable.setName(clientsDAO.getClientById(order.getClientiId()).getFamilyName());
            ordersTable.setTel(clientsDAO.getClientById(order.getClientiId()).getTel());
            ordersTable.setArtName(articlesDAO.getArticleById(order.getArticlesId()).getName());
            ordersTable.setQuantity(order.getQuantity()+"");
            ordersTable.setOrderId(order.getId()+"");
            ordersTableList.add(ordersTable);
        }
        model.addAttribute("ordersTableList", ordersTableList);
        return "admin/optiuni/comenzi";
    }
}
