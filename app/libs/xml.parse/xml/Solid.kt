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
data class Solid(
    var color: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(color)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Solid> = object : Parcelable.Creator<Solid> {
            override fun createFromParcel(source: Parcel): Solid = Solid(source)
            override fun newArray(size: Int): Array<Solid?> = arrayOfNulls(size)
        }
    }
}