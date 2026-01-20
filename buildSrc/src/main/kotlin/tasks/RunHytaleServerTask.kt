package tasks

import HytaleServerExtension
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import util.ServerResolver
import java.io.File

/**
 * Runs the Hytale Server with proper JVM configuration.
 */
abstract class RunHytaleServerTask : JavaExec() {
    
    @get:Internal
    var runDir: File = project.file("run")
    
    private val serverJar: File
        get() = runDir.resolve("Server/HytaleServer.jar")
    
    private val assetsZip: File
        get() = runDir.resolve("Assets.zip")
    
    private val serverLibDir: File
        get() = runDir.resolve("Server/lib")
    
    private val authEncFile: File
        get() = runDir.resolve("auth.enc")
    
    init {
        group = "hytale"
        description = "Runs the Hytale Server"
    }
    
    fun configure(extension: HytaleServerExtension) {
        workingDir = runDir
        standardInput = System.`in`
        
        jvmArgs(
            "-Xms${extension.minMemory}",
            "-Xmx${extension.maxMemory}"
        )
        jvmArgs(extension.additionalJvmArgs)
        
        args("--assets", "Assets.zip", "--allow-op")
        
        if (extension.javaExecutable != "java") {
            setExecutable(extension.javaExecutable)
        }
    }
    
    override fun exec() {
        configureAtExecutionTime()
        super.exec()
    }
    
    private fun configureAtExecutionTime() {
        if (!serverJar.exists()) {
            throw org.gradle.api.GradleException("Server JAR not found at ${serverJar.absolutePath}. Run downloadHytaleServer first.")
        }
        
        val serverResolver = ServerResolver(serverJar)
        
        val mainClassName = serverResolver.getMainClass()
        mainClass.set(mainClassName)
        logger.lifecycle("Resolved Main-Class: $mainClassName")
        
        val classpathFiles = serverResolver.getClasspath(serverLibDir)
        classpath(project.files(classpathFiles))
        
        if (!authEncFile.exists()) {
            args("--boot-command", "auth login browser", "--boot-command", "auth persistence Encrypted")
        }
    }
}
