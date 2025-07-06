package com.luojiawei.sdk.types.utils;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WXAccessTokenUtils {

    private static final String APPID = "wx7614eea931e4d374";
    private static final String SECRET = "da9a2e3c6412a99b82f196c14e453ca0";
    private static final String GRANT_TYPE = "client_credential";
    private static final String URL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";

    public static String getAccessToken() {
        try {
            String urlString = String.format(URL_TEMPLATE, GRANT_TYPE, APPID, SECRET);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println("Response: " + response.toString());

                Token token = JSON.parseObject(response.toString(), Token.class);

                return token.getAccess_token();
            } else {
                System.out.println("GET request failed");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Token {
        private String access_token;
        private Integer expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }
    }


}
