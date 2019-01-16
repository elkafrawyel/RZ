package com.hmaserv.rz.domain

import android.os.Parcelable
import com.chad.library.adapter.base.entity.SectionEntity
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class MainAttributeResponse(
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "values")
    val attributes: List<SubAttributeResponse?>?
)

data class SubAttributeResponse(
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "price")
    val price: Int?
)

data class AttributesRequest(
    @field:Json(name = "sub_cat_uuid")
    val subCategoryUuid: String
)

sealed class Attribute : Parcelable {

    @Parcelize
    data class MainAttribute(
        val name: String,
        @field:Json(name = "values")
        val attributes: List<SubAttribute>
    ) : Attribute()

    @Parcelize
    data class SubAttribute(
        @Transient val mainAttributeName: String,
        val name: String,
        val price: Int,
        @Transient var isChecked: Boolean
    ) : Attribute()
}

class AttributeSection : SectionEntity<Attribute.SubAttribute> {
    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
    constructor(subAttribute: Attribute.SubAttribute) : super(subAttribute)
}

fun MainAttributeResponse.toMainAttribute(): Attribute.MainAttribute? {
    if (name != null
        && attributes != null
    ) {
        return Attribute.MainAttribute(
            name,
            attributes.filterNotNull().mapNotNull { it.toSubAttribute(name) }
        )
    }

    return null
}

fun SubAttributeResponse.toSubAttribute(mainAttributeName: String): Attribute.SubAttribute? {
    if (this.name != null
        && this.price != null
    ) {
        return Attribute.SubAttribute(
            mainAttributeName,
            name,
            price,
            false
        )
    }

    return null
}

fun AttributeSection.toMainAttribute(): Attribute.MainAttribute? {
    if (!isHeader && t is Attribute.SubAttribute) {
        return Attribute.MainAttribute(
            t.mainAttributeName,
            listOf(t)
        )
    }
    return null
}