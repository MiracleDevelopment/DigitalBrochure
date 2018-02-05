package com.ipati.dev.brochure.model;

import java.util.ArrayList;

/**
 * Created by ipati on 15/6/2560.
 */

public class ClearFile {
    private ArrayList<String> listBrochure;
    private ArrayList<String> listVideo;

    public void setListBrochure(ArrayList<String> listBrochure) {
        this.listBrochure = listBrochure;
    }

    public void setListVideo(ArrayList<String> listVideo) {
        this.listVideo = listVideo;
    }

    public ArrayList<String> getListBrochure() {
        return listBrochure;
    }

    public ArrayList<String> getListVideo() {
        return listVideo;
    }
}
