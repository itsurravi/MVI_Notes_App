package com.ravikantsharma.mvinotesapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ravikantsharma.mvinotesapp.ui.components.MainTopAppBar
import com.ravikantsharma.mvinotesapp.ui.screens.NoteDetailScreen
import com.ravikantsharma.mvinotesapp.ui.screens.NotesScreen
import com.ravikantsharma.mvinotesapp.ui.theme.MVINotesAppTheme
import com.ravikantsharma.mvinotesapp.ui.viewmodel.EventManager
import com.ravikantsharma.mvinotesapp.ui.viewmodel.NoteDetailViewModel
import com.ravikantsharma.mvinotesapp.ui.viewmodel.NoteViewModel
import com.ravikantsharma.mvinotesapp.util.Constants.ROUTE_DETAIL_ARG_NAME
import com.ravikantsharma.mvinotesapp.util.Constants.ROUTE_DETAIL_PATH
import com.ravikantsharma.mvinotesapp.util.Constants.ROUTE_HOME
import com.ravikantsharma.mvinotesapp.util.stringResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVINotesAppTheme {
                val context = LocalContext.current

                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                MainApp(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )

                // Observe the centralized event flow
                LaunchedEffect(EventManager) {
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            EventManager.eventsFlow.collect { event ->
                                when (event) {
                                    is EventManager.AppEvent.ShowSnackbar -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.stringResource(event.message),
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    is EventManager.AppEvent.ExitScreen -> navController.navigateUp()
                                    is EventManager.AppEvent.NavigateToDetail -> {
                                        navController.navigate(
                                            ROUTE_DETAIL_PATH.replace(
                                                "{$ROUTE_DETAIL_ARG_NAME}",
                                                "${event.noteId}"
                                            )
                                        )
                                    }
                                    else -> {  }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val showBackButton by remember(currentBackStackEntry) {
        derivedStateOf { navController.previousBackStackEntry != null }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MainTopAppBar(
                navController = navController,
                showBackButton = showBackButton,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (!showBackButton) {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(ROUTE_DETAIL_PATH) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            composable(route = ROUTE_HOME) {
                val noteViewModel = hiltViewModel<NoteViewModel>()
                NotesScreen(viewModel = noteViewModel)
            }
            composable(
                route = ROUTE_DETAIL_PATH,
                arguments = listOf(
                    navArgument(ROUTE_DETAIL_ARG_NAME) { nullable = true },
                )
            ) {
                val noteDetailViewModel = hiltViewModel<NoteDetailViewModel>()
                NoteDetailScreen(noteDetailViewModel)
            }
        }
    }
}