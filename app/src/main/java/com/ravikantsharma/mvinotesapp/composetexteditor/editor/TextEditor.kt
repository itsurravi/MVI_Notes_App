package com.ravikantsharma.mvinotesapp.composetexteditor.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextEditor(
    annotatedString: AnnotatedString,
    activeFormats: Set<FormattingAction>,
    onAnnotatedStringChange: (AnnotatedString) -> Unit,
    onFormattingSpansChange: (List<FormattingSpan>) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(annotatedString)) }

    LaunchedEffect(annotatedString) {
        if (annotatedString != textFieldValue.annotatedString) {
            textFieldValue = TextFieldValue(annotatedString)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val newText = newValue.text

            val adjustedSpans = adjustSpanForNewText(
                existingAnnotatedString = textFieldValue.annotatedString,
                newText = newText
            )

            val updatedSpans = addActiveFormattingSpan(
                currentSpans = adjustedSpans,
                selectionStart = newValue.selection.start,
                selectionEnd = newValue.selection.end,
                activeFormats = activeFormats
            )

            val updatedAnnotatedString = applyFormattingSpans(newText, updatedSpans)

            textFieldValue = newValue.copy(annotatedString = updatedAnnotatedString)
            // Notify parent about the updates
            onAnnotatedStringChange(updatedAnnotatedString)
            onFormattingSpansChange(updatedSpans)
        },
        modifier = modifier
    )
}

fun applyFormattingSpans(
    text: String,
    formattingSpans: List<FormattingSpan>
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    // Reapply all formatting spans
    formattingSpans.forEach { span ->
        span.formats.forEach { format ->
            when (format) {
                FormattingAction.Bold -> builder.addStyle(
                    style = BoldStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.Italics -> builder.addStyle(
                    style = ItalicsStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.Underline -> builder.addStyle(
                    style = UnderlineStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.Strikethrough -> builder.addStyle(
                    style = StrikeThroughStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.Highlight -> builder.addStyle(
                    style = HighlightStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.Heading -> builder.addStyle(
                    style = HeadingStyle,
                    start = span.start,
                    end = span.end
                )

                FormattingAction.SubHeading -> builder.addStyle(
                    style = SubtitleStyle,
                    start = span.start,
                    end = span.end
                )
            }
        }
    }

    return builder.toAnnotatedString()
}

fun addActiveFormattingSpan(
    currentSpans: List<FormattingSpan>,
    selectionStart: Int,
    selectionEnd: Int,
    activeFormats: Set<FormattingAction>
): List<FormattingSpan> {
    val updatedSpans = currentSpans.toMutableList()

    if (selectionStart != selectionEnd && activeFormats.isNotEmpty()) {
        updatedSpans.add(
            FormattingSpan(
                start = selectionStart,
                end = selectionEnd,
                formats = activeFormats
            )
        )
    } else if (activeFormats.isEmpty() && selectionStart > 0) {
        val lastCharIndex = selectionStart - 1
        updatedSpans.add(
            FormattingSpan(lastCharIndex, selectionStart, activeFormats)
        )
    }

    return updatedSpans
}

/**
 * Represents a span of formatting applied to a specific range of text.
 */
data class FormattingSpan(val start: Int, val end: Int, val formats: Set<FormattingAction>)

fun adjustSpanForNewText(
    existingAnnotatedString: AnnotatedString,
    newText: String
): List<FormattingSpan> {
    val newTextLength = newText.length

    return existingAnnotatedString.spanStyles.mapNotNull { span ->
        val adjustedStart = span.start.coerceAtMost(newTextLength)
        val adjustedEnd = span.end.coerceAtMost(newTextLength)

        if (adjustedStart < adjustedEnd) {
            FormattingSpan(
                start = adjustedStart,
                end = adjustedEnd,
                formats = extractFormatFromSpanStyle(span.item)
            )
        } else {
            null
        }
    }
}

fun extractFormatFromSpanStyle(item: SpanStyle): Set<FormattingAction> {
    val formats = mutableSetOf<FormattingAction>()

    if (item == HighlightStyle) formats.add(FormattingAction.Highlight)
    if (item == HeadingStyle) formats.add(FormattingAction.Heading)
    if (item == SubtitleStyle) formats.add(FormattingAction.SubHeading)
    if (item == StrikeThroughStyle) formats.add(FormattingAction.Strikethrough)
    if (item == UnderlineStyle) formats.add(FormattingAction.Underline)
    if (item == ItalicsStyle) formats.add(FormattingAction.Italics)
    if (item == BoldStyle) formats.add(FormattingAction.Bold)

    return formats
}
