package com.hmaserv.rz.domain

import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

data class CategoryResponse(
    @field:Json(name = "files")
    val images: List<String?>?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)

@Entity
data class Category(
    @Id var id: Long = 0,
    @Unique val uuid: String = "",
    val title: String = "",
    val image: String = ""
) {
    override fun toString(): String {
        return title
    }
}

fun CategoryResponse.toCategory(): Category? {
    if (uuid != null
        && title != null
        && images != null
    ) {
        return Category(
            0,
            uuid,
            title,
            images.filterNotNull().firstOrNull() ?: ""
        )
    }

    return null
}

