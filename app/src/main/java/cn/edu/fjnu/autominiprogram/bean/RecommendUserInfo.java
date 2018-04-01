package cn.edu.fjnu.autominiprogram.bean;

/**
 * Created by gaofei on 2018/3/31.
 * 推荐的用户信息
 */

public class RecommendUserInfo {
    private int userId;
    private String phone;
    private String nickName;
    private int type;
    private String restime;
    private double canGet;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRestime() {
        return restime;
    }

    public void setRestime(String restime) {
        this.restime = restime;
    }

    public double getCanGet() {
        return canGet;
    }

    public void setCanGet(double canGet) {
        this.canGet = canGet;
    }
}
