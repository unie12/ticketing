package com.example.ticketing.service.user;

import com.example.ticketing.model.user.User;


public interface UserService {
    User findUserById(Long userId);

}
