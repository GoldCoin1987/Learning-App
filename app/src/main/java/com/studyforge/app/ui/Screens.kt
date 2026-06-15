package com.studyforge.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.studyforge.app.AppContainer
import com.studyforge.app.data.LessonEntity
import com.studyforge.app.data.StudyCard
import com.studyforge.app.data.SubtopicSummary
import com.studyforge.app.domain.Gating
import com.studyforge.app.domain.Sm2
import com.studyforge.app.model.CatalogEntry
import com.studyforge.app.model.ItemType
import kotlinx.coroutines.launch
import java.time.LocalDate

private fun today() = LocalDate.now().toEpochDay()

/** Converts a physical millimeter value to Dp using the display's real DPI. */
@Composable
private fun mmToDp(mm: Float): Dp {
    val ydpi = LocalContext.current.resources.displayMetrics.ydpi
    val px = mm / 25.4f * ydpi
    return with(LocalDensity.current) { px.toDp() }
}

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()
    val edge = mmToDp(6f) // buffer clear of the OS status bar (top) and nav/gesture bar (bottom)
    Box(modifier = Modifier.fillMaxSize().padding(top = edge, bottom = edge)) {
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(container, nav::navigate) }
        composable("catalog") { CatalogScreen(container) }
        composable("topics") { TopicsScreen(container, nav::navigate) }
        composable("topic/{packId}") { entry ->
            SubtopicsScreen(container, entry.arguments?.getString("packId").orEmpty(), nav::navigate)
        }
        composable("subtopic/{packId}/{subtopicId}") { entry ->
            LessonsScreen(
                container,
                entry.arguments?.getString("packId").orEmpty(),
                entry.arguments?.getString("subtopicId").orEmpty(),
                nav::navigate,
            )
        }
        composable("lesson/{packId}/{lessonId}") { entry ->
            LessonReadingScreen(
                container,
                entry.arguments?.getString("packId").orEmpty(),
                entry.arguments?.getString("lessonId").orEmpty(),
                nav::navigate,
            )
        }
        composable(
            route = "study?packId={packId}&subtopicId={subtopicId}&lessonId={lessonId}",
            arguments = listOf(
                navArgument("packId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("subtopicId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("lessonId") { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { entry ->
            StudyScreen(
                container = container,
                packId = entry.arguments?.getString("packId"),
                subtopicId = entry.arguments?.getString("subtopicId"),
                lessonId = entry.arguments?.getString("lessonId"),
                onDone = { nav.popBackStack() },
            )
        }
    }
    }
}

@Composable
private fun HomeScreen(container: AppContainer, navigate: (String) -> Unit) {
    val due by container.studyRepo.observeDueCount(today()).collectAsState(initial = 0)
    val packs by container.packRepo.observePacks().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("StudyForge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Installed topics: ${packs.size}", style = MaterialTheme.typography.bodyLarge)
        Text("Cards due today: $due", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))
        Button(onClick = { navigate("study") }, modifier = Modifier.fillMaxWidth()) {
            Text("Study all due")
        }
        OutlinedButton(onClick = { navigate("topics") }, modifier = Modifier.fillMaxWidth()) {
            Text("Browse topics")
        }
        OutlinedButton(onClick = { navigate("catalog") }, modifier = Modifier.fillMaxWidth()) {
            Text("Download topics")
        }
    }
}

@Composable
private fun TopicsScreen(container: AppContainer, navigate: (String) -> Unit) {
    val packs by container.packRepo.observePacks().collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Topics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        if (packs.isEmpty()) {
            Text("No topics installed yet. Use \"Download topics\".")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(packs) { pack ->
                    Card(Modifier.fillMaxWidth().clickable { navigate("topic/${pack.id}") }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(pack.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (pack.description.isNotBlank()) {
                                Text(pack.description, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtopicsScreen(container: AppContainer, packId: String, navigate: (String) -> Unit) {
    var subs by remember { mutableStateOf<List<SubtopicSummary>?>(null) }
    LaunchedEffect(packId) { subs = container.studyRepo.subtopics(packId, today()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Sub-topics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { navigate("study?packId=$packId") }, modifier = Modifier.fillMaxWidth()) {
            Text("Study entire topic")
        }
        Spacer(Modifier.height(12.dp))
        when (val list = subs) {
            null -> CircularProgressIndicator()
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { st ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(st.subtopicTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${st.total} cards · ${st.due} due · ${st.newCount} new", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { navigate("subtopic/$packId/${st.subtopicId}") }) {
                                Text("Open lessons")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonsScreen(container: AppContainer, packId: String, subtopicId: String, navigate: (String) -> Unit) {
    var lessons by remember { mutableStateOf<List<LessonEntity>?>(null) }
    LaunchedEffect(packId, subtopicId) { lessons = container.studyRepo.lessons(packId, subtopicId) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lessons", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { navigate("study?packId=$packId&subtopicId=$subtopicId") },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Study whole sub-topic") }
        Spacer(Modifier.height(12.dp))
        when (val list = lessons) {
            null -> CircularProgressIndicator()
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { lesson ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(lesson.lessonTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!lesson.content.isNullOrBlank()) {
                                    Button(onClick = { navigate("lesson/$packId/${lesson.lessonId}") }) { Text("Read") }
                                }
                                OutlinedButton(onClick = {
                                    navigate("study?packId=$packId&subtopicId=$subtopicId&lessonId=${lesson.lessonId}")
                                }) { Text("Questions") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonReadingScreen(container: AppContainer, packId: String, lessonId: String, navigate: (String) -> Unit) {
    var lesson by remember { mutableStateOf<LessonEntity?>(null) }
    var loaded by remember { mutableStateOf(false) }
    LaunchedEffect(packId, lessonId) {
        lesson = container.studyRepo.lesson(packId, lessonId)
        loaded = true
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val l = lesson
        when {
            !loaded -> CircularProgressIndicator()
            l == null -> Text("Lesson not found.")
            else -> {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Text(l.lessonTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    if (l.content.isNullOrBlank()) {
                        Text("No reading for this lesson yet — jump straight to the questions.")
                    } else {
                        RichText(l.content, modifier = Modifier.fillMaxWidth(), textSizeSp = 16f)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { navigate("study?packId=$packId&subtopicId=${l.subtopicId}&lessonId=$lessonId") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Start questions") }
            }
        }
    }
}

@Composable
private fun CatalogScreen(container: AppContainer) {
    val scope = rememberCoroutineScope()
    val installed by container.packRepo.observePacks().collectAsState(initial = emptyList())
    val installedIds = installed.map { it.id }.toSet()

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var entries by remember { mutableStateOf<List<CatalogEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            entries = container.packRepo.fetchCatalog().packs
        } catch (e: Exception) {
            error = e.message ?: "Failed to load catalog"
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Download topics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(entries) { entry ->
                    val isInstalled = entry.id in installedIds
                    val missing = Gating.missingPrereqs(entry.requires, installedIds)
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                            Text("v${entry.version} · ${entry.itemCount} items", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(8.dp))
                            when {
                                isInstalled -> Text("Installed", color = MaterialTheme.colorScheme.primary)
                                missing.isNotEmpty() ->
                                    Text("Locked — requires: ${missing.joinToString()}", color = MaterialTheme.colorScheme.error)
                                else -> Button(onClick = {
                                    scope.launch {
                                        try {
                                            container.packRepo.installFromUrl(entry.url, today())
                                        } catch (e: Exception) {
                                            error = e.message
                                        }
                                    }
                                }) { Text("Download") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyScreen(container: AppContainer, packId: String?, subtopicId: String?, lessonId: String?, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<List<StudyCard>?>(null) }
    var index by remember { mutableStateOf(0) }

    LaunchedEffect(packId, subtopicId, lessonId) {
        session = container.studyRepo.buildSession(today(), packId, subtopicId, lessonId)
    }

    val cards = session
    when {
        cards == null -> Centered { CircularProgressIndicator() }
        cards.isEmpty() || index >= cards.size -> Centered {
            Text(if (cards.isEmpty()) "Nothing to study here right now." else "Session complete 🎉")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onDone) { Text("Back") }
        }
        else -> {
            val card = cards[index]
            CardView(card, position = index + 1, total = cards.size) { grade ->
                scope.launch {
                    container.studyRepo.grade(card.packId, card.item.id, grade, today())
                    index += 1
                }
            }
        }
    }
}

@Composable
private fun CardView(card: StudyCard, position: Int, total: Int, onGrade: (Int) -> Unit) {
    val item = card.item
    var revealed by remember(item.id) { mutableStateOf(false) }
    var selected by remember(item.id) { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("$position / $total", style = MaterialTheme.typography.labelMedium)
        Text("${card.subtopicTitle} › ${card.lessonTitle}", style = MaterialTheme.typography.labelMedium)
        HorizontalDivider()

        if (item.type == ItemType.FLASHCARD) {
            RichText(item.front.orEmpty(), textSizeSp = 20f)
            item.image?.let { CardImage(it) }
            if (revealed) {
                HorizontalDivider()
                RichText(item.back.orEmpty(), textSizeSp = 17f)
                item.backImage?.let { CardImage(it) }
            }
            Spacer(Modifier.height(8.dp))
            if (!revealed) {
                item.hint?.let { RichText("Hint: $it", textSizeSp = 14f) }
                Button(onClick = { revealed = true }, modifier = Modifier.fillMaxWidth()) { Text("Reveal answer") }
            } else {
                GradeButtons(onGrade)
            }
        } else { // MCQ
            RichText(item.prompt.orEmpty(), textSizeSp = 20f)
            item.image?.let { CardImage(it) }
            Spacer(Modifier.height(4.dp))
            item.choices.forEachIndexed { i, choice ->
                val show = selected != null
                val correct = item.answerIndex == i
                val marker = when {
                    show && correct -> "✓ "
                    show && selected == i -> "✗ "
                    else -> ""
                }
                OutlinedButton(
                    onClick = { if (selected == null) selected = i },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    RichText(marker + choice, modifier = Modifier.fillMaxWidth(), textSizeSp = 15f)
                }
            }
            if (selected != null) {
                Spacer(Modifier.height(8.dp))
                item.explanation?.let { RichText(it, textSizeSp = 16f) }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onGrade(if (selected == item.answerIndex) Sm2.GRADE_GOOD else Sm2.GRADE_AGAIN) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Continue") }
            }
        }
    }
}

@Composable
private fun CardImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
    )
}

@Composable
private fun GradeButtons(onGrade: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onGrade(Sm2.GRADE_AGAIN) }, modifier = Modifier.weight(1f)) { Text("Again") }
            Button(onClick = { onGrade(Sm2.GRADE_HARD) }, modifier = Modifier.weight(1f)) { Text("Hard") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onGrade(Sm2.GRADE_GOOD) }, modifier = Modifier.weight(1f)) { Text("Good") }
            Button(onClick = { onGrade(Sm2.GRADE_EASY) }, modifier = Modifier.weight(1f)) { Text("Easy") }
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}
