# Hytale Server Gradle Plugin
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/D1D41SN93D)

---
Hey, take a look on my template for Hytale mods!
https://github.com/Paique-Studios/hytale-mod-template
---

This plugin helps manage a local Hytale server for mod development. It handles server download, setup, and execution within IntelliJ IDEA.

## Prerequisites

- **Java 25**: You must have Java 25 installed to run the Hytale server.
- **Gradle**: The project includes a Gradle wrapper.

## Setup

1.  **Download and Setup Server**:
    ```bash
    ./gradlew downloadHytaleServer
    ```
    This command will:
    - Download the Hytale Downloader CLI.
    - Extract it to the `run/` directory.
    - Run the downloader to fetch the server files (requires authentication on first run).

2.  **Build and Deploy Mod**:
    ```bash
    ./gradlew copyModToServer
    ```
    This builds your mod and copies it to `run/mods/`.

3.  **Run Server**:
    ```bash
    ./gradlew runHytaleServer
    ```
    Or use the **Gradle** tool window in IntelliJ: `Tasks > hytale > runHytaleServer`.
    
    *Note: The server launches with optimized arguments (`-XX:AOTCache=HytaleServer.aot`, `--assets Assets.zip`).*

## Configuration

You can configure the server settings in `build.gradle.kts`:

```kotlin
hytaleServer {
    minMemory = "2G"
    maxMemory = "4G"
    javaExecutable = "java" // Path to Java 25 executable if not in PATH
    serverVersion = "latest" // (Currently unused by downloader CLI but reserved)
    additionalJvmArgs = listOf("-Dsome.arg=value")
}
```

### Available Tasks

| Task | Description |
|------|-------------|
| `downloadHytaleServer` | Downloads and sets up the Hytale Server (Executes all tasks if needed) |
| `runHytaleServer` | Runs the server with your mods |
| `copyModToServer` | Builds and deploys mod to `run/mods/` |
| `setupServerDirectory` | Creates run directory structure |

## Debugging with IntelliJ IDEA

Just run runHytaleServer task as debug on IntelliJ IDEA.

## Authenticating the Server

On the first run of `downloadHytaleServer` or `runHytaleServer`, you may be prompted to authenticate. Follow the instructions in the console to link your server to your Hytale account.
