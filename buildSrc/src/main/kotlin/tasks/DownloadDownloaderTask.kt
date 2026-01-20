package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI

/**
 * Downloads the Hytale downloader zip from the official URL.
 */
abstract class DownloadDownloaderTask : DefaultTask() {
    
    companion object {
        private const val DOWNLOAD_URL = "https://downloader.hytale.com/hytale-downloader.zip"
    }
    
    @get:Internal
    var runDir: File = project.file("run")
    
    @get:OutputFile
    val downloaderZip: File
        get() = runDir.resolve("hytale-downloader.zip")
    
    init {
        group = "hytale"
        description = "Downloads Hytale Downloader Zip"
    }
    
    @TaskAction
    fun download() {
        if (!runDir.exists()) runDir.mkdirs()
        
        if (downloaderZip.exists()) {
            logger.lifecycle("Downloader already exists at ${downloaderZip.absolutePath}")
            return
        }

        logger.lifecycle("Downloading Hytale Downloader...")
            val downloadUrl = URI(DOWNLOAD_URL).toURL()
            downloaderZip.outputStream().use { output ->
                downloadUrl.openStream().use { input ->
                    input.copyTo(output)
                }
            }
            logger.lifecycle("Downloaded to ${downloaderZip.absolutePath}")
    }
}
