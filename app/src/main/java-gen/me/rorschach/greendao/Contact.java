package me.rorschach.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table "CONTACT".
 */
public class Contact implements Serializable{

    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String tel;
    /** Not-null value. */
    private String college;
    private boolean isStar;
    private boolean isRecord;
    private long communicateTime;

    public Contact() {
    }

    public Contact(Long id) {
        this.id = id;
    }

    public Contact(Long id, String name, String tel, String college, boolean isStar, boolean isRecord, long communicateTime) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.college = college;
        this.isStar = isStar;
        this.isRecord = isRecord;
        this.communicateTime = communicateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getTel() {
        return tel;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /** Not-null value. */
    public String getCollege() {
        return college;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCollege(String college) {
        this.college = college;
    }

    public boolean getIsStar() {
        return isStar;
    }

    public void setIsStar(boolean isStar) {
        this.isStar = isStar;
    }

    public boolean getIsRecord() {
        return isRecord;
    }

    public void setIsRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }

    public long getCommunicateTime() {
        return communicateTime;
    }

    public void setCommunicateTime(long communicateTime) {
        this.communicateTime = communicateTime;
    }

}
