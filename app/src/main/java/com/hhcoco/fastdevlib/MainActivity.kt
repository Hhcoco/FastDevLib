package com.hhcoco.fastdevlib

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import com.lxt.xstreamdemo.R
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.Screen
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core.mean
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*
import java.io.*
import kotlin.concurrent.thread

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName!!

    private val DATA_PATH by lazy { Environment.getExternalStorageDirectory().absolutePath + File.separator; }
    //        private val DEFAULT_LANGUAGE = "eng"
    private val DEFAULT_LANGUAGE = "VIN"

    private val floatWindowRun by lazy {
        FloatWindow.destroy()
        FloatWindow
            .with(applicationContext)
            .setView(R.layout.view_run)
            .setWidth(100)                               //设置控件宽高
            .setHeight(Screen.width, 0.5f)
            .setX(0)                                   //设置控件初始位置
            .setY(Screen.height, 0.5f)
            .setDesktopShow(true)                        //桌面显示
            .build()
        FloatWindow.get()
    }

    // tesseract zwp.test.exp0.tif zwp.test.exp0 -l chi_sim -psm 7 batch.nochop makebox
// tesseract vin.test.exp2.tif vin.test.exp2 -l eng -psm 7 batch.nochop makebox
//     echo fontyp 0 0 0 0 0 >font_properties
//     tesseract vin.test.exp2.tif vin.test.exp2 -l eng -psm 7 nobatch box.train
//     unicharset_extractor vin.test.exp2.box
//     shapeclustering -F font_properties -U unicharset -O vin.unicharset vin.test.exp2.tr
//     mftraining -F font_properties -U unicharset -O vin.unicharset vin.test.exp2.tr
//     cntraining vin.test.exp2.tr
//    rename normproto vin.normproto
//    rename inttemp vin.inttemp
//    rename pffmtable vin.pffmtable
//    rename unicharset vin.unicharset
//    rename shapetable vin.shapetable
//    combine_tessdata vin.
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        copyToSD(DATA_PATH, "VIN.traineddata")
        val resizeMultiple = 1
        tv_tips.setOnClickListener {
            var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.tess_test15)
            val resizeBitmap =
                Bitmap.createBitmap(bitmap.width * resizeMultiple, bitmap.height * resizeMultiple, bitmap.config)
            // 二值化处理
//            val binarizationBitmap = BitmapBinarization().binarization(bitmap)

            val src = Mat()
            val grey = Mat()
            val gauss = Mat()
            val sobel = Mat()
            val dst = Mat()
            val blur = Mat()
            val medianBlur = Mat()
            val morphologyEx = Mat()
            val canny = Mat()
            val corr = Mat()
            val dilate01 = Mat()
            val dilate02 = Mat()
            val erode01 = Mat()
            val erode02 = Mat()
            val erode03 = Mat()
            val erode04 = Mat()
            val erode05 = Mat()
            val blackwhite = Mat()
            val resize = Mat()
            Utils.bitmapToMat(bitmap, resize)
            resize(
                resize,
                src,
                Size(resize.cols() * resizeMultiple * 1.0, resize.rows() * resizeMultiple * 1.0),
                0.0,
                0.0,
                INTER_AREA
            )

//            GaussianBlur(src, gauss, Size(7.0, 7.0), 0.0)

//            Imgproc.medianBlur(src, medianBlur, 7) // 中值滤波处理


//            Imgproc.Sobel(grey, sobel, CV_165,1, 0)
//            val lightLevel = mean(grey).`val`[0]
//            threshold(grey, dst, lightLevel + 60, 255.0, THRESH_BINARY) // 二值化
//            val element = getStructuringElement(MORPH_RECT, Size(2.0, 2.0))
//            morphologyEx(src, morphologyEx, MORPH_TOPHAT, element) // 闭操作
            val kernel = getStructuringElement(MORPH_RECT, Size(6.0, 6.0))
            val kernel2 = getStructuringElement(MORPH_RECT, Size(10.0, 10.0))
//            dilate(dst, dilate01, kernelX)


            cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
//            Imgproc.blur(grey, blur, Size(12.0, 12.0)) // 低通滤波处理

            val lightLevel = mean(grey).`val`[0]
            Toast.makeText(this, "$lightLevel", Toast.LENGTH_LONG).show()
            threshold(grey, blackwhite, Math.min(lightLevel * 2.0, 220.0), 255.0, THRESH_BINARY) // 二值化

            erode(blackwhite, erode01, kernel)
            dilate(erode01, dilate02, kernel2)

            Imgproc.medianBlur(dilate02, medianBlur, 9) // 中值滤波处理

//            Imgproc.blur(blackwhite, blur, Size(12.0, 12.0)) // 低通滤波处理
//            Imgproc.GaussianBlur(medianBlur, canny, Size(3.0, 3.0), 0.0, 0.0, Core.BORDER_DEFAULT) // 高通滤波

            Utils.matToBitmap(grey, resizeBitmap)
            img_tips.setImageBitmap(resizeBitmap)

//            val contours = arrayListOf<MatOfPoint>()
//            Imgproc.findContours(medianBlur, contours, canny, RETR_TREE, CHAIN_APPROX_SIMPLE)

//            var newBitmap: Bitmap? = null
//            val rectList = contours.filter {
//                val rect = boundingRect(it)
//                rect.width >= 200 && rect.width > rect.height * 6
//            }.map {
//                boundingRect(it)
//            }
//            val rect = rectList[1]
//            if (rect.width >= 200 && rect.width > rect.height * 6) {
//                newBitmap = Bitmap.createBitmap(
//                    bitmap,
//                    Math.max(0, rect.x - 100),
//                    Math.max(0, rect.y - 200),
//                    Math.min(bitmap.width, rect.width + 200) - (rect.x - 100),
//                    Math.min(bitmap.height, rect.height + 400) - (rect.y - 200)
//                )
//                img_tips.setImageBitmap(newBitmap)

//                Utils.bitmapToMat(newBitmap, src)
//                    GaussianBlur(src, gauss, Size(0.0, 3.0), 0.0)
//                cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
//                threshold(grey, dst, 160.0, 255.0, THRESH_BINARY) // 二值化处理.
//                Utils.matToBitmap(dst, newBitmap)
//                img_tips.setImageBitmap(newBitmap)
//            }

//            Utils.matToBitmap(canny, bitmap)


            src.release()
            grey.release()
            dst.release()

            if (null != resizeBitmap) {
                val tessBaseAPI = TessBaseAPI()
                tessBaseAPI.init(DATA_PATH, DEFAULT_LANGUAGE)
                tessBaseAPI.setImage(resizeBitmap)
                val result = tessBaseAPI.utF8Text
                tv_tips.text = result
            }

        }
    }

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initDebug()
    }

    fun copyToSD(pathP: String, name: String) {

        val path = pathP + "tessdata" + File.separator + name

        //如果存在就删掉
        val f = File(path)
        if (f.exists()) {
//            return
            f.delete()
        }
        if (!f.exists()) {
            val p = File(f.parent)
            if (!p.exists()) {
                p.mkdir()
            }
            try {
                val result = f.createNewFile()
                Log.d("", "re")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        var inputStream: InputStream? = null
        var os: OutputStream? = null
        try {
            inputStream = this.assets.open(name)
            val file = File(path)
            os = FileOutputStream(file)
            val bytes = ByteArray(8 * 1024)
            var len = inputStream!!.read(bytes)
            while (len != -1) {
                os!!.write(bytes, 0, len)
                len = inputStream!!.read(bytes)
            }
            os!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (inputStream != null)
                    inputStream!!.close()
                if (os != null)
                    os!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

}
