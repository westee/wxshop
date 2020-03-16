package com.westee.wxshop.service;

import com.westee.wxshop.UserDao;
import com.westee.wxshop.generate.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        userDao.insertUser(user);
        return user;
    }
}
