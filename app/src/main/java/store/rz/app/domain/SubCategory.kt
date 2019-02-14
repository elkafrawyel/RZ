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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubCategory

        if (categoryUuid != other.categoryUuid) return false
        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryUuid.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image.hashCode()
        return result
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