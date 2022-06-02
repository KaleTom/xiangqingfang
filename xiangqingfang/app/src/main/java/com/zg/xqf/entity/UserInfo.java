package com.zg.xqf.entity;

import com.zg.xqf.util.Utils;
import com.zg.xqf.zego.Zego;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class UserInfo extends BaseEntity {
    public String uid;
    public String name;
    public boolean man;
    public String avatar;
    public int age;
    public String province;
    public boolean isMuted;

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMan() {
        return man;
    }

    public void setMan(boolean man) {
        this.man = man;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
//    public String mergeUserName() {
//        StringBuffer sb = new StringBuffer();
//        sb.append(uid).append("@@").append(name).append("@@").append(isMan).append("@@").append(avatar).append("@@").append(age).append("@@").append(province);
//        return sb.toString();
//    }
//
//    public void splitUserName(String userName) {
//        String[] data = userName.split("@@");
//        uid = data[0];
//        name = data[1];
//        isMan = Boolean.valueOf(data[2]);
//        avatar = data[3];
//        age = Integer.valueOf(data[4]);
//        province = data[5];
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || uid == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return uid.equals(userInfo.uid);
    }

    @Override
    public int hashCode() {
        if (uid == null)
            return super.hashCode();
        return uid.hashCode();

    }

    public static UserInfo random() {
        UserInfo user = new UserInfo();
        Random random = new Random();
        random.nextInt();
        user.uid = new Date().getTime() + "" + random.nextInt();
        user.man = Math.random() > 0.5;
        user.name = Utils.randomName(user.man);
        user.avatar = "";
        user.age = Zego.randInt(18, 40);
        user.province = Utils.randProvince();
        user.isMuted = false;
        return user;

    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
