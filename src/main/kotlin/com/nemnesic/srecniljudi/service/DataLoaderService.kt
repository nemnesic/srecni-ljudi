package com.nemnesic.srecniljudi.service

import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.FileReader

@Service
class DataLoaderService {

    fun loadCharactersFromCsvFile(): List<String> {
        val characters = mutableListOf<String>()
        val csvFile = "src/main/resources/srecni-ljudi-input.csv" // Replace with your CSV file path
        BufferedReader(FileReader(csvFile)).use { reader ->
            reader.readLine() // Skip the header row if there is one
            reader.lineSequence().forEach { line ->
                val columns = line.split(",")
                if (columns.size >= 3) {
                    val char1 = columns[0].trim()
                    val char2 = columns[1].trim()
                    characters.add(char1)
                    characters.add(char2)
                }
            }
        }

        //sort aphabeticly and remove duplicates

        return characters.distinct().sorted()
    }
}