package com.example.peitoinfinity

import org.junit.Test
import java.io.File
import java.util.zip.ZipFile

class ExampleUnitTest {
    @Test
    fun listRoom3Classes() {
        val userHome = System.getProperty("user.home")
        val gradleCache = File(userHome, ".gradle/caches")
        println("Scanning Gradle cache at: ${gradleCache.absolutePath}")

        val jarFiles = mutableListOf<File>()
        gradleCache.walkTopDown().forEach { file ->
            if (file.name.contains("room3") && (file.extension == "jar" || file.extension == "aar")) {
                jarFiles.add(file)
            }
        }

        println("Found ${jarFiles.size} Room 3 related files:")
        jarFiles.forEach { file ->
            println(" - ${file.absolutePath}")
            try {
                if (file.extension == "jar") {
                    listJarContents(file)
                } else if (file.extension == "aar") {
                    // Extract classes.jar from AAR
                    ZipFile(file).use { zip ->
                        val entry = zip.getEntry("classes.jar")
                        if (entry != null) {
                            val tempFile = File.createTempFile("classes_temp", ".jar")
                            tempFile.deleteOnExit()
                            zip.getInputStream(entry).use { input ->
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            println("   (Extracting classes.jar from AAR)")
                            listJarContents(tempFile)
                        }
                    }
                }
            } catch (e: Exception) {
                println("   Error reading file: ${e.message}")
            }
        }
    }

    private fun listJarContents(jarFile: File) {
        ZipFile(jarFile).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val name = entry.name
                if (name.endsWith(".class")) {
                    val className = name.removeSuffix(".class").replace('/', '.')
                    if (className.contains("room") || className.contains("Type")) {
                        println("     Class: $className")
                    }
                }
            }
        }
    }
}