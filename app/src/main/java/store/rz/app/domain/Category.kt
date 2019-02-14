package store.rz.app.domain

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

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (image != other.image) return false

        return true
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

