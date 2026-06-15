package com.studyforge.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Index of available packs, fetched from a static (public) catalog URL. */
@Serializable
data class CatalogIndex(
    val schemaVersion: Int = 2,
    val name: String = "",
    val updated: String = "",
    val packs: List<CatalogEntry> = emptyList(),
)

@Serializable
data class CatalogEntry(
    val id: String,
    val title: String,
    val version: String,
    val description: String = "",
    val requires: List<String> = emptyList(),
    val itemCount: Int = 0,
    val url: String,
    val sha256: String? = null,
)

/** A downloadable Topic ("plugin"): Topic → Sub-topics → Lessons → Items. */
@Serializable
data class Pack(
    val schemaVersion: Int = 2,
    val id: String,
    val title: String,
    val version: String,
    val description: String = "",
    val requires: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val subtopics: List<Subtopic> = emptyList(),
)

@Serializable
data class Subtopic(
    val id: String,
    val title: String,
    val order: Int = 0,
    val lessons: List<Lesson> = emptyList(),
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val order: Int = 0,
    val difficulty: Int = 1,
    val items: List<Item> = emptyList(),
)

@Serializable
enum class ItemType {
    @SerialName("flashcard") FLASHCARD,
    @SerialName("mcq") MCQ,
}

/** A single study item. Flashcard and MCQ share one class with optional fields. */
@Serializable
data class Item(
    val id: String,
    val type: ItemType,
    // flashcard
    val front: String? = null,
    val back: String? = null,
    val hint: String? = null,
    // mcq
    val prompt: String? = null,
    val choices: List<String> = emptyList(),
    val answerIndex: Int? = null,
    val explanation: String? = null,
    // optional images (absolute URLs; PNG/JPG/SVG). `image` shows with the front/prompt,
    // `backImage` with a flashcard's answer side. Text fields may contain LaTeX ($...$ / $$...$$).
    val image: String? = null,
    val backImage: String? = null,
)
