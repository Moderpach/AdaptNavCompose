package org.moderpach.adaptivenavigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import moderpach.compose.navigation.adaptive.listDetail.ListDetailNavHost
import moderpach.compose.navigation.adaptive.listDetail.detail
import moderpach.compose.navigation.adaptive.listDetail.list
import moderpach.compose.navigation.adaptive.listDetail.rememberListDetailNavController
import org.moderpach.adaptivenavigation.ui.theme.AdaptiveNavigationDemoTheme

private const val TAG = "ListDetailDemo"

class ListDetailDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdaptiveNavigationDemoTheme {
                ListDetailDemo()
            }
        }
    }
}

@Composable
fun ListDetailDemo() {
    val slideOffset = with(LocalDensity.current) {
        40.dp.roundToPx()
    }
    val navController = rememberListDetailNavController()
    ListDetailNavHost(
        navController,
        startDestination = "list1",
        enterTransition = {
            materialSharedAxisXIn(true, slideOffset)
        },
        exitTransition = {
            materialSharedAxisXOut(true, slideOffset)
        },
        popEnterTransition = {
            materialSharedAxisXIn(false, slideOffset)
        },
        popExitTransition = {
            materialSharedAxisXOut(false, slideOffset)
        },
        expectedListPaneWidth = 400.dp
    ) {
        repeat(3) { n ->
            list(
                route = "list$n",
                content = {
                    ListPane(
                        navController,
                        "list$n"
                    )
                },
                placeholder = {
                    DetailPane(
                        navController,
                        "placeholder$n"
                    )
                }
            )
        }
        repeat(3) { n ->
            detail("detail$n") {
                DetailPane(
                    navController,
                    "detail$n"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListPane(
    navController: NavController,
    name: String
) {
    Scaffold(
        topBar = {
            TopAppBar({
                Text(name)
            })
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = innerPadding
        ) {
            items(3) {
                ListItem(
                    headlineContent = {
                        Text("detail$it")
                    },
                    modifier = Modifier.clickable {
                        navController.navigate("detail$it")
                    }
                )
            }
            items(3) {
                ListItem(
                    headlineContent = {
                        Text("list$it")
                    },
                    modifier = Modifier.clickable {
                        navController.navigate("list$it")
                    }
                )
            }
            item {
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                ListItem(
                    headlineContent = {
                        Text("getLifecycleState")
                    },
                    modifier = Modifier.clickable {
                        Log.i(TAG, "ListPane: lifecycle ${lifecycle.currentState}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailPane(
    navController: NavController,
    name: String
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                Text(name)
            },
                navigationIcon = {
                    IconButton({
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.surfaceContainerLow)
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(3) {
                Button(onClick = {
                    navController.navigate("detail$it")
                }) {
                    Text("detail$it")
                }
            }
            items(3) {
                Button(onClick = {
                    navController.navigate("list$it")
                }) {
                    Text("list$it")
                }
            }
        }
    }
}