package com.westee.wxshop.service;

import com.westee.wxshop.dao.UserDao;
import org.springframework.dao.DuplicateKeyException;
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
            // 并发情况下不能通过查询来判断用户是否存在，电话号码被设置为唯一，因此直接插入并捕捉错误即可。
            userDao.insertUser(user);
        } catch (DuplicateKeyException e) {
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
