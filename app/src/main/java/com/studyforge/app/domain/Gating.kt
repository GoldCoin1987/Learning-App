package com.studyforge.app.domain

/** Prerequisite rules between packs (the "knowledge builds on knowledge" enforcement). */
object Gating {
    /** A pack may be installed/studied only once every pack id it requires is installed. */
    fun isPackAvailable(requires: List<String>, installedIds: Set<String>): Boolean =
        requires.all { it in installedIds }

    /** Missing prerequisites, for display in the catalog. */
    fun missingPrereqs(requires: List<String>, installedIds: Set<String>): List<String> =
        requires.filter { it !in installedIds }
}
