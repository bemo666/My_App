package com.ikea.myapp;

import java.io.Serializable;

public class PlanNote implements Serializable {
    private String note;

    public PlanNote() {
    }

    public PlanNote(int num) {
        note = new String();
    }

    public PlanNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
