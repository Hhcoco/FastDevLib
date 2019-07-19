package com.lxt.core.skin.xml

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * desc:
 * time: 2019/4/19
 * @author wl
 */
@Keep
data class CornersP(
    var corners: Corners? = null
)

@Keep
class Corners(
    var radius: String? = null,
    @SerializedName("state_checked")
    var stateChecked: Boolean? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readValue(Boolean::class.java.classLoader) as Boolean?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(radius)
        writeValue(stateChecked)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Corners> = object : Parcelable.Creator<Corners> {
            override fun createFromParcel(source: Parcel): Corners = Corners(source)
            override fun newArray(size: Int): Array<Corners?> = arrayOfNulls(size)
        }
    }
}