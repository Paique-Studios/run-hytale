/**
 * Extension for configuring the Hytale Server plugin.
 */
open class HytaleServerExtension {
    /** Hytale server version (saving this just in case it turns out to be useful like on Minecraft) */
    var serverVersion: String = "latest"
    
    /** Path to Java executable (should be Java 25+) */
    var javaExecutable: String = "java"
    
    /** Minimum heap memory */
    var minMemory: String = "4G"
    
    /** Maximum heap memory */
    var maxMemory: String = "4G"
    
    /** Server bind port */
    var serverPort: Int = 5520
    
    /** Additional JVM arguments */
    var additionalJvmArgs: List<String> = emptyList()
}
