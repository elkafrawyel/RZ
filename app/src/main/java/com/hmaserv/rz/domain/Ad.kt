package com.hmaserv.rz.domain

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

data class MainAttributeResponse(
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "values")
    val attributes: List<AttributeResponse?>?
)

data class AttributeResponse(
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "price")
    val price: Int?
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