package util

import java.io.File
import java.util.jar.JarFile

/**
 * Resolves server JAR metadata (Main-Class, classpath).
 * Single Responsibility: JAR introspection and classpath building.
 */
class ServerResolver(private val serverJar: File) {
    
    /**
     * Reads the Main-Class attribute from the server JAR's manifest.
     * @throws org.gradle.api.GradleException if Main-Class cannot be read
     */
    fun getMainClass(): String {
        if (!serverJar.exists()) {
            throw org.gradle.api.GradleException("Server JAR not found: ${serverJar.absolutePath}")
        }
        
        return try {
            JarFile(serverJar).use { jar ->
                jar.manifest.mainAttributes.getValue("Main-Class")
                    ?: throw org.gradle.api.GradleException("Could not find Main-Class in ${serverJar.name}")
            }
        } catch (e: Exception) {
            throw org.gradle.api.GradleException("Failed to read Main-Class from ${serverJar.name}", e)
        }
    }
    
    /**
     * Builds the classpath including the server JAR and all library JARs.
     * @param serverLibDir Directory containing library JARs
     * @return List of files to include in classpath
     */
    fun getClasspath(serverLibDir: File): List<File> {
        val classpathFiles = mutableListOf(serverJar)
        
        if (serverLibDir.exists()) {
            serverLibDir.listFiles { _, name -> name.endsWith(".jar") }
                ?.let { classpathFiles.addAll(it) }
        }
        
        return classpathFiles
    }
}
