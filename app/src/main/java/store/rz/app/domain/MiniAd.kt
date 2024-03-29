package store.rz.app.domain

import com.squareup.moshi.Json

data class MiniAdResponse(
    @field:Json(name = "ads_uuid")
    val uuid: String?,
    @field:Json(name = "files")
    val images: List<String?>?,
    @field:Json(name = "price")
    val price: Int?,
    @field:Json(name = "rate")
    val rate: Float?,
    @field:Json(name = "discount_price")
    val discountPrice: Int?,
    @field:Json(name = "sub_category")
    val subCategory: String?,
    @field:Json(name = "title")
    val title: String?
)

data class MiniAdRequest(
    @field:Json(name = "sub_cat_uuid")
    val subCategoryUuid: String
)

data class MiniAd(
    val uuid: String,
    val title: String,
    val price: Int,
    val discountPrice: Int,
    val rate: Int,
    val subCategory: String,
    val images: List<String>
)

fun MiniAdResponse.toMiniAd(): MiniAd? {
    if (this.uuid != null
        && this.title != null
        && this.price != null
        && this.discountPrice != null
        && this.rate != null
        && this.subCategory != null
        && this.images != null
    ) {
        return MiniAd(
            this.uuid,
            this.title,
            this.price,
            this.discountPrice,
            this.rate.toInt(),
            this.subCategory,
            this.images.filterNotNull()
        )
    }

    return null
}