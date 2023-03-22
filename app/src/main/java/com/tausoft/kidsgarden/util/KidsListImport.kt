package com.tausoft.kidsgarden.util

import java.io.BufferedReader
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

// Загрузка списка детей
@Singleton
class KidsListImport @Inject constructor() {
    fun readStream(inputStream: InputStream): List<String> {
        val lines: MutableList<String> = mutableListOf()
        BufferedReader(inputStream.reader())
            .use {
                var line = it.readLine()
                while (line != null) {
                    if (line.isNotEmpty())
                        lines.add(line)
                    line = it.readLine()
                }
            }
        return lines
    }
}