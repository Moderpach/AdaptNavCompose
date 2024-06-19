# ListDetail

https://github.com/Moderpach/AdaptNavCompose/assets/34941512/9bfb0bd2-cd8a-4458-95ed-bcc7e96fe891

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
    detail("detail1") {
        // do something
    }
    detail("detail2") {
        // do something
    }
}
```
