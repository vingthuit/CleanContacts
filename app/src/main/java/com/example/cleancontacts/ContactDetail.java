package com.example.cleancontacts;

import java.util.Objects;

public class ContactDetail {
    private int type;
    private String detail;

    public ContactDetail(int type, String detail) {
        this.type = type;
        this.detail = detail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactDetail that = (ContactDetail) o;
        return type == that.type && detail.equals(that.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, detail);
    }
}
