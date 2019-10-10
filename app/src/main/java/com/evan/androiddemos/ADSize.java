package com.evan.androiddemos;

public class ADSize implements IADSize {
    private int height;
    private int width;
    private int mode;

    public ADSize(int width, int height, int mode) {
        this.height = height;
        this.width = width;
        this.mode = mode;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMode() {
        return mode;
    }
}
