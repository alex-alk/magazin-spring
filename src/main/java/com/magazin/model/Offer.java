package com.magazin.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Entity
@Table(name="offers")
//@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class Offer {

    //@XmlAttribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@XmlElement
    private String url;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}