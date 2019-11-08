package com.activity.smallgame.request;

import lombok.Data;

import java.util.Objects;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 16:58
 **/
@Data
public class UserProfileRequest {

    private String name;

    private String mobile;

    private String email;

    private String goal;

    private String authCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileRequest request = (UserProfileRequest) o;
        return Objects.equals(name, request.name) &&
                Objects.equals(authCode, request.authCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, authCode);
    }
}
