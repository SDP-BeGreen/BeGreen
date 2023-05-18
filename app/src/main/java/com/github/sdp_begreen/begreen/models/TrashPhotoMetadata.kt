package com.github.sdp_begreen.begreen.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashPhotoMetadata(
    override var pictureId: String? = null,
    override val takenOn: ParcelableDate? = null,
    override val takenBy: String? = null,
    val caption: String? = null,
    val trashCategory: TrashCategory? = null,
    val location: CustomLatLng? = null

) : PhotoMetadata()
