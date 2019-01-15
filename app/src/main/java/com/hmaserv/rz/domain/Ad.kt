package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class AdResponse(
    @Json(name = "ads_uuid")
    val adsUuid: String?,
    @Json(name = "mainAttributes")
    val mainAttributes: List<MainAttributeResponse?>?,
    @Json(name = "create_ad")
    val createAd: String?,
    @Json(name = "decs")
    val decs: String?,
    @Json(name = "discount_price")
    val discountPrice: Int?,
    @Json(name = "files")
    val files: List<String?>?,
    @Json(name = "owner")
    val owner: OwnerResponse?,
    @Json(name = "price")
    val price: Int?,
    @Json(name = "rate")
    val rate: Int?,
    @Json(name = "reviews")
    val reviews: Int?,
    @Json(name = "sub_category")
    val subCategory: String?,
    @Json(name = "title")
    val title: String?
)

data class MainAttributeResponse(
    @Json(name = "name")
    val name: String?,
    @Json(name = "values")
    val attributes: List<AttributeResponse?>?
)

data class AttributeResponse(
    @Json(name = "name")
    val name: String?,
    @Json(name = "price")
    val price: Int?
)

data class OwnerResponse(
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "full_name")
    val fullName: String?,
    @Json(name = "uuid")
    val uuid: String?
)

data class AdRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String
)

data class Ad(
    val uuid: String,
    val mainAttributes: List<MainAttribute>,
    val date: String,
    val description: String,
    val discountPrice: Int,
    val images: List<String>,
    val owner: Owner,
    val price: Int,
    val rate: Int,
    val reviewsNo: Int,
    val subCategoryName: String,
    val title: String
)

data class MainAttribute(
    val name: String,
    val attributes: List<Attribute>
)

data class Attribute(
    val name: String,
    val price: Int
)

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
            files.filterNotNull(),
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

fun MainAttributeResponse.toMainAttribute(): MainAttribute? {
    if (this.name != null
        && this.attributes != null
    ) {
        return MainAttribute(
            name,
            attributes.filterNotNull().mapNotNull { it.toAttribute() }
        )
    }

    return null
}

fun AttributeResponse.toAttribute(): Attribute? {
    if (this.name != null
        && this.price != null
    ) {
        return Attribute(
            name,
            price
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