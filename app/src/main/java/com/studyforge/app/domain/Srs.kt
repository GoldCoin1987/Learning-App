package com.studyforge.app.domain

import com.studyforge.app.data.ItemEntity
import kotlin.math.roundToInt

/**
 * Classic SM-2 spaced-repetition scheduler.
 *
 * Grade scale 0..5: 0–2 = failed recall (lapse), 3–5 = successful recall.
 * Isolated here so it can later be swapped for FSRS without touching storage or UI.
 */
object Sm2 {
    const val GRADE_AGAIN = 1
    const val GRADE_HARD = 3
    const val GRADE_GOOD = 4
    const val GRADE_EASY = 5

    fun review(item: ItemEntity, grade: Int, todayEpochDay: Long): ItemEntity {
        val q = grade.coerceIn(0, 5)
        var ef = item.ef
        var reps = item.reps
        var interval = item.intervalDays

        if (q < 3) {
            reps = 0
            interval = 1
        } else {
            reps += 1
            interval = when (reps) {
                1 -> 1
                2 -> 6
                else -> (interval * ef).roundToInt().coerceAtLeast(1)
            }
        }
        // SM-2 easiness-factor update, floored at 1.3.
        ef = (ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))).coerceAtLeast(1.3)

        return item.copy(
            ef = ef,
            reps = reps,
            intervalDays = interval,
            dueEpochDay = todayEpochDay + interval,
            lastReviewedEpochDay = todayEpochDay,
            introduced = true,
        )
    }
}
