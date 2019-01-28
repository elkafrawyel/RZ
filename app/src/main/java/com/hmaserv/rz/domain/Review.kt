package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class ReviewResponse(
    @field:Json(name = "rate")
    val rate: Float?,
    @field:Json(name = "comment")
    val comment: String?,
    @field:Json(name = "owner")
    val owner: OwnerResponse?,
    @field:Json(name = "created_at")
    val created_at: String?
)

data class ReviewsRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String
)

data class Review(
    val rate: Int,
    val owner: Owner,
    val comment: String,
    val created_at: String
)

fun ReviewResponse.toReview(): Review? {
    if (rate != null
        && comment != null
        && created_at != null
        && owner != null
    ) {
        return Review(
            rate.toInt(),
            owner.toOwner(),
            comment,
            created_at)
    }
    return null
}

data class WriteReviewRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String,
    @field:Json(name = "rate")
    val rate: Int,
    @field:Json(name = "comment")
    val comment: String
)