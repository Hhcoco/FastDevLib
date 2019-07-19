package com.lxt.core.skin.xml

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * desc:
 * time: 2019/4/18
 * @author wl
 */
@Keep
data class SelectorP(
    var selector: Selector? = null
)

@Keep
data class Selector(
    @SerializedName("item")
    var items: ArrayList<Item> = arrayListOf()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.createTypedArrayList(Item.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(items)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Selector> = object : Parcelable.Creator<Selector> {
            override fun createFromParcel(source: Parcel): Selector = Selector(source)
            override fun newArray(size: Int): Array<Selector?> = arrayOfNulls(size)
        }
    }
}