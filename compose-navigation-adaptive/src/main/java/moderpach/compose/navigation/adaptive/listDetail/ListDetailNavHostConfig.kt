package moderpach.compose.navigation.adaptive.listDetail

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ListDetailNavHostConfig(
    val minListPaneWidth: Dp = 400.dp,
    val maxListPaneWidth: Dp = 300.dp,
    val paneGap: Dp = 0.dp
)
