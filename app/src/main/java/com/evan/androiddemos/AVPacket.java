package com.evan.androiddemos;

public class AVPacket {
    public long pts;
    public byte[] data;
    public int size;

    public AVPacket(long pts, byte[] data, int size) {
        this.pts = pts;
        this.size = size;
        this.data = data;
    }
}
