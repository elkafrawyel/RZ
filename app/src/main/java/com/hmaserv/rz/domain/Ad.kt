package com.hmaserv.rz.domain

import android.net.Uri
import com.squareup.moshi.Json


data class AdResponse(
    @field:Json(name = "ads_uuid")
    val adsUuid: String?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "price")
    val price: Int?,
    @field:Json(name = "discount_price")
    val discountPrice: Int?,
    @field:Json(name = "quantity")
    val quantity: Int?,
    @field:Json(name = "rate")
    val rate: Float?,
    @field:Json(name = "reviews")
    val reviews: Int?,
    @field:Json(name = "created_at")
    val createdAt: String?,
    @field:Json(name = "decs")
    val decs: String?,
    @field:Json(name = "files")
    val files: List<String?>?,
    @field:Json(name = "owner")
    val owner: OwnerResponse?,
    @field:Json(name = "category")
    val category: String?,
    @field:Json(name = "sub_category_uuid")
    val subCategoryUuid: String?,
    @field:Json(name = "sub_category")
    val subCategory: String?,
    @field:Json(name = "characteristics")
    val mainAttributes: List<MainAttributeResponse?>?
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

data class SearchRequest(
    @field:Json(name = "search_keyword")
    val searchText: String,
    @field:Json(name = "sub_cat_uuid")
    val subCategoryUuid: String
)

data class Ad(
    val uuid: String,
    val mainAttributes: List<Attribute.MainAttribute>,
    val date: String,
    val description: String,
    val discountPrice: Int,
    val quantity: Int,
    val images: List<Image.UrlImage>,
    val owner: Owner,
    val price: Int,
    val rate: Int,
    val reviewsNo: Int,
    val categoryName: String,
    val subCategoryUuid: String,
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
    if (adsUuid != null
        && title != null
        && price != null
        && discountPrice != null
        && quantity != null
        && rate != null
        && reviews != null
        && createdAt != null
        && decs != null
        && files != null
        && owner != null
        && category != null
        && subCategoryUuid != null
        && subCategory != null
        && mainAttributes != null
    ) {
        return Ad(
            adsUuid,
            mainAttributes.filterNotNull().mapNotNull { it.toMainAttribute() },
            createdAt,
            decs,
            discountPrice,
            quantity,
            files.filterNotNull().toImages(),
            owner.toOwner(),
            price,
            rate.toInt(),
            reviews,
            category,
            subCategoryUuid,
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