package com.azkivam.bank.dto.mapper;

import com.azkivam.bank.dto.User;
import com.azkivam.bank.model.UserModel;

import java.util.function.Function;

public class UserMapper {

    public static final Function<UserModel, User> toDTO =
            model -> new User(model.getId(), model.getUsername(), model.getPassword(),
                                model.getFirstname(), model.getLastname(), model.getVersion());

    public static final Function<User, UserModel> toModel =
            dto -> new UserModel(dto.id(), dto.username(), dto.password(), dto.firstname(), dto.lastname(), dto.version());

}
