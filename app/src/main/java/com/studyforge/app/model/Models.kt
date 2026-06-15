package com.studyforge.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Index of available packs, fetched from a static catalog URL. */
@Serializable
data class CatalogIndex(
    val schemaVersion: Int = 1,
    val name: String = "",
    val updated: String = "",
    val packs: List<CatalogEntry> = emptyList(),
)

@Serializable
data class CatalogEntry(
    val id: String,
    val title: String,
    val version: String,
    val curriculumIndex: String = "",
    val description: String = "",
    val requires: List<String> = emptyList(),
    val itemCount: Int = 0,
    val url: String,
    val sha256: String? = null,
)

/** A downloadable content module ("plugin"). */
@Serializable
data class Pack(
    val schemaVersion: Int = 1,
    val id: String,
    val title: String,
    val version: String,
    val curriculumIndex: String = "",
    val description: String = "",
    val requires: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val items: List<Item> = emptyList(),
)

@Serializable
enum class ItemType {
    @SerialName("flashcard") FLASHCARD,
    @SerialName("mcq") MCQ,
}

/**
 * A single study item. Flashcard fields and MCQ fields share one class with optional
 * properties to keep pack JSON and serialization simple.
 */
@Serializable
data class Item(
    val id: String,
    val type: ItemType,
    val difficulty: Int = 1,
    val topic: String = "",
    val requires: List<String> = emptyList(),
    // flashcard
    val front: String? = null,
    val back: String? = null,
    val hint: String? = null,
    // mcq
    val prompt: String? = null,
    val choices: List<String> = emptyList(),
    val answerIndex: Int? = null,
    val explanation: String? = null,
)
