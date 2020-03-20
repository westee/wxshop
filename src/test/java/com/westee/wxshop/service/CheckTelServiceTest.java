package com.westee.wxshop.service;

import com.westee.wxshop.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckTelServiceTest {
    private static AuthController.TelAndCode VALID_PARAMS = new AuthController.TelAndCode("13812345678", null);
    private static AuthController.TelAndCode EMPTY_PARAMS = new AuthController.TelAndCode(null, null);

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