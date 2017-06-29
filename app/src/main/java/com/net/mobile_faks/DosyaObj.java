package com.net.mobile_faks;

public class DosyaObj {
    private int ID;
    private int tip; // 1-resim , 2-dosya
    private int size;
    private String imagepath; // dosyanin yolu..

    public DosyaObj(int id, int tip, int size, String imagepath) {
        this.ID = id;
        this.tip = tip;
        this.size = size;
        this.imagepath = imagepath;
    }


    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
