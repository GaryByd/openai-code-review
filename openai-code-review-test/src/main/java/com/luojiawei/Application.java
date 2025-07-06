package com.luojiawei;

import com.luojiawei.sdk.types.utils.WXAccessTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class Application {
    public static void main(String[] args) {
        WXAccessTokenUtils.getAccessToken();
    }
}
