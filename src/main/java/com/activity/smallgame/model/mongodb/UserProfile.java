package com.activity.smallgame.model.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户信息
 * @author: Mr.dong
 * @create: 2019-08-20 17:29
 **/
@Data
@Document(collection = "user_profile")
@CompoundIndexes({
        @CompoundIndex(name = "idx_mobile_userCode", def = "{'mobile': 1, 'userCode': 1}")
})
public class UserProfile {

    @Indexed(unique = true)
    private String userCode;

    private String name;

    private String mobile;

    private String email;

    private Date createTime;

    private double multiple;

    private Long useAreaCode;

    public UserProfile() {
    }

    public UserProfile(String userCode, String name, String mobile, String email, Date createTime, Long useAreaCode) {
        this.userCode = userCode;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.createTime = createTime;
        this.useAreaCode = useAreaCode;
        this.multiple = 1;
    }
}
