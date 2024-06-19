# ListDetail

<video src="/doc/ListDetailDemo.mkv" width="540" height="300" controls></video>

```kotlin
val navController = rememberListDetailNavController()
ListDetailNavHost(
    navController,
    startDestination = "list1",
    expectedListPaneWidth = 400.dp,
) {
    list(
        route = "list1",
        content = {
            // do something
        },
        placeholder = {
            // do something
        }
    )
    detail("detail$n") {
        // do something
    }
}
```
