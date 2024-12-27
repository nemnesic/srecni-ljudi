package com.nemnesic.srecniljudi.controller

import com.nemnesic.srecniljudi.service.GraphRelationshipService
import com.nemnesic.srecniljudi.service.SupabaseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
class GraphController(private val graphService: GraphRelationshipService, private val supabaseService: SupabaseService) {

    @GetMapping("/find-relationship")
    fun findRelationship(
        @RequestParam char1: String,
        @RequestParam char2: String
    ): String? {
        val savedRelationship = supabaseService.getRelationship(char1, char2)
        if (savedRelationship != null) {
            println("Found saved relationship: $savedRelationship")
            return savedRelationship
        } else {
            println("Relationship not found, generating explanation")
            return graphService.findRelationshipAndGenerateExplanation(char1, char2)
        }
    }

    @GetMapping("/find-relationship-raw")
    fun findRelationshipRaw(
        @RequestParam char1: String,
        @RequestParam char2: String
    ): String? {
        val relationships  = graphService.findRelationship(char1, char2)
        val userMessage = "Explain how ${char1} and ${char2} are connected. Here is json data of relationships of people they may know: ${relationships.map { it.toJson() }.joinToString("\n")}"
        println ("User message: $userMessage")
        return graphService.findRelationship(char1, char2).joinToString { it.toJson() }
    }


    @GetMapping("/graph")
    fun getGraph(): String {
        return graphService.graph.toString()
    }

    @GetMapping("/visualize-relationship")
    fun visualizeRelationship(
        @RequestParam char1: String,
        @RequestParam char2: String
    ): ResponseEntity<ByteArray> {
        val image = graphService.visualizeRelationshipPath(char1, char2)
        return if (image != null) {
            val baos = ByteArrayOutputStream()
            ImageIO.write(image, "png", baos)
            val headers = HttpHeaders()
            headers.contentType = org.springframework.http.MediaType.IMAGE_PNG
            ResponseEntity(baos.toByteArray(), headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/test")
    fun test() {
        supabaseService.saveRelationship("test1", "test2", "test")
    }
}
