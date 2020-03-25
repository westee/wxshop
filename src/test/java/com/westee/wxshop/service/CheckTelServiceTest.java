package com.westee.wxshop.service;

import com.westee.wxshop.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckTelServiceTest {
    public static AuthController.TelAndCode VALID_PARAMS = new AuthController.TelAndCode("13812345678", null);
    public static AuthController.TelAndCode VALID_PARAMS_CODE = new AuthController.TelAndCode("13812345678", "000000");
    public static AuthController.TelAndCode EMPTY_PARAMS = new AuthController.TelAndCode(null, null);

    @Test
    public void returnTrueIfValid() {
        Assertions.assertTrue(new CheckTelService()
                .verifyTelParams(VALID_PARAMS));
    }

    @Test
    public void returnFalseIfEmpty() {
        Assertions.assertFalse(new CheckTelService().verifyTelParams(EMPTY_PARAMS));
        Assertions.assertFalse(new CheckTelService().verifyTelParams(null));
    }

}