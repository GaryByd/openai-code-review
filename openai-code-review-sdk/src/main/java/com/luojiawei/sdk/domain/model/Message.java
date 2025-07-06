package com.luojiawei.sdk.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private String touser = "o7Rw_vq2r5YUtH2zsJlyqvty4aMA";
    private String template_id = "dJEtVyeZuMDzdstjzdv6sKylDnAE0awqN4ODJFNNgsI";
    private String url = "https://github.com/GaryByd/openai-code-review-log/blob/main/2025-07-06/1324bfa1-e72b-484a-b6e1-f1c3fe405572.md";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;
            {
                put("value", value);
            }
        });
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }

}
