package com.example.myapplication;

public class Item {
    private String code;
    private String name;
    private String room;
    private boolean checked;

    public Item(String code, String name, String room, boolean checked) {
        this.code = code;
        this.name = name;
        this.room = room;
        this.checked = checked;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}