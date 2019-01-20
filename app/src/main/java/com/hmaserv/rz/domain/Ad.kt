package com.hmaserv.rz.domain

import android.net.Uri
import com.squareup.moshi.Json


data class AdResponse(
    @field:Json(name = "ads_uuid")
    val adsUuid: String?,
    @field:Json(name = "characteristics")
    val mainAttributes: List<MainAttributeResponse?>?,
    @field:Json(name = "create_ad")
    val createAd: String?,
    @field:Json(name = "decs")
    val decs: String?,
    @field:Json(name = "discount_price")
    val discountPrice: Int?,
    @field:Json(name = "files")
    val files: List<String?>?,
    @field:Json(name = "owner")
    val owner: OwnerResponse?,
    @field:Json(name = "price")
    val price: Int?,
    @field:Json(name = "rate")
    val rate: Int?,
    @field:Json(name = "reviews")
    val reviews: Int?,
    @field:Json(name = "sub_category")
    val subCategory: String?,
    @field:Json(name = "title")
    val title: String?
)

data class OwnerResponse(
    @field:Json(name = "avatar")
    val avatar: String?,
    @field:Json(name = "full_name")
    val fullName: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)

data class AdRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String
)

data class Ad(
    val uuid: String,
    val mainAttributes: List<Attribute.MainAttribute>,
    val date: String,
    val description: String,
    val discountPrice: Int,
    val images: List<Image.UrlImage>,
    val owner: Owner,
    val price: Int,
    val rate: Int,
    val reviewsNo: Int,
    val subCategoryName: String,
    val title: String
)

sealed class Image {
    data class UrlImage(val url: String) : Image()
    data class UriImage(val uri: Uri) : Image()
}

data class Owner(
    val image: String,
    val fullName: String,
    val uuid: String
)

fun AdResponse.toAd(): Ad? {
    if (this.adsUuid != null
        && this.title != null
        && this.price != null
        && this.discountPrice != null
        && this.rate != null
        && this.reviews != null
        && this.createAd != null
        && this.decs != null
        && this.files != null
        && this.owner != null
        && this.subCategory != null
        && this.mainAttributes != null
    ) {
        return Ad(
            adsUuid,
            mainAttributes.filterNotNull().mapNotNull { it.toMainAttribute() },
            createAd,
            decs,
            discountPrice,
            files.filterNotNull().toImages(),
            owner.toOwner(),
            price,
            rate,
            reviews,
            subCategory,
            title
        )
    }

    return null
}

fun OwnerResponse.toOwner(): Owner {
    return Owner(
        avatar ?: "",
        fullName ?: "Name is null",
        uuid ?: "uuid is null"
    )
}

fun List<String>.toImages(): List<Image.UrlImage> {
    return this.map { Image.UrlImage(it) }
}