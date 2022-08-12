package com.magazin.model;

import javax.persistence.*;

@Entity
@Table(name="orders")
public class Order {

    @Column(name="articles_id")
    private Long articlesId;

    private int quantity;

    @Column(name="client_id")
    private Long clientId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getArticlesId() {
        return this.articlesId;
    }

    public void setArticlesId(Long articlesId) {
        this.articlesId = articlesId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getClientiId() {
        return this.clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}