package com.magazin.dao;

import com.magazin.model.Offer;
import com.magazin.repository.OfferRepository;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Service
public class OfferDAO {
    private final OfferRepository offerRepository;

    public OfferDAO(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> getAll(){
        return offerRepository.findAll();
    }
    public Offer getOfferById(Long id){
        Optional<Offer> offer = offerRepository.findById(id);
        if (offer.isPresent()) {
            return offer.get();
        } else {
            throw new EntityNotFoundException("Offer not found");
        }
    }
    public void addOffer (Offer offer) {
        offerRepository.save(offer);
    }
    public void deleteOffer (Offer offer) {
        offerRepository.delete(offer);
    }
}
