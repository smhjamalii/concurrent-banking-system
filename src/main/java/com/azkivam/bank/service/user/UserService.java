package com.azkivam.bank.service.user;

import com.azkivam.bank.dto.User;
import com.azkivam.bank.service.CrudService;

import java.util.Optional;

public interface UserService extends CrudService<User> {

    Optional<User> findByUsername(String username);

}
