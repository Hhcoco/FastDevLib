package com.hhcoco.fastdevlib;

import android.graphics.Bitmap;
import android.os.Environment;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * desc:
 * time: 2019/8/8
 *
 * @author wl
 */
public class Test {

    public static MatOfPoint2f createMatOfPoint2f(Point[] points) {
        return new MatOfPoint2f(points);
    }

    public static MatOfPoint createMatOfPoint(Point[] points) {
        return new MatOfPoint(points);
    }

    public static void saveBitmap(String picName, Bitmap bm) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tessdata";
        File f = new File(path, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
