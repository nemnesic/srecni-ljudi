package com.nemnesic.srecniljudi.controller

import com.nemnesic.srecniljudi.dto.RelationshipResponse
import com.nemnesic.srecniljudi.service.GraphRelationshipService
import com.nemnesic.srecniljudi.service.SupabaseService
import opennlp.tools.cmdline.ArgumentParser.OptionalParameter
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
    fun getRelationshipResponse(
        @RequestParam char1: String,
        @RequestParam char2: String,
        @OptionalParameter @RequestParam(defaultValue = "true") useCache: Boolean,
        @OptionalParameter @RequestParam(defaultValue = "true") cacheResponse: Boolean,
        @OptionalParameter @RequestParam(defaultValue = "true") callChatGpt: Boolean
    ): RelationshipResponse {
        val relationships = graphService.findRelationship(char1, char2)
        if (relationships.isEmpty()) {
            return RelationshipResponse(
                character1 = char1,
                character2 = char2,
                userPrompt = "-",
                chatGptResponse = "No connection found.",
                relationships = emptyList(),
                cachedResponse = false
            )
        }

        val userPrompt = graphService.generateUserPrompt(char1, char2, relationships)

        var chatGptResponse: String? = "No connection found."

        var cachedResponse: Boolean = false

        if (callChatGpt) {
            if (useCache) {
                chatGptResponse = supabaseService.getRelationship(char1, char2)
                if (chatGptResponse != null) {
                    cachedResponse = true
                } else {
                    chatGptResponse = graphService.generateExplanation(userPrompt)
                }
            }
        }
        if (cacheResponse && !cachedResponse) {
            supabaseService.saveRelationship(char1, char2, chatGptResponse)
        }


        return RelationshipResponse(
            character1 = char1,
            character2 = char2,
            userPrompt = userPrompt,
            chatGptResponse = chatGptResponse ?: "No connection found.",
            relationships = relationships,
            cachedResponse = cachedResponse
        )
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
