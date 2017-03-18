package com.mymobilesafe.domain;

/**
 * Created by mrka on 16-12-28.
 */

public class UrlBean extends Object {
    private int versionCode;
    private String url;
    private String desc;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
