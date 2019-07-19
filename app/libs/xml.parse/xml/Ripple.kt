package com.lxt.core.skin.xml

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

/**
 * desc:
 * time: 2019/4/19
 * @author wl
 */
@Keep
data class RippleP(
    var ripple: Ripple? = null
)

@Keep
data class Ripple(
    var color: String? = null,
    var item: Item? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readParcelable<Item>(Item::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(color)
        writeParcelable(item, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Ripple> = object : Parcelable.Creator<Ripple> {
            override fun createFromParcel(source: Parcel): Ripple = Ripple(source)
            override fun newArray(size: Int): Array<Ripple?> = arrayOfNulls(size)
        }
    }
}