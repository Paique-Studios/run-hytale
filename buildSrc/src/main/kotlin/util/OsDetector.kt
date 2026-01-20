package util

/**
 * Utility object for OS detection and executable name resolution.
 * Single Responsibility: Detect OS and provide platform-specific executable names.
 */
object OsDetector {
    
    private val osName: String by lazy { 
        System.getProperty("os.name").lowercase() 
    }
    
    fun isWindows(): Boolean = osName.contains("win")
    
    fun isMac(): Boolean = osName.contains("mac")
    
    fun isLinux(): Boolean = !isWindows() && !isMac()
    
    /**
     * Returns the appropriate Hytale downloader executable name for the current OS.
     * @throws org.gradle.api.GradleException if OS is not supported (MacOS)
     */
    fun getDownloaderExecutable(): String = when {
        isWindows() -> "hytale-downloader-windows-amd64.exe"
        isMac() -> throw org.gradle.api.GradleException("MacOS is not supported by the Hytale Downloader.")
        else -> "hytale-downloader-linux-amd64"
    }
}
