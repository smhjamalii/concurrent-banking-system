package com.azkivam.bank.service.user;

import com.azkivam.bank.dto.User;
import com.azkivam.bank.dto.mapper.UserMapper;
import com.azkivam.bank.model.UserModel;
import com.azkivam.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User dto) {
        UserModel model = UserMapper.toModel.apply(dto);
        userRepository.save(model);

        return UserMapper.toDTO.apply(model);
    }

    @Override
    public boolean delete(User dto) {
        try {

            userRepository.deleteById(dto.id());
            return true;
        } catch (Exception e) {

            log.info(e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<UserModel> model = userRepository.findById(id);
        if(model.isPresent()) {

            return model.map(UserMapper.toDTO);
        } else {

            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<UserModel> model = userRepository.findByUsername(username);
        if(model.isPresent()) {

            return model.map(UserMapper.toDTO);
        } else {

            return Optional.empty();
        }
    }

}
