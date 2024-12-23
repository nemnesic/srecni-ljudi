package com.nemnesic.srecniljudi.controller

import com.nemnesic.srecniljudi.service.GraphRelationshipService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
class GraphController(private val graphService: GraphRelationshipService) {

    @GetMapping("/find-relationship")
    fun findRelationship(
        @RequestParam char1: String,
        @RequestParam char2: String
    ): String? {
        return graphService.findRelationshipAndGenerateExplanation(char1, char2)
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
}