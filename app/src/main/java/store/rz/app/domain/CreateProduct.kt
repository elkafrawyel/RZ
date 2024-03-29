package store.rz.app.domain
import com.squareup.moshi.Json


data class CreateProductResponse(
    @field:Json(name = "ads_uuid")
    val adsUuid: String?
)

data class CreateProductRequest(
    @field:Json(name = "sub_cat_uuid")
    val subCatUuid: String,
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "desc")
    val description: String,
    @field:Json(name = "price")
    val price: Int,
    @field:Json(name = "discount_price")
    val discountPrice: Int,
    @field:Json(name = "quantity")
    val quantity: Int,
    @field:Json(name = "characteristics")
    val mainAttribute: List<Attribute.MainAttribute>
)

data class UpdateAdRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String,
    @field:Json(name = "sub_cat_uuid")
    val subCatUuid: String,
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "desc")
    val description: String,
    @field:Json(name = "price")
    val price: Int,
    @field:Json(name = "discount_price")
    val discountPrice: Int,
    @field:Json(name = "quantity")
    val quantity: Int,
    @field:Json(name = "characteristics")
    val mainAttribute: List<Attribute.MainAttribute>
)