package com.westee.wxshop.service;

import com.westee.wxshop.controller.AuthController;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CheckTelService {
    private static Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");
    /**
     * 验证参数是否合法
     *
     * @param param 输入的参数
     * @return true == 合法；false == 不合法
     */
    public boolean verifyTelParams(AuthController.TelAndCode param) {
        if (param == null) {
            return false;
        } else if (param.getTel() == null) {
            return false;
        } else {
            return  TEL_PATTERN.matcher(param.getTel()).find();
        }
    }
}
