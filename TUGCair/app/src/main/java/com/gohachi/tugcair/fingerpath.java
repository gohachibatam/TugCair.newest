package com.gohachi.tugcair;


import android.graphics.Path;

public class fingerpath {
    public int color;
    public  boolean emboss;
    public boolean blur;
    public int strokewidth;
    public Path path;

    public fingerpath(int color, boolean emboss, boolean blur, int strokewidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokewidth = strokewidth;
        this.path = path;
    }
}
