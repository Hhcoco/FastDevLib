package com.hhcoco.fastdevlib

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import com.googlecode.tesseract.android.TessBaseAPI
import com.lxt.xstreamdemo.R
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.Screen
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Core.mean
import org.opencv.core.Core.minMaxLoc
import org.opencv.core.CvType.*
import org.opencv.imgproc.Imgproc.*
import java.io.*
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName!!

    private val DATA_PATH by lazy { Environment.getExternalStorageDirectory().absolutePath + File.separator; }
    //        private val DEFAULT_LANGUAGE = "eng"
    private val DEFAULT_LANGUAGE = "VIN4"

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

    private var isSwitch = false

    private var picId = R.mipmap.tess_test2

    private val cachePoint = arrayListOf<MatOfPoint>()

    private var pageIndex = 50

    private var filterPoint: List<MatOfPoint>? = null

    private val src by lazy { Mat() }
    private val blackwhite by lazy { Mat() }
    private val contour2 by lazy { Mat.zeros(src.rows(), src.cols(), CV_8UC3) }

    private val cache = hashMapOf<String, ArrayList<Point2>>()
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
        copyToSD(DATA_PATH, "VIN4.traineddata")
        val resizeMultiple = 1

//        floatWindowRun.show()

        val bitmapList = arrayListOf(
            R.mipmap.tess_test2, R.mipmap.tess_test3, R.mipmap.tess_test5,
            R.mipmap.tess_test6, R.mipmap.tess_test7, R.mipmap.tess_test8,
            R.mipmap.tess_test9, R.mipmap.tess_test10, R.mipmap.tess_test13, R.mipmap.tess_test14, R.mipmap.tess_test15
        )

//        val bitmapList = arrayListOf(R.mipmap.tess_test4)

        tv_resize.setOnClickListener {

            pageIndex = 50

            var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.tess_test17)
            val grey = Mat()
            Utils.bitmapToMat(bitmap, src)
            cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
            threshold(grey, blackwhite, 165.0, 255.0, THRESH_BINARY) // 二值化

            val dilate = Mat()
            val kernelX1 = getStructuringElement(MORPH_RECT, Size(30.0, 2.0))
            erode(blackwhite, dilate, kernelX1)

            val points = arrayListOf<MatOfPoint>()
            findContours(dilate, points, Mat(), RETR_TREE, CHAIN_APPROX_NONE, Point(0.0, 0.0))
            val maxRect = points.map { boundingRect(it) }.filter {
                (it.x + it.width / 2) in blackwhite.width() / 2 - it.width / 4 .. blackwhite.width() / 2 + it.width / 4
                        && it.width in 100 .. 500 && it.width > it.height && it.y > blackwhite.height() - 500
            }.maxBy { it.area() }
            val maxMat = Mat(blackwhite, maxRect)

//            val dilate = Mat()
//            val kernelX1 = getStructuringElement(MORPH_RECT, Size(30.0, 2.0))
//            dilate(blackwhite, dilate, kernelX1)
//
//            val points = arrayListOf<MatOfPoint>()
//            findContours(dilate, points, Mat(), RETR_EXTERNAL, CHAIN_APPROX_NONE, Point(0.0, 0.0))
//            val maxRect = points.map { boundingRect(it) }.filter {
//                (it.x + it.width / 2) in blackwhite.width() / 2 - it.width..blackwhite.width() / 2 + it.width
//                        && it.width < 500 && it.width > it.height
//            }.maxBy { it.area() }
//            val maxMat = Mat(blackwhite, maxRect)


//            val max = points.maxBy { boundingRect(it).area() }
//            val maxRect = boundingRect(max)
//            val maxMat = Mat(blackwhite, maxRect)
//
//            val srcMat = Mat(
//                maxMat, Rect(
//                    0, (maxRect.height - (260 / 1011.0) * maxRect.height).toInt(),
//                    maxRect.width / 2, ((260 / 1011.0) * maxRect.height).toInt()
//                )
//            )

//            val srcMat = Mat(
//                blackwhite, Rect(
//                    (blackwhite.width() / 2 - 90 * density).toInt(), (statusBarHeight + 40 * density).toInt(),
//                    (150 * density).toInt(), (50 * density).toInt()
//                ))

//            filterPoint = points.filter {
//                val rect = boundingRect(it)
//                rect.width > 0
//            }.sortedBy {
//                boundingRect(it).y
//            }
//            filterPoint?.withIndex()?.forEach {
//                drawContours(
//                    contour2, filterPoint, it.index, Scalar(
//                        255 * Random().nextDouble(),
//                        255 * Random().nextDouble(),
//                        255 * Random().nextDouble()
//                    ),
//                    1
//                )
//            }

            val srcBitmap = Bitmap.createBitmap(maxMat.width(), maxMat.height(), Bitmap.Config.RGB_565)
            Utils.matToBitmap(maxMat, srcBitmap)
            img_tips.setImageBitmap(srcBitmap)

            val key = tv_key.text.toString()
            Test.saveBitmap("${key}_${System.currentTimeMillis()}.png", srcBitmap)
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()

        }

        tv_add.setOnClickListener {
            // 获取当前的
//            val current = filterPoint?.get(pageIndex)?.toArray()
//            if (null == current) {
//                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
//            } else {
//                val key = tv_key.text.toString()
//                val json = GsonUtil.json(current)
//                writeTxtToFile(json, "${key}_${System.currentTimeMillis()}")
//                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
//            }

            val current = filterPoint?.get(pageIndex)
            if (null == current) {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
            } else {

                val srcMat = Mat(blackwhite, boundingRect(current))
                val srcBitmap = Bitmap.createBitmap(srcMat.width(), srcMat.height(), Bitmap.Config.RGB_565)
                Utils.matToBitmap(srcMat, srcBitmap)
                img_tips.setImageBitmap(srcBitmap)

                val key = tv_key.text.toString()
                Test.saveBitmap("${key}_${System.currentTimeMillis()}.png", srcBitmap)

//                val key = tv_key.text.toString()
//                val json = GsonUtil.json(current)
//                writeTxtToFile(json, "${key}_${System.currentTimeMillis()}")
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
            }

        }

        tv_next.setOnClickListener {
            pageIndex++
            filterPoint?.withIndex()?.forEach {
                if (it.index == pageIndex) {
                    drawContours(contour2, filterPoint, it.index, Scalar.all(255.0), 1)
                } else {
                    drawContours(contour2, filterPoint, it.index, Scalar(84.0, 184.0, 80.0), 1)
                }
            }
            val bitmap = Bitmap.createBitmap(contour2.cols(), contour2.rows(), Bitmap.Config.ARGB_4444)
            Utils.matToBitmap(contour2, bitmap)
            img_tips.setImageBitmap(bitmap)
        }

        tv_find.setOnClickListener {

            val mask = Mat.zeros(src.size(), CV_8UC1)
            drawContours(mask, filterPoint, pageIndex, Scalar.all(255.0), -1)
            val dstMat = Mat()
            src.copyTo(dstMat, mask)
            val bitmap = Bitmap.createBitmap(mask.cols(), mask.rows(), Bitmap.Config.ARGB_4444)
            Utils.matToBitmap(mask, bitmap)
            img_tips.setImageBitmap(bitmap)
        }

        tv_switch.setOnClickListener {

            val path = DATA_PATH + "tessdata"
            val file = File(path)
            val allFiles = file.listFiles().filter {
                it.name.contains("2_") && !it.name.contains(".")
            }
            val mat = allFiles.map {
                val json = readTxtFromFile(it.absolutePath)
                val pointArray = GsonUtil.obj<Array<Point>>(json, object : TypeToken<Array<Point>>() {}.type)
                if (pointArray == null) {
                    null
                } else {
                    MatOfPoint(*pointArray)
                }
            }.filter {
                it != null
            }

            val contour3 = Mat.zeros(src.rows(), src.cols(), CV_8UC3)

            drawContours(
                contour3, mat, -1, Scalar(84.0, 184.0, 80.0),
                1
            )

            val bitmap = Bitmap.createBitmap(contour3.cols(), contour3.rows(), Bitmap.Config.ARGB_4444)
            Utils.matToBitmap(contour3, bitmap)
            img_tips.setImageBitmap(bitmap)

        }

        tv_template.setOnClickListener {

            val grey = Mat()
            var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.tess_test20)
            Utils.bitmapToMat(bitmap, src)
            val blackwhite = Mat(src.cols(), src.rows(), CV_8UC4)
            cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
            threshold(grey, blackwhite, 165.0, 255.0, THRESH_BINARY) // 二值化

            Utils.matToBitmap(blackwhite, bitmap)
            img_tips.setImageBitmap(bitmap)
            return@setOnClickListener

            val path = DATA_PATH + "tessdata" + File.separator + "tess_test_103.png"
            val bitmap103 = BitmapFactory.decodeFile(path)
            val mat103 = Mat()
            Utils.bitmapToMat(bitmap103, mat103)
            val resultMat = Mat()
            kotlin.runCatching {
                val start = System.currentTimeMillis()
                val grey103 = Mat()
                cvtColor(mat103, grey103, COLOR_BGR2GRAY)
                val mat1038U = Mat()
                threshold(grey103, mat1038U, 165.0, 255.0, THRESH_BINARY) // 二值化
                val mid0 = System.currentTimeMillis()
                val rect = Rect(300, 1500, 600, 240)
                val rectBlackWhite = Mat(blackwhite, rect)

                resultMat.create(
                    (rectBlackWhite.cols() - mat103.cols() + 1),
                    (rectBlackWhite.rows() - mat103.rows() + 1),
                    CV_8UC1
                )

                matchTemplate(rectBlackWhite, mat1038U, resultMat, TM_CCORR)
                val mid = System.currentTimeMillis()
                val result = minMaxLoc(resultMat)
                Log.d(TAG, "")
                Toast.makeText(
                    this,
                    "${mid0 - start}/${mid - start}/${System.currentTimeMillis() - start}",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure {
                Log.e(TAG, it.message)
            }
            img_tips.setImageBitmap(bitmap103)

//            val rect = Rect(400, 1600, 500, 140)
//            val newMat = Mat(grey, rect)
//            bitmap = Bitmap.createBitmap(newMat.cols(), newMat.rows(), Bitmap.Config.ARGB_4444)
//            Utils.matToBitmap(newMat, bitmap)
//            img_tips.setImageBitmap(bitmap)
//            saveBitmap(bitmap, 104)
        }

        tv_tips.setOnClickListener {

            pageIndex = 200

            var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.tess_test25)

            val resizeBitmap =
                Bitmap.createBitmap(bitmap.width * resizeMultiple, bitmap.height * resizeMultiple, bitmap.config)
            // 二值化处理
//            val binarizationBitmap = BitmapBinarization().binarization(bitmap)


            val rotate = Mat()
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
            val flood = Mat()
            val dilate02 = Mat()
            val erode01 = Mat()
            val erode02 = Mat()
            val erode03 = Mat()
            val erode04 = Mat()
            val erode05 = Mat()

            val resize = Mat()
            val inRange = Mat()
            val hsv = Mat()
            val black = Mat()
            Utils.bitmapToMat(bitmap, src)
            cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
            threshold(grey, blackwhite, 165.0, 255.0, THRESH_BINARY) // 二值化
//            inRange(grey, Scalar(240.0, 240.0, 242.0), Scalar(241.0, 241.0, 243.0), inRange)

//            val kernelY = getStructuringElement(MORPH_RECT, Size(8.0, 8.0))
//            dilate(blackwhite, erode01, kernelY)

            val points = arrayListOf<MatOfPoint>()
//            findContours(blackwhite, points, Mat(), RETR_EXTERNAL, CHAIN_APPROX_NONE, Point(0.0, 0.0))
            findContours(blackwhite, points, Mat(), RETR_TREE, CHAIN_APPROX_NONE, Point(0.0, 0.0))

            val rects = points.map {
                boundingRect(it)
            }

            filterPoint = points.filter {
                val rect = boundingRect(it)
                rect.width > 0
//                (rect.height / rect.width.toDouble()) in 0.0..3.0 && rect.width in 0..90 && rect.height in 0..90
            }.sortedBy {
                boundingRect(it).y
            }
            filterPoint?.withIndex()?.forEach {
                drawContours(
                    contour2, filterPoint, it.index, Scalar(
                        255 * Random().nextDouble(),
                        255 * Random().nextDouble(),
                        255 * Random().nextDouble()
                    ),
                    1
                )

//                drawContours(contour2, filterPoint, it.index, Scalar.all(0.0), 1)
            }

            bitmap = Bitmap.createBitmap(contour2.cols(), contour2.rows(), Bitmap.Config.ARGB_4444)

            Utils.matToBitmap(contour2, bitmap)
            img_tips.setImageBitmap(bitmap)

            return@setOnClickListener

            cvtColor(src, grey, COLOR_BGR2GRAY) // 灰度化处理
//            Imgproc.blur(grey, blur, Size(12.0, 12.0)) // 低通滤波处理

//            GaussianBlur(grey, gauss, Size(31.0, 31.0), 4.0, 4.0)

            val lightLevel = mean(grey).`val`[0]
            val cc = OpencvUtil.getCc(lightLevel)
//            adaptiveThreshold(grey, blackwhite, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 55, 0.0)
            adaptiveThreshold(grey, blackwhite, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 7, 0.0)

            threshold(grey, blackwhite, Math.min(lightLevel * 2, 230.0), 255.0, THRESH_BINARY) // 二值化

            filterPoint = points.filter {
                it.toArray()
                val rect = boundingRect(it)
                val area = rect.area()
                area > 2000
//                        && rect.height.toFloat() / rect.width in 1f..5f
            }

            if (cachePoint.isNullOrEmpty()) {
                val result = filterPoint!!.sortedBy {
                    boundingRect(it).x
                }.subList(1, Math.min(15, filterPoint!!.size - 1))
                    .map {
                        it.toArray()
                    }.map {
                        Test.createMatOfPoint(it)
                    }
                cachePoint.addAll(result)
            }

            val filterRect = filterPoint!!.map {
                boundingRect(it)
            }

            filterRect.withIndex().forEach {

                val matchShapes = cachePoint.map { innerIt ->
                    matchShapes(filterPoint!![it.index], innerIt, CV_CONTOURS_MATCH_I1, 0.0)
                }
                if (it.index == 0) {
                    rectangle(
                        contour2, it.value, Scalar(
                            180.0,
                            180.0,
                            180.0
                        )
                    )
                }
//                if (matchShapes.min() ?: 1.0 < 0.2) {

                drawContours(
                    contour2, filterPoint, it.index, Scalar(
                        255 * Random().nextDouble(),
                        255 * Random().nextDouble(),
                        255 * Random().nextDouble()
                    ),
                    2
                )
//                }
            }

//                    val mat2 = Test.createMatOfPoint2f(points[it.index].toArray())
//                    val length = arcLength(mat2, true)
//                    val area2 = contourArea(points[it.index])

            val finalBmp2 = Bitmap.createBitmap(contour2.cols(), contour2.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(contour2, finalBmp2)
            img_tips.setImageBitmap(finalBmp2)
            return@setOnClickListener

            val chayi = rects.filter { it.height / it.width in 1..3 && it.height > src.height() / 6 }.sortedBy { it.x }

            if (chayi.size < 5) {
                val finalBmp2 = Bitmap.createBitmap(erode01.cols(), erode01.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(erode01, finalBmp2)
                img_tips.setImageBitmap(finalBmp2)
                return@setOnClickListener
            }

            val average = (chayi.sumBy { it.height } / chayi.size).toFloat()

            val averageLevel01 = if (average < 50f) 0.3f else 0.2f

            val chayi2 = chayi.filterIndexed { index, rect ->
                val sss = if (index == chayi.size - 1) {
                    Math.abs(rect.x - chayi[index - 1].x)
                } else {
                    Math.abs(rect.x - chayi[index + 1].x)
                }.toFloat()
                sss in 1f..rect.height * 1.5f &&
                        Math.abs(rect.height - average) in 0f..Math.min(20f, average * averageLevel01)
            }

            val average2 = (chayi2.sumBy { it.height } / chayi2.size).toFloat()
            val averageLevel02 = if (average2 < 50) 0.3f else 0.2f
            val chayi3 = chayi2.filter {
                Math.abs(it.height - average2) in 0f..Math.min(20f, average2 * averageLevel02)
            }
            return@setOnClickListener

            // 如果还没有找到.
            Utils.matToBitmap(blackwhite, bitmap)
            img_tips.setImageBitmap(bitmap)

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

    override fun onPause() {


        super.onPause()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.getToolType(0)
        return super.onTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initDebug()
    }

    private fun completeNewPoint(center: Point, angle: Float, srcPoint: Point): Point {
        val x =
            (srcPoint.x - center.x) * Math.cos(angle.toDouble()) - (srcPoint.y - center.y) * Math.sin(angle.toDouble()) + center.x
        val y =
            (srcPoint.x - center.x) * Math.sin(angle.toDouble()) + (srcPoint.y - center.y) * Math.cos(angle.toDouble()) + center.y
        return Point(x, y)
    }

    /**
     *  计算两个点的角度.
     */
    private fun completeAngle(pointA: Rect, pointB: Rect): Int {
        val x = Math.abs(pointA.x - pointB.x)
        val y = Math.abs(pointA.y - pointB.y)
        val z = Math.sqrt(x * x + y * y * 1.0)
        return Math.round((Math.asin(y / z) / Math.PI * 180).toFloat())
    }

    private fun saveBitmap(bitmap: Bitmap, index: Int) {
        val path = DATA_PATH + "tessdata" + File.separator + "tess_test_$index.png"
        val file = File(path)
        kotlin.runCatching {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bos.flush()
            bos.close()
        }
    }

    fun writeTxtToFile(json: String, name: String) {

        try {

            val path = DATA_PATH + "tessdata" + File.separator + name

            makeFile(path)

            val fw = FileWriter(path, true)
            val bw = BufferedWriter(fw)
            bw.append(json)
            bw.close()
            fw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun readTxtFromFile(path: String): String {

        var inputStream: FileInputStream? = null
        var reader: BufferedReader? = null
        val content = StringBuilder()
        try {
            inputStream = FileInputStream(File(path))
            reader = BufferedReader(InputStreamReader(inputStream))
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return content.toString()

    }

    fun copyToSD(pathP: String, name: String) {

        val path = pathP + "tessdata" + File.separator + name

        makeFile(path)
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

    private fun makeFile(path: String) {
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
    }

}
