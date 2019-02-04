package store.rz.app.domain
import com.squareup.moshi.Json


data class Slider(
    @field:Json(name = "file")
    val image: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)