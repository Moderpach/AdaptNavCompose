package moderpach.compose.navigation.adaptive.listDetail

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import moderpach.compose.navigation.adaptive.listDetail.ListDetailNavigator.Destination

@Navigator.Name("list_detail")
class ListDetailNavigator(
    val context: Context,
) : Navigator<Destination>() {

    /**
     * Get the map of transitions currently in progress from the [state].
     */
    val transitionsInProgress get() = state.transitionsInProgress

    /**
     * Get the back stack from the [state].
     */
    public val backStack get() = state.backStack

    val isPop = mutableStateOf(false)

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
        isPop.value = false
    }

    override fun createDestination(): Destination {
        return ListDestination(this, {_, _ -> }, {_, _ -> })
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
        isPop.value = true
    }

    /**
     * Callback to mark a navigation in transition as complete.
     *
     * This should be called in conjunction with  as those
     * calls merely start a transition to the target destination, and requires manually marking
     * the transition as complete by calling this method.
     *
     * Failing to call this method could result in entries being prevented from reaching their
     * final [Lifecycle.State].
     */
    public fun onTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    /**
     * NavDestination specific to [ListDetailNavigator]
     */
    @NavDestination.ClassType(Composable::class)
    abstract class Destination(
        navigator: ListDetailNavigator,
        val content: @Composable AnimatedContentScope.(@JvmSuppressWildcards NavBackStackEntry, Boolean) -> Unit
    ) : NavDestination(navigator) {

        var enterTransition: (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null

        var exitTransition: (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null

        var popEnterTransition: (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null

        var popExitTransition: (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null

    }

    @NavDestination.ClassType(Composable::class)
    class ListDestination(
        navigator: ListDetailNavigator,
        content: @Composable() (AnimatedContentScope.(@JvmSuppressWildcards NavBackStackEntry, Boolean) -> Unit),
        val placeholder: @Composable() (AnimatedContentScope.(@JvmSuppressWildcards NavBackStackEntry, Boolean) -> Unit)
    ) : Destination(navigator, content)

    @NavDestination.ClassType(Composable::class)
    class DetailDestination(
        navigator: ListDetailNavigator,
        content: @Composable() (AnimatedContentScope.(@JvmSuppressWildcards NavBackStackEntry, Boolean) -> Unit),
    ) : Destination(navigator, content)

    companion object {
        const val NAME = "list_detail"
    }

}

