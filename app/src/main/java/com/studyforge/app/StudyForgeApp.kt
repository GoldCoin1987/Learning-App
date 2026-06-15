package com.studyforge.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.studyforge.app.data.PackRepository
import com.studyforge.app.data.StudyDatabase
import com.studyforge.app.data.StudyRepository
import com.studyforge.app.notifications.ReminderScheduler
import com.studyforge.app.notifications.ensureNotificationChannel

class StudyForgeApp : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ensureNotificationChannel(this)
        ReminderScheduler.scheduleNext(this)
    }

    // App-wide Coil loader with SVG support; disk cache makes images available offline after first view.
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .build()
}

/** Lightweight manual DI container (no Hilt, to keep the scaffold lean). */
class AppContainer(context: Context) {
    val db: StudyDatabase = Room
        .databaseBuilder(context, StudyDatabase::class.java, "studyforge.db")
        // Content is re-downloadable; a destructive recreate on schema change is fine.
        .fallbackToDestructiveMigration()
        .build()

    /** Catalog index URL. Point this at your hosted catalog.json (editable in a settings screen later). */
    var catalogUrl: String =
        "https://raw.githubusercontent.com/GoldCoin1987/Learning-App/main/packs/catalog.json"

    val packRepo: PackRepository = PackRepository(db, catalogUrl = { catalogUrl })
    val studyRepo: StudyRepository = StudyRepository(db)
}
