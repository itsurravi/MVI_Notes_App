package com.ravikantsharma.mvinotesapp.ui.viewmodel

import androidx.compose.ui.text.AnnotatedString
import app.cash.turbine.test
import com.ravikantsharma.mvinotesapp.BaseUnitTest
import com.ravikantsharma.mvinotesapp.data.db.model.Note
import com.ravikantsharma.mvinotesapp.data.repository.NoteRepository
import com.ravikantsharma.mvinotesapp.ui.intent.NoteIntent
import com.ravikantsharma.mvinotesapp.util.hashedString
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import java.time.OffsetDateTime

class NoteViewModelTest: BaseUnitTest() {
    val repository: NoteRepository = mock()

    private val expectedNotes = listOf(
        Note(
            title = "Test 1",
            description = AnnotatedString(text = ""),
            encrypt = false,
            password = null,
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        ),
        Note(
            title = "Test 2",
            description = AnnotatedString(text = "Test 2"),
            encrypt = true,
            password = "anitaa".hashedString(),
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )
    )

    @Test
    fun `when viewmodel is first initialized then returns initial state`() = runTest {
        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertEquals(0, initialState.notes.size)
            assertEquals(null, initialState.passwordErrorResId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when db has notes available returns list of notes`() = runTest {
        `when`(repository.getNotes().first()).thenReturn(expectedNotes)
        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            // initial state
            awaitItem()

            viewModel.handleIntent(NoteIntent.LoadNotes)
            // loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(0, loadingState.notes.size)
            assertEquals(null, loadingState.passwordErrorResId)

            // notes are loaded successfully
            val finalState = awaitItem()
            assertEquals(expectedNotes.size, finalState.notes.size)
            assertEquals(expectedNotes[0], finalState.notes.first())
            assertEquals(expectedNotes[1], finalState.notes.last())
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when db has no notes then returns empty list`() = runTest {
        `when`(repository.getNotes().first()).thenReturn(emptyList())

        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            // initial state
            awaitItem()

            viewModel.handleIntent(NoteIntent.LoadNotes)
            // loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(0, loadingState.notes.size)
            assertEquals(null, loadingState.passwordErrorResId)

            // notes are loaded successfully but is empty
            val finalState = awaitItem()
            assertTrue(finalState.notes.isEmpty())
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }
}