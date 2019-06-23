package com.evan.androiddemos.codekk;

public class EntranceItem {
    private String name;
    private Class<?> clazz;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTarget(Class<?> clazz) {
        this.clazz = clazz;
    }

    Class<?> getTarget() {
        return clazz;
    }
}
