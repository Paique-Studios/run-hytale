package util

import java.io.File

/**
 * Resolves Hytale server version from the downloader CLI.
 * Single Responsibility: Version detection and server zip location.
 */
class VersionResolver(private val runDir: File) {
    
    private val executablePath: File by lazy {
        runDir.resolve(OsDetector.getDownloaderExecutable())
    }
    
    /**
     * Queries the downloader to get the current Hytale version.
     * @throws org.gradle.api.GradleException if version cannot be determined
     */
    fun getVersion(): String {
        val process = ProcessBuilder(executablePath.absolutePath, "-print-version")
            .directory(runDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        
        val version = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()
        
        if (version.isEmpty()) {
            throw org.gradle.api.GradleException("Failed to determine Hytale version.")
        }
        
        return version
    }
    
    /**
     * Finds the server zip file for the detected version.
     * @return The server zip file, or null if not found
     */
    fun getServerZip(): File? {
        val version = getVersion()
        val primaryZip = runDir.resolve("hytale-server-$version.zip")
        val fallbackZip = runDir.resolve("$version.zip")
        
        return when {
            primaryZip.exists() -> primaryZip
            fallbackZip.exists() -> fallbackZip
            else -> null
        }
    }
}
