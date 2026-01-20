package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import util.OsDetector
import util.VersionResolver
import java.io.File

/**
 * Extracts the downloaded game server zip.
 */
abstract class ExtractServerTask : DefaultTask() {
    
    @get:Internal
    var runDir: File = project.file("run")
    
    init {
        group = "hytale"
        description = "Extracts the downloaded game server zip"
    }
    
    @TaskAction
    fun extract() {
        val versionResolver = VersionResolver(runDir)
        val version = versionResolver.getVersion()
        
        logger.lifecycle("Detected Hytale version: $version")
        
        val serverZip = versionResolver.getServerZip()
        
        if (serverZip == null) {
            logger.warn("Could not find server zip for version $version. Downloader may have failed or naming mismatch occurred.")
            return
        }
        
        logger.lifecycle("Extracting $serverZip to $runDir")
        project.copy {
            from(project.zipTree(serverZip))
            into(runDir)
        }
    }
}
