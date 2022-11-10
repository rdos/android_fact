package ru.smartro.worknote.presentation.work.utils

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
    fun unzipWTF(files: List<File>, zipFile: ZipFile) {
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

        /**
         * @param zipFilePath
         * @param destDirectory
         * @throws IOException
         */
        fun unzip(zipFilePath: File, destDirectory: String) {

            File(destDirectory).run {
                if (!exists()) {
                    mkdirs()
                }
            }

            ZipFile(zipFilePath).use { zip ->

                zip.entries().asSequence().forEach { entry ->

                    zip.getInputStream(entry).use { input ->


                        val filePath = destDirectory + File.separator + entry.name

                        if (!entry.isDirectory) {
                            // if the entry is a file, extracts it
                            extractFile(input, filePath)
                        } else {
                            // if the entry is a directory, make the directory
                            val dir = File(filePath)
                            dir.mkdir()
                        }

                    }

                }
            }
        }

        /**
         * Extracts a zip entry (file entry)
         * @param inputStream
         * @param destFilePath
         * @throws IOException
         */
        @Throws(IOException::class)
        private fun extractFile(inputStream: InputStream, destFilePath: String) {
            val bos = BufferedOutputStream(FileOutputStream(destFilePath))
            val bytesIn = ByteArray(BUFFER_SIZE)
            var read: Int
            while (inputStream.read(bytesIn).also { read = it } != -1) {
                bos.write(bytesIn, 0, read)
            }
            bos.close()
        }

        /**
         * Size of the buffer to read/write data
         */
        private const val BUFFER_SIZE = 4096

}