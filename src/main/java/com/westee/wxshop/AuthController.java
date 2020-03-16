package com.westee.wxshop;

import com.westee.wxshop.service.AuthService;
import com.westee.wxshop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode){
        authService.sendAuthCode(telAndCode.getTel());
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode){
        UsernamePasswordToken token = new UsernamePasswordToken(telAndCode.getTel(), telAndCode.getCode());

        SecurityUtils.getSubject().login(token);
    }

    public static class TelAndCode {
        private String tel;
        private String code;

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
