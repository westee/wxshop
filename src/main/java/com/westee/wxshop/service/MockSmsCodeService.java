package com.westee.wxshop.service;

public class MockSmsCodeService implements SmsCodeService {
    @Override
    public String sendSmsCode(String tel) {
        return "000000";
    }
}
