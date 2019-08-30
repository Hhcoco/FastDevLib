package com.hhcoco.fastdevlib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import com.googlecode.tesseract.android.TessBaseAPI
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*
import java.io.File

/**
 * desc: 图片处理相关工具类.
 * time: 2019/7/31
 * @author wl
 */
object OpencvUtil {

    private val DATA_PATH by lazy { Environment.getExternalStorageDirectory().absolutePath + File.separator; }
    //        private val DEFAULT_LANGUAGE = "eng"
    private val DEFAULT_LANGUAGE = "VIN"

    private val tessBaseAPI by lazy {
        val tessBaseAPI = TessBaseAPI()
        tessBaseAPI.init(DATA_PATH, DEFAULT_LANGUAGE)
        tessBaseAPI
    }

    fun ttt(context: Context, resourceId: Int): Bitmap {

        // 源图
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)

        // 获取原图二值化后的图
        val dstMedianBlur = PThreshold(srcMat)
        var resizeMat = PResize(dstMedianBlur, srcMat)
        if (resizeMat == null) {
            val dstMedianBlur2 = PThreshold(srcMat, 160.0)
            resizeMat = PResize(dstMedianBlur2, srcMat)
        }
        // 如果还是找不到, 不找了
        resizeMat = if (resizeMat == null) {
            val grey = Mat()
            val adaptive = Mat()
            cvtColor(srcMat, grey, COLOR_BGR2GRAY)
            val lightLevel = Core.mean(grey).`val`[0]
            Toast.makeText(context, "$lightLevel", Toast.LENGTH_SHORT).show()
            val cc = getCc(lightLevel)
            adaptiveThreshold(grey, adaptive, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 155, cc)
            adaptive
//            PThreshold(srcMat, 10.0, 1.
//
//            4, isNeedMedianBlue = true, needTess = false)
        } else {
            val grey = Mat()
            val adaptive = Mat()
            cvtColor(resizeMat, grey, COLOR_BGR2GRAY)
            val lightLevel = Core.mean(grey).`val`[0]
            Toast.makeText(context, "$lightLevel", Toast.LENGTH_SHORT).show()

            val cc = getCc(lightLevel)
            adaptiveThreshold(grey, adaptive, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 155, cc)
            adaptive

//            PThreshold(resizeMat, 10.0, 2.0, isNeedMedianBlue = true, needTess = false)
        }
//        val resizeMat = dstMedianBlur

        val finalBmp = Bitmap.createBitmap(resizeMat.cols(), resizeMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resizeMat, finalBmp)
        return finalBmp
    }

    fun getCc(lightLevel: Double): Double {
        val cc = when (lightLevel) {

            in 0.0..65.0 -> {
                6.0
            }
            in 65.0..75.0 -> {
                4.0
            }
            in 75.0..85.0 -> {
                2.0
            }
            in 85.0..95.0 -> {
                -5.0
            }
            in 95.0..105.0 -> {
                -10.0
            }
            in 105.0..115.0 -> {
                -15.0
            }

            in 115.0..125.0 -> {
                -20.0
            }
            in 125.0..135.0 -> {
                -15.0
            }
            in 135.0..145.0 -> {
                -10.0
            }
            in 145.0..155.0 -> {
                -5.0
            }
            in 155.0..165.0 -> {
                2.0
            }
            in 165.0..175.0 -> {
                4.0
            }
            in 175.0..255.0 -> {
                6.0
            }
            else -> {
                2.0
            }
        }
        return cc
    }

    /**
     *  获取黑白图.
     */
    private fun PThreshold(
        srcMat: Mat,
        kernelWidth: Double = 60.0,
        light: Double = 2.0,
        isNeedMedianBlue: Boolean = false,
        needTess: Boolean = false
    ): Mat {

        // 灰度图
        val greyMat = Mat()
        val dilate01 = Mat()
        val dilate02 = Mat()
        val erode01 = Mat()
        val erode02 = Mat()
        val blackwhite = Mat()
        val medianBlur = Mat()

        // 灰度化处理
        cvtColor(srcMat, greyMat, COLOR_BGR2GRAY)

        if (needTess) {
            val greyBitmap = Bitmap.createBitmap(greyMat.cols(), greyMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(greyMat, greyBitmap)
            return greyMat
//            tessBaseAPI.setImage(greyBitmap)
//            val result = tessBaseAPI.utF8Text
        }

        // 获取平均亮度
        val lightLevel = Core.mean(greyMat).`val`[0]
        val cc = getCc(lightLevel)
        adaptiveThreshold(greyMat, blackwhite, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 155, cc)

//        Imgproc.threshold(greyMat, blackwhite, Math.min(lightLevel * light, 230.0), 255.0, Imgproc.THRESH_BINARY) // 二值化
        // 区域连通
        val kernelX = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(kernelWidth, 2.0))
        val kernelY = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 4.0))

        Imgproc.dilate(blackwhite, dilate01, kernelX)
        Imgproc.erode(dilate01, erode01, kernelX)

        Imgproc.erode(erode01, erode02, kernelY)
        Imgproc.dilate(erode02, dilate02, kernelY)

        if (isNeedMedianBlue) {
            Imgproc.medianBlur(dilate02, medianBlur, 7) // 中值滤波处理

//            if (needTess) {
//                val medianBitmap = Bitmap.createBitmap(medianBlur.cols(), medianBlur.rows(), Bitmap.Config.ARGB_8888)
//                Utils.matToBitmap(medianBlur, medianBitmap)
//                tessBaseAPI.setImage(medianBitmap)
//                val result = tessBaseAPI.utF8Text
//            }

            return medianBlur
        }

//        if (needTess) {
//            val dilateBitmap = Bitmap.createBitmap(dilate02.cols(), dilate02.rows(), Bitmap.Config.ARGB_8888)
//            Utils.matToBitmap(dilate02, dilateBitmap)
//            tessBaseAPI.setImage(dilateBitmap)
//            val result = tessBaseAPI.utF8Text
//        }

        return dilate02
    }

    /**
     *  获取裁剪区域.
     */
    private fun PResize(medianBlur: Mat, srcMat: Mat): Mat? {

        val points = arrayListOf<MatOfPoint>()
        Imgproc.findContours(
            medianBlur,
            points,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE,
            Point(0.0, 0.0)
        )

        val rects = points.map {
            Imgproc.boundingRect(it)
        }.filter { rect ->
            ((rect.x != 0 || rect.y != 0) && rect.width >= (srcMat.width() * 0.5) && rect.height >= 50 && rect.width > rect.height * 6)
        }.maxBy { rect ->
            rect.width * rect.height
        }
        return if (rects != null) {
            Mat(srcMat, rects)
        } else {
            null
        }
    }

}