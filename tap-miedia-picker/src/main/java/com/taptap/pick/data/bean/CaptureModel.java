package com.taptap.pick.data.bean;

import java.io.Serializable;

public class CaptureModel implements Serializable {

    public final boolean isPublic;
    public final String authority;
    public final String directory;

    public CaptureModel(boolean isPublic, String authority) {
        this(isPublic, authority, null);
    }

    public CaptureModel(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }

}
