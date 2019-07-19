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
data class Item(
    var shape: Shape? = null,
    var selector: Selector? = null,
    @SerializedName("state_enabled")
    var stateEnabled: Boolean? = null,
    @SerializedName("state_selected")
    var stateSelected: Boolean? = null,
    @SerializedName("drawable")
    var androidDrawable: String? = null,
    @SerializedName("color")
    var androidColor: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<Shape>(Shape::class.java.classLoader),
        source.readParcelable<Selector>(Selector::class.java.classLoader),
        source.readValue(Boolean::class.java.classLoader) as Boolean?,
        source.readValue(Boolean::class.java.classLoader) as Boolean?,
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(shape, 0)
        writeParcelable(selector, 0)
        writeValue(stateEnabled)
        writeValue(stateSelected)
        writeString(androidDrawable)
        writeString(androidColor)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Item> = object : Parcelable.Creator<Item> {
            override fun createFromParcel(source: Parcel): Item = Item(source)
            override fun newArray(size: Int): Array<Item?> = arrayOfNulls(size)
        }
    }
}