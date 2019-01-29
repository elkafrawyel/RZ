package com.hmaserv.rz.domain

import com.hmaserv.rz.utils.Constants
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter

@Entity
data class Settings(
    @Id(assignable = true)
    var id: Long = Constants.SETTINGS_ID,
    @Convert(converter = LanguageConverter::class, dbType = String::class)
    val language: Constants.Language,
    val firebaseToken: String? = null,
    val isAcceptedContract: Boolean = false
)

class LanguageConverter : PropertyConverter<Constants.Language, String> {

    override fun convertToDatabaseValue(entityProperty: Constants.Language?): String {
        return entityProperty?.value ?: Constants.Language.DEFAULT.value
    }

    override fun convertToEntityProperty(databaseValue: String?): Constants.Language {
        databaseValue ?: return Constants.Language.DEFAULT
        return when(databaseValue) {
            Constants.Language.ARABIC.value -> Constants.Language.ARABIC
            Constants.Language.ENGLISH.value -> Constants.Language.ENGLISH
            else -> Constants.Language.DEFAULT
        }
    }
}