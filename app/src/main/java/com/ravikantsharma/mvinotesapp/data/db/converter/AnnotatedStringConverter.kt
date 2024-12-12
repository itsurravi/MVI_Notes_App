package com.ravikantsharma.mvinotesapp.data.db.converter

import androidx.compose.ui.text.AnnotatedString
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.BoldStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.DefaultStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.FormattingAction
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.HeadingStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.HighlightStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.ItalicsStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.StrikeThroughStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.SubtitleStyle
import com.ravikantsharma.mvinotesapp.composetexteditor.editor.UnderlineStyle

class AnnotatedStringConverter {

    @TypeConverter
    fun fromAnnotatedString(value: AnnotatedString): String {
        val spans = value.spanStyles.map {
            AnnotatedStringSpan(
                start = it.start,
                end = it.end,
                style = SpanStyleData(
                    tag = when (it.item) {
                        HighlightStyle -> FormattingAction.Highlight.name
                        HeadingStyle -> FormattingAction.Heading.name
                        SubtitleStyle -> FormattingAction.SubHeading.name
                        BoldStyle -> FormattingAction.Bold.name
                        ItalicsStyle -> FormattingAction.Italics.name
                        UnderlineStyle -> FormattingAction.Underline.name
                        StrikeThroughStyle -> FormattingAction.Strikethrough.name
                        else -> null
                    }
                )
            )
        }
        return Gson().toJson(AnnotatedStringData(value.text, spans))
    }

    @TypeConverter
    fun toAnnotatedString(value: String): AnnotatedString {
        val annotatedStringData = Gson().fromJson(value, AnnotatedStringData::class.java)
        val spanStyles = annotatedStringData.spans.map {
            AnnotatedString.Range(
                item = when (it.style?.tag) {
                    FormattingAction.Highlight.name -> HighlightStyle
                    FormattingAction.Heading.name -> HeadingStyle
                    FormattingAction.SubHeading.name -> SubtitleStyle
                    FormattingAction.Bold.name -> BoldStyle
                    FormattingAction.Italics.name -> ItalicsStyle
                    FormattingAction.Underline.name -> UnderlineStyle
                    FormattingAction.Strikethrough.name -> StrikeThroughStyle
                    else -> DefaultStyle
                },
                start = it.start,
                end = it.end
            )
        }
        return AnnotatedString(annotatedStringData.text, spanStyles)
    }
}

// Data classes for JSON serialization
data class AnnotatedStringData(
    val text: String,
    val spans: List<AnnotatedStringSpan>
)

data class AnnotatedStringSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyleData?
)

data class SpanStyleData(
    val tag: String? // Use a tag like "Heading", "Subtitle", or "Highlight"
)