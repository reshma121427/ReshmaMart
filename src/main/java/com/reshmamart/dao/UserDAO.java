package com.reshmamart.dao;

import com.reshmamart.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User create(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    int countAll();
    boolean existsByEmail(String email);
}
