package com.studyforge.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.studyforge.app.data.PackRepository
import com.studyforge.app.data.StudyDatabase
import com.studyforge.app.data.StudyRepository
import com.studyforge.app.notifications.ReminderScheduler
import com.studyforge.app.notifications.ensureNotificationChannel

class StudyForgeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ensureNotificationChannel(this)
        ReminderScheduler.scheduleNext(this)
    }
}

/** Lightweight manual DI container (no Hilt, to keep the scaffold lean). */
class AppContainer(context: Context) {
    val db: StudyDatabase = Room
        .databaseBuilder(context, StudyDatabase::class.java, "studyforge.db")
        // Schema v2 (hierarchical packs). Content is re-downloadable, so a destructive
        // upgrade is acceptable here rather than hand-writing a migration.
        .fallbackToDestructiveMigration()
        .build()

    /** Catalog index URL. Point this at your hosted catalog.json (editable in a settings screen later). */
    var catalogUrl: String =
        "https://raw.githubusercontent.com/GoldCoin1987/Learning-App/main/packs/catalog.json"

    val packRepo: PackRepository = PackRepository(db, catalogUrl = { catalogUrl })
    val studyRepo: StudyRepository = StudyRepository(db)
}
