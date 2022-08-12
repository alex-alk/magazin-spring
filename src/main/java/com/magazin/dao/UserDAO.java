package com.magazin.dao;

import com.magazin.model.Article;
import com.magazin.model.User;
import com.magazin.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.Optional;

@Service
public class UserDAO {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserDAO(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User getClientByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getClientById(Long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    public void saveClient (User user) {
        user.setRole("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
