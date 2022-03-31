package com.ikea.myapp.models;

import java.io.Serializable;

public class PlanNote extends Plan implements Serializable {
    private String note;

    public PlanNote() {
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
