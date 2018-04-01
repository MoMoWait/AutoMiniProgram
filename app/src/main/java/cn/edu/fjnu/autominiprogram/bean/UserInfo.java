package cn.edu.fjnu.autominiprogram.bean;

/**
 * Created by gaofei on 2017/10/5.
 * 用户信息
 */

public class UserInfo {
    /**
     * 用户ID
     */
    private int userId;
    private String userName;
    private String passwd;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户类型
     */
    private int type;
    /**
     * 用户状态
     */
    private int state;
    /**
     * 发单数
     */
    private int singleNum;
    /**
     * 群数
     */
    private int groupNum;
    /**
     * 推荐人
     */
    private int spreader;
    /**
     * 获得金额
     */
    private double money;
    /**
     * 推广位
     */
    private String spreadSpace;
    /**
     * 可以拿到多少钱
     */
    private double canGet;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSingleNum() {
        return singleNum;
    }

    public void setSingleNum(int singleNum) {
        this.singleNum = singleNum;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public int getSpreader() {
        return spreader;
    }

    public void setSpreader(int spreader) {
        this.spreader = spreader;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpreadSpace() {
        return spreadSpace;
    }

    public void setSpreadSpace(String spreadSpace) {
        this.spreadSpace = spreadSpace;
    }

    public double getCanGet() {
        return canGet;
    }

    public void setCanGet(double canGet) {
        this.canGet = canGet;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", passwd='" + passwd + '\'' +
                ", phone='" + phone + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", singleNum=" + singleNum +
                ", groupNum=" + groupNum +
                ", spreader=" + spreader +
                ", money=" + money +
                ", canGet=" + canGet +
                '}';
    }
}
