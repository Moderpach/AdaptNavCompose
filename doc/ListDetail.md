# ListDetail

https://github.com/Moderpach/AdaptNavCompose/assets/34941512/9bfb0bd2-cd8a-4458-95ed-bcc7e96fe891

```kotlin
val navController = rememberListDetailNavController()
ListDetailNavHost(
    navController,
    startDestination = "list1",
) {
    list(
        route = "list1",
        content = { entry: NavBackStackEntry, showTwoPane: Boolean ->
            // do something
        },
        placeholder = { entry: NavBackStackEntry, showTwoPane: Boolean ->
            // do something
        }
    )
    detail("detail1") { entry: NavBackStackEntry, showTwoPane: Boolean ->
        // do something
    }
    detail("detail2") { entry: NavBackStackEntry, showTwoPane: Boolean ->
        // do something
    }
}
```

## Limitations

Back stack entries' lifecycle is managed by `NavControl` in private. So, list pane lifecycle in two pane mode follows the lifecycle outside of the `NavHost`.
