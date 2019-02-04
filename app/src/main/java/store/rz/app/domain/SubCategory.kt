package store.rz.app.domain

import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

data class SubCategoryResponse(
    @field:Json(name = "files")
    val images: List<String?>?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)

data class SubCategoryRequest(
    @field:Json(name = "cat_uuid") val categoryUuid: String
)

@Entity
data class SubCategory(
    @Id var id: Long = 0,
    @Index val categoryUuid: String = "",
    @Unique val uuid: String = "",
    val title: String = "",
    val image: String = ""

) {
    override fun toString(): String {
        return title
    }
}

fun SubCategoryResponse.toSubCategory(categoryUuid: String): SubCategory? {
    if (uuid != null
        && title != null
        && images != null
    ) {
        return SubCategory(
            0,
            categoryUuid,
            uuid,
            title,
            images.filterNotNull().firstOrNull() ?: ""
        )
    }

    return null
}