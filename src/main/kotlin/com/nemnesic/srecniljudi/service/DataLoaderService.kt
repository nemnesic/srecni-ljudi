package com.nemnesic.srecniljudi.service

import com.nemnesic.srecniljudi.dto.CharacterInfo
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader

@Service
class DataLoaderService {

    fun loadCharactersFromCsvFile(): List<CharacterInfo> {
        val characters = mutableListOf<CharacterInfo>()
        val csvFile = ClassPathResource("characters-and-photos.csv").inputStream
        BufferedReader(InputStreamReader(csvFile)).use { reader ->
            reader.readLine() // Skip the header row if there is one
            reader.lineSequence().forEach { line ->
                val columns = line.split(",")
                val char1 = columns[0].trim()
                val photo = "/img/characters/${columns[1].trim()}"
                characters.add(CharacterInfo(char1, photo))
            }
        }

        // Sort alphabetically and remove duplicates
        return characters.distinct().sortedBy { it.name }
    }
}