package com.studyforge.app.ui

import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

/**
 * Renders a text field as markdown with inline/block LaTeX (`$...$` and `$$...$$`).
 * Inline LaTeX requires MarkwonInlineParserPlugin to be registered alongside JLatexMathPlugin.
 * Rendering is wrapped defensively: any unparseable LaTeX falls back to plain text instead of crashing.
 */
@Composable
fun RichText(text: String, modifier: Modifier = Modifier, textSizeSp: Float = 16f) {
    val context = LocalContext.current
    val colorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val sizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, textSizeSp, context.resources.displayMetrics,
    )
    val markwon = remember(sizePx) {
        runCatching {
            Markwon.builder(context)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(sizePx) { builder ->
                    builder.inlinesEnabled(true)
                    builder.blocksEnabled(true)
                })
                .build()
        }.getOrNull()
    }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
            }
        },
        update = { tv ->
            tv.setTextColor(colorArgb)
            val mw = markwon
            if (mw == null) {
                tv.text = text
            } else {
                runCatching { mw.setMarkdown(tv, text) }
                    .onFailure { tv.text = text }
            }
        },
    )
}
