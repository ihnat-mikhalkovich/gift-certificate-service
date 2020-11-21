package com.epam.esm.dao;

import com.epam.esm.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    /**
     * Returns all users.
     *
     * @return all users
     */
    List<User> findAll(int page, int size);

    /**
     * Retrieves a user by its id.
     *
     * @param id unique user identifier.
     * @return the user with the given id or {@literal Optional#empty()} if none found.
     */
    Optional<User> findById(long id);

    /**
     * Retrieves a user by its email.
     *
     * @param email unique user identifier.
     * @return the user with the given email or {@literal Optional#empty()} if none found.
     */
    Optional<User> findByEmail(String email);
}