package com.github.sdp_begreen.begreen.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfilePhotoMetadata(
    override var pictureId: String? = null,
    override val takenOn: ParcelableDate? = null,
    override val takenBy: String? = null,

    ) : PhotoMetadata()