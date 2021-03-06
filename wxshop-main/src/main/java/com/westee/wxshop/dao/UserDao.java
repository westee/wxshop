package com.westee.wxshop.dao;

import com.westee.wxshop.generate.User;
import com.westee.wxshop.generate.UserExample;
import com.westee.wxshop.generate.UserMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
@Service
public class UserDao {
    private final UserMapper userMapper;

    @Autowired
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(User user) {
            userMapper.insert(user);
    }

    public User getUserByTel(String tel) {
            UserExample example = new UserExample();
            example.createCriteria().andTelEqualTo(tel);
            return userMapper.selectByExample(example).get(0);
    }
}
