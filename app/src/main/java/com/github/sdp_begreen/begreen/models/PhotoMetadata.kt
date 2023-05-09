package com.github.sdp_begreen.begreen.models

import android.os.Parcelable

abstract class PhotoMetadata: Parcelable {

    abstract var pictureId: String?
    abstract val takenOn: ParcelableDate?
    abstract val takenBy: String?
}
