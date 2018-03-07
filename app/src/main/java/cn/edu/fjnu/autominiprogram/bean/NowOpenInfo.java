package cn.edu.fjnu.autominiprogram.bean;

/**
 * Created by gaofei on 2018/2/1.
 * 当前开奖信息
 */

public class NowOpenInfo {
    private String imgUrl;
    private String title;
    private String no;
    private String openDate;
    private String number;
    private String head;
    private String tip;
    public NowOpenInfo(){

    }

    public NowOpenInfo(String title, String no, String openDate, String number, String head, String tip) {

        this.title = title;
        this.no = no;
        this.openDate = openDate;
        this.number = number;
        this.head = head;
        this.tip = tip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "NowOpenInfo{" +
                "title='" + title + '\'' +
                ", no='" + no + '\'' +
                ", openDate='" + openDate + '\'' +
                ", number='" + number + '\'' +
                ", head='" + head + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
