package com.westee.wxshop.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CheckAuthCodeService {
    private Map<String, String> telToCorrectCode = new ConcurrentHashMap<>();

    public void addCode(String tel, String correctCode) {
        telToCorrectCode.put(tel, correctCode);
    }

    public String getCorrectCode(String tel) {
         return telToCorrectCode.get(tel);
    }
}
