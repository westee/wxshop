package com.westee.wxshop.entity;

public class LoginResponse {
    private boolean login;
    private User user;

    public static LoginResponse notLogin() {
        return new LoginResponse(false, null);
    }

    public static LoginResponse AlreadyLogin(User user) {
        return new LoginResponse(true, user);
    }

    // json序列化需要一个空构造器
    public LoginResponse() {}

    public LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }

}
