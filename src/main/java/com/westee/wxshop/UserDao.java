package com.westee.wxshop;

import com.westee.wxshop.generate.User;
import com.westee.wxshop.generate.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDao {
    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public UserDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertUser(User user) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insert(user);
        }

    }
}
