package com.westee.wxshop.service;

import com.westee.wxshop.generate.User;

public class UserContext {
    private static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static User getCurrentUser(){
        return currentUser.get();
    }

    public static void setCurrentUser(User user){
        currentUser.set(user);
    }
}
