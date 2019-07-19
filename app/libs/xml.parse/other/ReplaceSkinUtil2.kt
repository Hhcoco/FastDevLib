package com.lxt.core.skin

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.TextView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.free.extensions.color
import com.free.extensions.utils.GlobalContextProvider
import com.free.extensions.utils.ScreenUtil
import com.lxt.core.skin.xml.*
import com.lxt.core.utils.gson.toObj
import com.lxt.xml2json.json.XML

/**
 * desc: 换肤相关工具类
 * time: 2019/4/16
 * @author wl.
 */
object ReplaceSkinUtil2 {

    private val TAG = ReplaceSkinUtil2::class.java.simpleName!!

    // 要替换的颜色值.
    var newColorMap: HashMap<String, Int>? = hashMapOf()
    // 要替换的drawable值.
    var newDrawableMap: HashMap<String, Drawable>? = hashMapOf()

    private val resources by lazy { GlobalContextProvider.getGlobalContext().resources }

    private const val RIPPLE = "ripple"
    private const val ITEM = "item"
    private const val SHAPE = "shape"
    private const val CORNERS = "corners"
    private const val SOLID = "solid"
    private const val GRADIENT = "gradient"
    private const val SIZE = "size"
    private const val STROKE = "stroke"
    private const val SELECTOR = "selector"
    private const val STATE_ENABLED = "state_enabled"
    private const val STATE_SELECTED = "state_selected"

    private const val EXCEPTION_TAG = "replace skin occur error."

    init {
//        newColorMap?.put("skin_primary", Color.parseColor("#BE2921"))
//        newColorMap?.put("skin_start_primary", Color.parseColor("#DB4942"))
    }

    /**
     *  解析xml文件，生成对应的model.
     *  @param xmlId xml文件名称.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> parseXml(xmlId: Int, mClass: Class<T>): T? {

        kotlin.runCatching {

            val xmlStr = XmlUtils.xml2json(xmlId)
            val xmlObj = XML.toJSONObject(xmlStr)
            val jsonStr = xmlObj.toString()
            val xmlModel = when (xmlObj.keys().next()) {
                RIPPLE -> {
                    jsonStr.toObj<RippleP>()?.ripple
                }
                SELECTOR -> {
                    jsonStr.toObj<SelectorP>()?.selector
                }
                SHAPE -> {
                    jsonStr.toObj<ShapeP>()?.shape
                }
                else -> {
                    null
                }
            }

            var drawable: Any? = null
            when {
                (mClass == StateListDrawable::class.java && xmlModel is Selector) -> { // selector
                    drawable = StateListDrawable()
                    xmlModel.items?.forEach { item ->

                        when {
                            item.androidDrawable != null -> {
                                // 获取drawable的名称
                                val oldName = getOldResName(item.androidDrawable) ?: throw Exception(EXCEPTION_TAG)
                                // 查看是否有新的值需要替换
                                val newDrawable = newDrawableMap?.get(oldName) ?: throw Exception(EXCEPTION_TAG)
                                val stateArray = arrayListOf<Int>()
                                when {
                                    item.stateSelected == true -> stateArray.add(android.R.attr.state_selected)
                                    item.stateEnabled == false -> stateArray.add(-android.R.attr.state_selected)
                                    else -> stateArray.add(-1)
                                }
                                when {
                                    item.stateEnabled == true -> stateArray.add(android.R.attr.state_enabled)
                                    item.stateEnabled == false -> stateArray.add(-android.R.attr.state_enabled)
                                    else -> stateArray.add(-1)
                                }
                                (drawable as StateListDrawable).addState(stateArray.toIntArray(), newDrawable)
                            }
                            item.shape != null -> {
                                val shapeDrawable = GradientDrawable()
                                val radius = item.shape!!.corners?.radius ?: throw Exception(EXCEPTION_TAG)
                                if (!radius.isEmpty()) { // 圆角
                                    val corner = ScreenUtil.dip(radius.replace("dip", "").toFloat().toInt()).toFloat()
                                    shapeDrawable.cornerRadius = corner
                                }
                                val stroke = item.shape!!.stroke
                                if (stroke != null) {
                                    val width =
                                        ScreenUtil.dip(stroke.width?.replace("dip", "")?.toFloat()?.toInt() ?: 0)
                                    // 替换颜色
                                    val oldColorName = getOldResName(stroke.color)
                                    val newColor = newColorMap?.get(oldColorName)
                                    if (newColor != null) {
                                        shapeDrawable.setStroke(width, newColor)
                                    }
                                    val stateArray = arrayListOf<Int>()
                                    when {
                                        item.stateSelected == true -> stateArray.add(android.R.attr.state_selected)
                                        item.stateEnabled == false -> stateArray.add(-android.R.attr.state_selected)
                                        else -> stateArray.add(-1)
                                    }
                                    when {
                                        item.stateEnabled == true -> stateArray.add(android.R.attr.state_enabled)
                                        item.stateEnabled == false -> stateArray.add(-android.R.attr.state_enabled)
                                        else -> stateArray.add(-1)
                                    }
                                    (drawable as StateListDrawable).addState(stateArray.toIntArray(), shapeDrawable)
                                }
                            }
                        }
                    }
                }
                (mClass == RippleDrawable::class.java && xmlModel is Ripple) -> { // ripple.
                    // todo 目前只处理了层级比较浅的这种.
                    val bgDrawable = generateShape(xmlModel.item?.shape) ?: throw Exception(EXCEPTION_TAG)
                    drawable = RippleDrawable(
                        ColorStateList.valueOf(Color.GRAY),
                        bgDrawable,
                        bgDrawable
                    )
                }
                (mClass == GradientDrawable::class.java && xmlModel is Shape) -> {
                    drawable = generateShape(xmlModel) ?: return null
                }
            }
            return drawable as T?
        }
        return null
    }

    /**
     *  获取旧的名称.
     *  @param path 全路径名称.
     */
    private fun getOldResName(path: String?): String? {
        val array = path?.split(Regex(":|/")) ?: return null
        if (array.size < 3) {
            return null
        }
        return array[2]
    }

    /**
     *  设置新的字体颜色
     */
    fun setNewTextColor(view: TextView?, colorName: String = "skin_primary") {
        view ?: return
        if (newColorMap != null && newColorMap!!.containsKey(colorName)) {
            view.setTextColor(newColorMap!![colorName]!!)
        }
    }


    /**
     *  生成Drawable.
     */
    private fun generateShape(originalValue: Shape?): GradientDrawable? {

        originalValue ?: return null

        val drawable = GradientDrawable()
        // 处理圆角.
        val radius = originalValue.corners?.radius
        if (!radius.isNullOrEmpty()) { // 圆角
            val corner = ScreenUtil.dip(radius?.replace("dip", "")?.toFloat().toInt()).toFloat()
            drawable.cornerRadius = corner
        }

        // 处理Gradient.
        val gradient = originalValue.gradient
        if (gradient != null) {
            val startColor = getRealColor(gradient.startColor)
            val endColor = getRealColor(gradient.endColor)
            // todo 根据角度设置方向
            drawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            if (startColor != null && endColor != null)
                drawable.colors = intArrayOf(startColor, endColor)
        }
        // 处理stroke.
        val stroke = originalValue.stroke
        if (stroke != null) {
            val width = ScreenUtil.dip(stroke.width?.replace("dip", "")?.toFloat()?.toInt() ?: 0)
            // 替换颜色
            val finalColor = getRealColor(stroke.color)
            drawable.setStroke(width, finalColor)
        }
        // 处理size
        val size = originalValue.size
        if (size != null) {
            val width = ScreenUtil.dip(size.width?.replace("dip", "")?.toFloat()?.toInt() ?: 0)
            val height = ScreenUtil.dip(size.height?.replace("dip", "")?.toFloat()?.toInt() ?: 0)
            drawable.setSize(width, height)
        }
        // 处理solid
        val solid = originalValue.solid
        if (solid != null) {
            val finalColor = getRealColor(solid.color)
            drawable.setColor(finalColor)
        }
        return drawable
    }

    /**
     *  获取真正的颜色.
     */
    private fun getRealColor(colorName: String?): Int {
        colorName ?: return -1
        val array = colorName?.split(Regex(":|/"))
        if (array.size != 3) {
            return -1
        }
        return getPatchColor(array[2]) ?: GlobalContextProvider.getGlobalContext().color(
            resources.getIdentifier(
                array[2],
                array[1],
                array[0]
            )
        )
    }

    /**
     *  从指定路径找新的颜色.
     */
    private fun getPatchColor(oldName: String): Int? {

        kotlin.runCatching {
            // 从内存中找
            if (newColorMap == null) return null
            return newColorMap!![oldName]
        }
        return null


        // 从配置文件中查找
//        val parse = Xml.newPullParser()
//        parse.setInput(resources.assets.open("res/values/colors.xml"), "UTF-8")
//        while (parse.next() != XmlPullParser.END_DOCUMENT) {
//            // 遇到开始标签就创建对应的类
//            when (parse.eventType) {
//                XmlPullParser.START_TAG -> {
//                    if (parse.name == "androidColor") {
//                        val name = parse.getAttributeValue(null, "name")
//                        if (oldName == name) {
//                            val value = parse.getAttributeValue(null, "value")
//                            return Color.parseColor(value)
//                        }
//                    }
//                }
//            }
//        }
//        return null
    }

    /**
     *  从patch包中找是否有相同名称和类型的资源，如果有，返回patch资源id.
     *  如果没有，返回-1
     */
    private fun getPatchRes(oldName: String): Drawable? {
        kotlin.runCatching {
            // 获取patch包中的所有资源文件
            resources.assets.list("res/drawable").forEach {
                // 如果有同名资源
                if (it.split(".")[0] == oldName) {
                    if (it.endsWith(".xml")) {
                        return VectorDrawableCompat.createFromStream(
                            resources.assets.open("res/androidDrawable/$it"),
                            null
                        )
                    } else if (it.endsWith(".png")) {
                        return VectorDrawableCompat.createFromStream(
                            resources.assets.open("res/androidDrawable/$it"),
                            null
                        )
                    }
                }
            }
        }.onFailure {
            return null
        }
        return null
    }
}