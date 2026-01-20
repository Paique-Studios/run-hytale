import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.*
import tasks.DownloadDownloaderTask
import tasks.ExtractServerTask
import tasks.RunHytaleServerTask
import util.OsDetector

/**
 * Gradle plugin for Hytale server development.
 * 
 * Provides tasks for downloading, setting up, and running the Hytale server
 * with automatic mod/plugin deployment.
 */
class HytaleServerPlugin : Plugin<Project> {
    
    override fun apply(project: Project) {
        val extension = project.extensions.create<HytaleServerExtension>("hytaleServer")
        val runDir = project.file("run")
        val modsDir = runDir.resolve("mods")
        
        registerDownloadTasks(project, runDir)
        registerSetupTasks(project, runDir, modsDir)
        registerRunTask(project, runDir, modsDir, extension)
    }
    
    private fun registerDownloadTasks(project: Project, runDir: java.io.File) {
        val downloadZipTask = project.tasks.register<DownloadDownloaderTask>("downloadDownloaderZip") {
            this.runDir = runDir
        }
        
        val extractTask = project.tasks.register<Copy>("extractDownloader") {
            group = "hytale"
            description = "Extracts Hytale Downloader"
            dependsOn(downloadZipTask)
            
            val downloaderZip = runDir.resolve("hytale-downloader.zip")
            from(project.zipTree(downloaderZip))
            into(runDir)
            
            doLast {
                if (!OsDetector.isWindows()) {
                    runDir.resolve("hytale-downloader-linux-amd64").setExecutable(true)
                }
            }
        }
        
        val executeDownloaderTask = project.tasks.register<Exec>("runDownloaderCLI") {
            group = "hytale"
            description = "Runs the Hytale Downloader CLI"
            dependsOn(extractTask)
            
            workingDir(runDir)
            standardInput = System.`in`
            
            doFirst {
                commandLine(runDir.resolve(OsDetector.getDownloaderExecutable()).absolutePath)
            }
        }
        
        val extractGameServer = project.tasks.register<ExtractServerTask>("extractGameServer") {
            this.runDir = runDir
            dependsOn(executeDownloaderTask)
        }
        
        project.tasks.register("downloadHytaleServer") {
            group = "hytale"
            description = "Downloads and sets up the Hytale Server (Lifecycle)"
            dependsOn(extractGameServer)
            
            doLast {
                println("Hytale Server setup complete. Files are in ${runDir.absolutePath}")
            }
        }
    }
    
    private fun registerSetupTasks(project: Project, runDir: java.io.File, modsDir: java.io.File) {
        val setupTask = project.tasks.register("setupServerDirectory") {
            group = "hytale"
            description = "Sets up the run directory structure"
            
            doLast {
                if (!runDir.exists()) runDir.mkdirs()
                if (!modsDir.exists()) modsDir.mkdirs()
                println("Run directory structure ready at ${runDir.absolutePath}")
            }
        }
        
        project.tasks.register<Copy>("copyModToServer") {
            group = "hytale"
            description = "Copies the compiled plugin/mod JAR to the server mods directory"
            
            dependsOn("build")
            dependsOn(setupTask)
            
            from(project.layout.buildDirectory.dir("libs"))
            into(modsDir)
            include("*.jar")
            
            doLast {
                println("Copied built jars to ${modsDir.absolutePath}")
            }
        }
    }
    
    private fun registerRunTask(
        project: Project, 
        runDir: java.io.File, 
        modsDir: java.io.File,
        extension: HytaleServerExtension
    ) {
        project.tasks.register<RunHytaleServerTask>("runHytaleServer") {
            this.runDir = runDir
            
            dependsOn("copyModToServer")
            
            val serverJar = runDir.resolve("Server/HytaleServer.jar")
            val assetsZip = runDir.resolve("Assets.zip")
            
            if (!serverJar.exists() || !assetsZip.exists()) {
                dependsOn("downloadHytaleServer")
            }
            
            configure(extension)
        }
    }
}
