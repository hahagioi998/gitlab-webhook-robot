package io.github.ealenxie.gitlab.webhook.dto;

/**
 * Created by EalenXie on 2021/12/1 9:40
 */
public class Author {


    private String name;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}