package com.hhcoco.fastdevlib

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import com.lxt.xstreamdemo.R
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Core.mean
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.MORPH_RECT
import org.opencv.imgproc.Imgproc.dilate
import java.io.*


class MainActivity : AppCompatActivity() {

    private val DATA_PATH by lazy { Environment.getExternalStorageDirectory().absolutePath + File.separator; }
    //        private val DEFAULT_LANGUAGE = "eng"
    private val DEFAULT_LANGUAGE = "VIN"

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

        tv_tips.setOnClickListener {
            var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.tess_test8)
            // 二值化处理
//            val binarizationBitmap = BitmapBinarization().binarization(bitmap)

            val src = Mat()
            val temp = Mat()
            val dst = Mat()
            val blur = Mat()
            val medianBlur = Mat()
            val canny = Mat()
            val corr = Mat()
            Utils.bitmapToMat(bitmap, src)
            Imgproc.cvtColor(src, temp, Imgproc.COLOR_BGR2GRAY) // 灰度化处理
//            val lightLevel = mean(temp).`val`[0]
//            Toast.makeText(this, "light: $lightLevel", Toast.LENGTH_LONG).show()
//            Imgproc.threshold(temp, dst, lightLevel + 60, 255.0, Imgproc.THRESH_BINARY) // 二值化
//            Imgproc.blur(dst, blur, Size(6.0, 6.0)) // 低通滤波处理
//            Imgproc.medianBlur(blur, medianBlur, 3) // 低通滤波处理
//            Imgproc.GaussianBlur(medianBlur, canny, Size(3.0, 3.0), 0.0, 0.0, Core.BORDER_DEFAULT) // 高通滤波

            val element = Imgproc.getStructuringElement(MORPH_RECT, Size(50.0, 50.0))
            dilate(temp, corr, element)

            Utils.matToBitmap(corr, bitmap)

            img_tips.setImageBitmap(bitmap)

            src.release()
            temp.release()
            dst.release()

            val tessBaseAPI = TessBaseAPI()
            tessBaseAPI.init(DATA_PATH, DEFAULT_LANGUAGE)
            tessBaseAPI.setImage(bitmap)
            val result = tessBaseAPI.utF8Text
            tv_tips.text = result

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
