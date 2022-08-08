package ru.smartro.worknote.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ZipManager {

    fun zip(files: Array<File>?, zipFile: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { output ->
            files?.forEach { file ->
                if (file.length() > 1) {
                    FileInputStream(file).use { input ->
                        BufferedInputStream(input).use { origin ->
                            val entry = ZipEntry(file.name)
                            output.putNextEntry(entry)
                            origin.copyTo(output, 1024)
                        }
                    }
                }
            }
        }
    }

    //If we do not set encoding as "ISO-8859-1", European characters will be replaced with '?'.
    fun unzip(files: List<File>, zipFile: ZipFile) {
        zipFile.use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    BufferedReader(InputStreamReader(input, "ISO-8859-1")).use { reader ->
                        files.find { it.name.contains(entry.name) }?.run {
                            BufferedWriter(FileWriter(this)).use { writer ->
                                var line: String? = null
                                while ({ line = reader.readLine(); line }() != null) {
                                    writer.append(line).append('\n')
                                }
                                writer.flush()
                            }
                        }
                    }
                }
            }
        }
    }
}