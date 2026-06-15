package com.studyforge.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studyforge.app.AppContainer
import com.studyforge.app.data.StudyCard
import com.studyforge.app.domain.Gating
import com.studyforge.app.domain.Sm2
import com.studyforge.app.model.CatalogEntry
import com.studyforge.app.model.ItemType
import kotlinx.coroutines.launch
import java.time.LocalDate

private fun today() = LocalDate.now().toEpochDay()

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(container, nav::navigate) }
        composable("catalog") { CatalogScreen(container) }
        composable("study") { StudyScreen(container, onDone = { nav.popBackStack() }) }
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
        Text("Installed packs: ${packs.size}", style = MaterialTheme.typography.bodyLarge)
        Text("Cards due today: $due", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))
        Button(onClick = { navigate("study") }, modifier = Modifier.fillMaxWidth()) {
            Text("Start study session")
        }
        OutlinedButton(onClick = { navigate("catalog") }, modifier = Modifier.fillMaxWidth()) {
            Text("Browse / download packs")
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

    LaunchedEffectOnce {
        try {
            entries = container.packRepo.fetchCatalog().packs
        } catch (e: Exception) {
            error = e.message ?: "Failed to load catalog"
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Catalog", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
private fun StudyScreen(container: AppContainer, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<List<StudyCard>?>(null) }
    var index by remember { mutableStateOf(0) }

    LaunchedEffectOnce {
        session = container.studyRepo.buildSession(today())
    }

    val cards = session
    when {
        cards == null -> Box { CircularProgressIndicator() }
        cards.isEmpty() || index >= cards.size -> {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(if (cards.isEmpty()) "Nothing due. Download a pack or come back later." else "Session complete 🎉")
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDone) { Text("Back to home") }
            }
        }
        else -> {
            val card = cards[index]
            fun grade(g: Int) {
                scope.launch {
                    container.studyRepo.grade(card.packId, card.item.id, g, today())
                    index += 1
                }
            }
            CardView(card, position = index + 1, total = cards.size, onGrade = ::grade)
        }
    }
}

@Composable
private fun CardView(card: StudyCard, position: Int, total: Int, onGrade: (Int) -> Unit) {
    val item = card.item
    var revealed by remember(item.id) { mutableStateOf(false) }
    var selected by remember(item.id) { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("$position / $total · ${item.topic} · tier ${item.difficulty}", style = MaterialTheme.typography.labelMedium)
        HorizontalDivider()

        if (item.type == ItemType.FLASHCARD) {
            Text(item.front.orEmpty(), style = MaterialTheme.typography.titleLarge)
            if (revealed) {
                Spacer(Modifier.height(8.dp))
                Text(item.back.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(16.dp))
            if (!revealed) {
                item.hint?.let { Text("Hint: $it", style = MaterialTheme.typography.bodySmall) }
                Button(onClick = { revealed = true }, modifier = Modifier.fillMaxWidth()) { Text("Reveal answer") }
            } else {
                GradeButtons(onGrade)
            }
        } else { // MCQ
            Text(item.prompt.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            item.choices.forEachIndexed { i, choice ->
                val correct = item.answerIndex == i
                val show = selected != null
                OutlinedButton(
                    onClick = { if (selected == null) selected = i },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val marker = when {
                        show && correct -> "✓ "
                        show && selected == i -> "✗ "
                        else -> ""
                    }
                    Text("$marker$choice")
                }
            }
            if (selected != null) {
                Spacer(Modifier.height(8.dp))
                item.explanation?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
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
private fun GradeButtons(onGrade: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onGrade(Sm2.GRADE_AGAIN) }, modifier = Modifier.fillMaxWidth()) { Text("Again") }
        Button(onClick = { onGrade(Sm2.GRADE_HARD) }, modifier = Modifier.fillMaxWidth()) { Text("Hard") }
        Button(onClick = { onGrade(Sm2.GRADE_GOOD) }, modifier = Modifier.fillMaxWidth()) { Text("Good") }
        Button(onClick = { onGrade(Sm2.GRADE_EASY) }, modifier = Modifier.fillMaxWidth()) { Text("Easy") }
    }
}

/** Runs a suspend block exactly once when the composable enters composition. */
@Composable
private fun LaunchedEffectOnce(block: suspend () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) { block() }
}

@Composable
private fun Box(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}
