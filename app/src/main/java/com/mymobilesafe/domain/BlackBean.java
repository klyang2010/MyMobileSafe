package com.mymobilesafe.domain;


public class BlackBean {
    private String phone;//号码
    private int mode;//模式
    private long time;//添加的时间

    @Override
    public boolean equals(Object o) {
        if (o instanceof BlackBean) {
            BlackBean blackBean = (BlackBean) o;
            return phone.equals(blackBean.getPhone());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
