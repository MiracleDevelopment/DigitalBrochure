package com.ipati.dev.brochure.model;

import android.os.StatFs;
import android.view.View;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ipati on 9/6/2560.
 */

public class MemoryState {



    public String FreeSpace(String path) {
        long kb = 1024;
        long mb = 1024 * kb;

        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        StatFs statFs = new StatFs(path);
        long total = statFs.getBlockSizeLong();
        long spaceUse = statFs.getAvailableBlocksLong();
        return numberFormat.format((spaceUse * total) / mb);
    }

    public long FreeSpaceInt(String path) {
        long kb = 1024;
        long mb = 1024 * kb;

        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        StatFs statFs = new StatFs(path);
        long total = statFs.getBlockSizeLong();
        long spaceUse = statFs.getAvailableBlocksLong();
        return (spaceUse * total) / mb;


    }


}
