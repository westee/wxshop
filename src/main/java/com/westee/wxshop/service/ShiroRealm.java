package com.westee.wxshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealm extends AuthorizingRealm {

    private final CheckAuthCodeService checkAuthCodeService;

    @Autowired
    public ShiroRealm(CheckAuthCodeService checkAuthCodeService) {
        this.checkAuthCodeService = checkAuthCodeService;
        this.setCredentialsMatcher(new CredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
                 return authenticationToken.getCredentials().equals(authenticationInfo.getCredentials());
            }
        });
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tel = (String) token.getPrincipal();
        String correctCode = checkAuthCodeService.getCorrectCode(tel);
        return new SimpleAuthenticationInfo(tel, correctCode, getName());
    }
}
