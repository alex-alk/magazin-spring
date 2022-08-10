package com.magazin.dao;

import com.magazin.model.Article;
import com.magazin.repository.ArticleRepository;
import com.magazin.utils.MainQuery;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleDAO {
    private final ArticleRepository articleRepository;
    private final EntityManager em;

    public ArticleDAO(ArticleRepository articleRepository, EntityManager em) {
        this.articleRepository = articleRepository;
        this.em = em;
    }

    public List<Article> getAll(){
        return articleRepository.findAll();
    }

    public Article getArticleById(Long id){
        Optional<Article> article = articleRepository.findById(id);
        if (article.isPresent()) {
            return article.get();
        } else {
         throw new EntityNotFoundException("Article not found");
        }
    }

    public List<Article> getArticlesByMainQuery(MainQuery mainQuery){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Article> q = cb.createQuery(Article.class);
        Root<Article> c = q.from(Article.class);

        if(mainQuery.nothingSelected()){
            q.select(c).where(cb.like(c.get("name"), "%"+mainQuery.getText()+"%"))
                    .orderBy(cb.asc(c.get(mainQuery.orderBy())));
        }else {
            q.select(c).where(
                            cb.or(
                                    cb.equal(c.get("category"), mainQuery.getPesti()),
                                    cb.equal(c.get("category"), mainQuery.getHrana()),
                                    cb.equal(c.get("category"), mainQuery.getAcv()),
                                    cb.equal(c.get("category"), mainQuery.getAccesorii())
                            ),cb.like(c.get("name"), "%"+mainQuery.getText()+"%")
                    )
                    .orderBy(cb.asc(c.get(mainQuery.orderBy())));
        }
        TypedQuery<Article> articolQuery = em.createQuery(q)
                .setFirstResult(mainQuery.getOffset())
                .setMaxResults(mainQuery.getLimit());
        List<Article> articles = articolQuery.getResultList();
        em.close();
        return articles;
    }

    public List<Article> getAllArticlesByMainQuery(MainQuery mainQuery){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Article> q = cb.createQuery(Article.class);
        Root<Article> c = q.from(Article.class);

        if(mainQuery.nothingSelected()){
            q.select(c).where(cb.like(c.get("name"), "%"+mainQuery.getText()+"%"))
                    .orderBy(cb.asc(c.get(mainQuery.orderBy())));
        }else {
            q.select(c).where(
                            cb.or(
                                    cb.equal(c.get("category"), mainQuery.getPesti()),
                                    cb.equal(c.get("category"), mainQuery.getHrana()),
                                    cb.equal(c.get("category"), mainQuery.getAcv()),
                                    cb.equal(c.get("category"), mainQuery.getAccesorii())
                            ),cb.like(c.get("name"), "%"+mainQuery.getText()+"%")
                    )
                    .orderBy(cb.asc(c.get(mainQuery.orderBy())));
        }
        TypedQuery<Article> articolQuery = em.createQuery(q);
        List<Article> articles = articolQuery.getResultList();
        em.close();
        return articles;
    }
    public void addArticle (Article article) {
        articleRepository.save(article);
    }
    public void updateArticle (Article article) {
        articleRepository.save(article);
    }
    public void deleteArticle (Article a) {
        articleRepository.delete(a);
    }
}
