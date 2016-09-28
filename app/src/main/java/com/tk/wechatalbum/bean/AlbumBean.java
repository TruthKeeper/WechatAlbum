package com.tk.wechatalbum.bean;

/**
 * Created by TK on 2016/8/24.
 */
public class AlbumBean {
    private String path;
    private String name;
    private long date;

    public AlbumBean(String path, String name, long date) {
        this.path = path;
        this.name = name;
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AlbumBean) {
            AlbumBean bean = (AlbumBean) o;
            return bean.getPath().equals(path)
                    && bean.getName().equals(name)
                    && bean.getDate() == date;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
