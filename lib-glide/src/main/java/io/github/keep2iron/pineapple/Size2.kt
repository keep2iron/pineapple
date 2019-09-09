package io.github.keep2iron.pineapple

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

data class Size2(val width: Int, val height: Int) : Parcelable {

  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readInt()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(width)
    parcel.writeInt(height)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "Size2(width=$width, height=$height)"
  }

  companion object CREATOR : Creator<Size2> {
    override fun createFromParcel(parcel: Parcel): Size2 {
      return Size2(parcel)
    }

    override fun newArray(size: Int): Array<Size2?> {
      return arrayOfNulls(size)
    }
  }

}