package com.westee.wxshop.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final CheckSmsAuthCodeService checkSmsAuthCodeService;
    private final MockSmsCodeService mockSmsCodeService;

    public AuthService(UserService userService,
                       CheckSmsAuthCodeService checkSmsAuthCodeService,
                       MockSmsCodeService mockSmsCodeService) {
        this.userService = userService;
        this.checkSmsAuthCodeService = checkSmsAuthCodeService;
        this.mockSmsCodeService = mockSmsCodeService;
    }

    /**
     * 发送验证码
     * @param tel 手机号码
     */
    public void sendAuthCode(String tel) {
        userService.createUserIfNotExist(tel);
        String correctCode = mockSmsCodeService.sendSmsCode(tel);
        checkSmsAuthCodeService.addCode(tel, correctCode);
    }
}
