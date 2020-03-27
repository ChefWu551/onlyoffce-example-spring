package com.example.onlineEditorFront.model;

import lombok.Data;

/**
 * @program: OnlineEditors
 * @description:
 * @author: wuyuefeng
 * @email: 565948592@qq.com
 * @create: 2019-04-22 14:28
 **/
@Data
public class UserInfo {
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 姓名
     */
    private String name;
    /**
     * 头像id
     */
    private Integer avatarId;

    /**
     * 头像url
     */
    private String avatarUrl;
}
