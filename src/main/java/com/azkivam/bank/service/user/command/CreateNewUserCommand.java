package com.azkivam.bank.service.user.command;

import com.azkivam.bank.dto.User;
import com.azkivam.bank.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateNewUserCommand implements UserOperationCommand {

    private final UserService userService;
    private final User user;

    @Override
    public void execute() {
        userService.save(user);
    }
}
