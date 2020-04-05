package com.westee.wxshop;

import com.westee.wxshop.entity.LoginResponse;
import com.westee.wxshop.generate.User;
import com.westee.wxshop.service.AuthService;
import com.westee.wxshop.service.CheckTelService;
import com.westee.wxshop.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final CheckTelService checkTelService;

    public AuthController(AuthService authService, CheckTelService checkTelService) {
        this.authService = authService;
        this.checkTelService = checkTelService;
    }

    @CrossOrigin
    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        if (checkTelService.verifyTelParams(telAndCode)) {
            authService.sendAuthCode(telAndCode.getTel());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(telAndCode.getTel(), telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

    @PostMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    @GetMapping("/status")
    public Object loginStatus() {
        if (UserContext.getCurrentUser() == null) {
            return LoginResponse.notLogin();
        } else {
            return LoginResponse.AlreadyLogin(UserContext.getCurrentUser());
        }
    }

    public static class TelAndCode {
        private String tel;
        private String code;

        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }

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
