package com.westee.wxshop.service;

import com.westee.wxshop.UserDao;
import com.westee.wxshop.generate.User;
import org.apache.ibatis.exceptions.PersistenceException;
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

        try {
            userDao.insertUser(user);
        } catch (PersistenceException e) {
            return userDao.getUserByTel(tel);
        }
        return user;
    }

    /**
     * 根据手机号查找用户，用户不存在则返回null
     * @param tel 用户手机号
     * @return User
     */
    public User getUserByTel(String tel) {
        return userDao.getUserByTel(tel);
    }
}
