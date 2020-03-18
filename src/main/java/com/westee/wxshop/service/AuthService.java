package com.westee.wxshop.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final CheckAuthCodeService checkAuthCodeService;
    private final MockSmsCodeService mockSmsCodeService;

    public AuthService(UserService userService,
                       CheckAuthCodeService checkAuthCodeService,
                       MockSmsCodeService mockSmsCodeService) {
        this.userService = userService;
        this.checkAuthCodeService = checkAuthCodeService;
        this.mockSmsCodeService = mockSmsCodeService;
    }

    public void sendAuthCode(String tel) {

        userService.createUserIfNotExist(tel);
        String correctCode = mockSmsCodeService.sendSmsCode(tel);
        checkAuthCodeService.addCode(tel, correctCode);
    }
}
