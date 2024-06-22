package moderpach.compose.navigation.adaptive.listDetail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.createGraph
import androidx.navigation.get

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the navController for this host
 * @param startDestination the route for the start destination
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param route the route for the graph
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param config configuration for layout behavior in this host
 * @param builder the builder used to construct the graph
 */
@Composable
public fun ListDetailNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    route: String? = null,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        { fadeIn(animationSpec = tween(700)) },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        { fadeOut(animationSpec = tween(700)) },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        enterTransition,
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        exitTransition,
    config: ListDetailNavHostConfig = ListDetailNavHostConfig(),
    builder: NavGraphBuilder.() -> Unit
) {
    ListDetailNavHost(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier,
        contentAlignment,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        config
    )
}

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * @param navController the navController for this host
 * @param graph the graph for this host
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param config configuration for layout behavior in this host
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
public fun ListDetailNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        { fadeIn(animationSpec = tween(700)) },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        { fadeOut(animationSpec = tween(700)) },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        enterTransition,
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        exitTransition,
    config: ListDetailNavHostConfig,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }

    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)

    // Then set the graph
    navController.graph = graph

    // Find the ListDetailNavigator, returning early if it isn't found
    // (such as is the case when using TestNavHostController)
    val listDetailNavigator = navController.navigatorProvider.get<Navigator<out NavDestination>>(
        ListDetailNavigator.NAME
    ) as? ListDetailNavigator ?: return

    val currentBackStack by listDetailNavigator.backStack.collectAsState()

    BackHandler(currentBackStack.size > 1) {
        navController.popBackStack()
    }

    DisposableEffect(lifecycleOwner) {
        // Setup the navController with proper owners
        navController.setLifecycleOwner(lifecycleOwner)
        onDispose { }
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    val allVisibleEntries by navController.visibleEntries.collectAsState()

    // Intercept back only when there's a destination to pop
    val visibleEntries by remember {
        derivedStateOf {
            allVisibleEntries.filter { entry ->
                entry.destination.navigatorName == ListDetailNavigator.NAME
            }
        }
    }

    val backStackEntry: NavBackStackEntry? = visibleEntries.lastOrNull()

    val backStack by listDetailNavigator.backStack.collectAsState()

    val listBackStackEntries by remember {
        derivedStateOf {
            backStack.filter { entry ->
                entry.destination is ListDetailNavigator.ListDestination
            }
        }
    }

    // Last list
    val listBackStackEntry = listBackStackEntries.lastOrNull()

    val showTowPane: Boolean = shouldShowTwoPane(config)

    val zIndices = remember { mutableMapOf<String, Float>() }

    if (backStackEntry != null && listBackStackEntry != null) {
        val finalEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            val targetDestination = targetState.destination as ListDetailNavigator.Destination

            if (listDetailNavigator.isPop.value) {
                targetDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createPopEnterTransition(this)
                } ?: popEnterTransition.invoke(this)
            } else {
                targetDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createEnterTransition(this)
                } ?: enterTransition.invoke(this)
            }
        }

        val finalExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            val initialDestination = initialState.destination as ListDetailNavigator.Destination

            if (listDetailNavigator.isPop.value) {
                initialDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createPopExitTransition(this)
                } ?: popExitTransition.invoke(this)
            } else {
                initialDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createExitTransition(this)
                } ?: exitTransition.invoke(this)
            }
        }

        val transitionSpec: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ContentTransform =
            {
                // If the initialState of the AnimatedContent is not in visibleEntries, we are in
                // a case where visible has cleared the old state for some reason, so instead of
                // attempting to animate away from the initialState, we skip the animation.
                if (initialState in visibleEntries) {
                    val initialZIndex = zIndices[initialState.id]
                        ?: 0f.also { zIndices[initialState.id] = 0f }
                    val targetZIndex = when {
                        targetState.id == initialState.id -> initialZIndex
                        listDetailNavigator.isPop.value -> initialZIndex - 1f
                        else -> initialZIndex + 1f
                    }.also { zIndices[targetState.id] = it }

                    ContentTransform(finalEnter(this), finalExit(this), targetZIndex)
                } else {
                    EnterTransition.None togetherWith ExitTransition.None
                }
            }
        val mainTransition = updateTransition(backStackEntry, label = "entry")

        if (showTowPane) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(config.paneGap)
            ) {
                val shouldShowInSameWidth = LocalConfiguration.current.screenWidthDp.dp <=
                        config.maxListPaneWidth * 2 + config.paneGap
                val listModifier =
                    if (shouldShowInSameWidth) {
                        Modifier.weight(1f)
                    } else {
                        Modifier.width(config.maxListPaneWidth)
                    }
                val listTransition = updateTransition(listBackStackEntry, label = "list_entry")
                listTransition.AnimatedContent(
                    modifier = listModifier,
                    transitionSpec = transitionSpec,
                    contentKey = { it.id }
                ) {
                    it.ListLocalOwnersProvider(saveableStateHolder, true) {
                        (it.destination as ListDetailNavigator.Destination)
                            .content(this, it, showTowPane)
                    }
                }
                mainTransition.AnimatedContent(
                    modifier = Modifier.weight(1f),
                    transitionSpec = transitionSpec,
                    contentKey = { it.id }
                ) {
                    // In some specific cases, such as clearing your back stack by changing your
                    // start destination, AnimatedContent can contain an entry that is no longer
                    // part of visible entries since it was cleared from the back stack and is not
                    // animating. In these cases the currentEntry will be null, and in those cases,
                    // AnimatedContent will just skip attempting to transition the old entry.
                    // See https://issuetracker.google.com/238686802
                    val currentEntry = visibleEntries.lastOrNull { entry -> it == entry }

                    // while in the scope of the composable, we provide the navBackStackEntry as the
                    // ViewModelStoreOwner and LifecycleOwner
                    if (currentEntry?.destination is ListDetailNavigator.ListDestination) {
                        currentEntry.PlaceholderLocalOwnersProvider(saveableStateHolder) {
                            (currentEntry.destination as ListDetailNavigator.ListDestination)
                                .placeholder(this, currentEntry, showTowPane)
                        }
                    } else {
                        currentEntry?.LocalOwnersProvider(saveableStateHolder) {
                            (currentEntry.destination as ListDetailNavigator.DetailDestination)
                                .content(this, currentEntry, showTowPane)
                        }
                    }
                }
            }
        } else {
            mainTransition.AnimatedContent(
                modifier,
                transitionSpec = transitionSpec,
                contentAlignment,
                contentKey = { it.id }
            ) {
                // In some specific cases, such as clearing your back stack by changing your
                // start destination, AnimatedContent can contain an entry that is no longer
                // part of visible entries since it was cleared from the back stack and is not
                // animating. In these cases the currentEntry will be null, and in those cases,
                // AnimatedContent will just skip attempting to transition the old entry.
                // See https://issuetracker.google.com/238686802
                val currentEntry = visibleEntries.lastOrNull { entry -> it == entry }

                // while in the scope of the composable, we provide the navBackStackEntry as the
                // ViewModelStoreOwner and LifecycleOwner
                if (currentEntry?.destination is ListDetailNavigator.ListDestination) {
                    currentEntry.ListLocalOwnersProvider(saveableStateHolder, false) {
                        (currentEntry.destination as ListDetailNavigator.ListDestination)
                            .content(this, currentEntry, showTowPane)
                    }
                } else {
                    currentEntry?.LocalOwnersProvider(saveableStateHolder) {
                        (currentEntry.destination as ListDetailNavigator.DetailDestination)
                            .content(this, currentEntry, showTowPane)
                    }
                }
            }
        }
        LaunchedEffect(mainTransition.currentState, mainTransition.targetState) {
            if (mainTransition.currentState == mainTransition.targetState) {
                visibleEntries.forEach { entry ->
                    listDetailNavigator.onTransitionComplete(entry)
                }
                zIndices
                    .filter { it.key != mainTransition.targetState.id }
                    .forEach { zIndices.remove(it.key) }
            }
        }
        DisposableEffect(true) {
            onDispose {
                visibleEntries.forEach { entry ->
                    listDetailNavigator.onTransitionComplete(entry)
                }
            }
        }
    }
}

@Composable
fun shouldShowTwoPane(
    config: ListDetailNavHostConfig,
): Boolean {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return screenWidth >= config.minListPaneWidth * 2 + config.paneGap
}

private fun NavDestination.createEnterTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition? = when (this) {
    is ListDetailNavigator.Destination -> this.enterTransition?.invoke(scope)
    else -> null
}

private fun NavDestination.createExitTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition? = when (this) {
    is ListDetailNavigator.Destination -> this.exitTransition?.invoke(scope)
    else -> null
}

private fun NavDestination.createPopEnterTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition? = when (this) {
    is ListDetailNavigator.Destination -> this.popEnterTransition?.invoke(scope)
    else -> null
}

private fun NavDestination.createPopExitTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition? = when (this) {
    is ListDetailNavigator.Destination -> this.popExitTransition?.invoke(scope)
    else -> null
}
