package com.lxt.core.skin

import android.util.Log
import androidx.annotation.AnyRes
import com.free.extensions.utils.GlobalContextProvider
import org.xmlpull.v1.XmlPullParser

/**
 * desc: xml文件处理
 * time: 2019/7/9
 * @author wl
 */
object XmlUtils {
    /**
     *  xml转
     */
    fun xml2json(@AnyRes xmlId: Int): String {
        val xmlParse = GlobalContextProvider.getGlobalContext().resources.getXml(xmlId)
        val stringBuilder = StringBuilder()

        while (xmlParse.next() != XmlPullParser.END_DOCUMENT) {
            val name = xmlParse.name
            // 遇到开始标签就创建对应的类
            when (xmlParse.eventType) {
                XmlPullParser.START_TAG -> {
                    stringBuilder.append("<")
                    stringBuilder.append(name)
                    stringBuilder.append("\r\n")
                    for (i in 0 until xmlParse.attributeCount) {
                        val attrName = xmlParse.getAttributeName(i)
                        var attrValue = xmlParse.getAttributeValue(i)
                        attrValue = if (attrValue.contains("@"))
                            GlobalContextProvider.getGlobalContext().resources.getResourceName(
                                attrValue.replace(
                                    "@",
                                    ""
                                ).toInt()
                            )
                        else attrValue
                        stringBuilder.append(attrName)
                        stringBuilder.append("=")
                        stringBuilder.append("\"$attrValue\"")
                        stringBuilder.append("\r\n")
                    }
                    stringBuilder.append(">")
                    stringBuilder.append("\r\n")
                    Log.d("", "")
                }
                XmlPullParser.END_TAG -> {
                    Log.d("", "")
                    stringBuilder.append("</$name>\r\n")
                }
            }
        }
        return stringBuilder.toString()
    }
}