package com.nemnesic.srecniljudi.service

import com.nemnesic.srecniljudi.CharacterRelationship
import dev.ai4j.openai4j.chat.ChatCompletionRequest
import dev.langchain4j.data.message.ChatMessage
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYTextAnnotation
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader

class RelationshipEdge(var relationship: String = "") : DefaultEdge() {
    override fun toString(): String {
        return relationship
    }
}

@Service
class GraphRelationshipService(val assistant: Assistant, val supabaseService: SupabaseService) {


    val relationships = mutableListOf<Triple<String, String, String>>()

    init {
        loadGraphFromCSV("srecni-ljudi-input.csv")
    }

    val graph = SimpleGraph<String, RelationshipEdge>(RelationshipEdge::class.java)
    private fun loadGraphFromCSV(fileName: String) {
        val csvFile = ClassPathResource("srecni-ljudi-input.csv").inputStream
        BufferedReader(InputStreamReader(csvFile)).use { reader ->
            reader.readLine() // Skip the header row if there is one
            reader.lineSequence().forEach { line ->
                val columns = line.split(",")
                val char1 = columns[0].trim()
                val char2 = columns[1].trim()
                val relationship = columns[2].trim()

                if (relationship != "0") {
                    graph.addVertex(char1)
                    graph.addVertex(char2)
                    val edge = graph.addEdge(char1, char2)
                    if (edge != null) {
                        edge.relationship = relationship
                    }
                }
            }
        }
    }

    fun findRelationship(char1: String, char2: String): List<CharacterRelationship> {
        val dijkstra = DijkstraShortestPath(graph)
        val path = dijkstra.getPath(char1, char2)
        val characterRelationships = mutableListOf<CharacterRelationship>()
        return if (path != null) {
            //create list of character relationships
            path.edgeList.forEach { edge ->
                val relationshipEdge = edge as RelationshipEdge
                characterRelationships.add(
                    CharacterRelationship(
                        path.graph.getEdgeSource(edge),
                        path.graph.getEdgeTarget(edge),
                        relationshipEdge.relationship
                    )
                )
            }
            return characterRelationships
        } else {
            //return empty list if no path is found
            emptyList()
        }
    }

    fun visualizeRelationshipPath(char1: String, char2: String): BufferedImage? {
        val dijkstra = DijkstraShortestPath(graph)
        val path = dijkstra.getPath(char1, char2)

        return if (path != null) {
            val series = XYSeries("Relationship Path")
            path.vertexList.forEachIndexed { index, vertex ->
                series.add(index.toDouble(), vertex.hashCode().toDouble())
            }

            val dataset = XYSeriesCollection(series)
            val chart: JFreeChart = ChartFactory.createXYLineChart(
                "Relationship Path Between $char1 and $char2",
                "Step",
                "Character",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            )

            val plot: XYPlot = chart.xyPlot
            val renderer = XYLineAndShapeRenderer()
            renderer.setSeriesPaint(0, Color.BLUE)
            plot.renderer = renderer

            // Add edge labels (relationships)
            val edgeLabels = path.edgeList.mapIndexed { index, edge ->
                index.toDouble() to (edge as RelationshipEdge).relationship
            }.toMap()
            edgeLabels.forEach { (index, label) ->
                plot.addAnnotation(XYTextAnnotation(label, index, series.getY(index.toInt()).toDouble()))
            }

            chart.createBufferedImage(800, 600)
        } else {
            null
        }
    }

    fun generateExplanation(char1: String, char2: String, relationShips: List<CharacterRelationship>): String? {
        if (relationShips.isEmpty()) {
            return "No connection found."
        }

        // Create a narrative description for LLM input

        val userMessage = "Explain how ${char1} and ${char2} are connected. Here is json data of relationships of people they may know: ${
            relationShips.map { it.toJson() }.joinToString("\n")
        }"

        println("User message: $userMessage")
        //return userMessage
        val response = assistant.chat(userMessage)

        supabaseService.saveRelationship(char1, char2, response)

        return response
    }

    fun findRelationshipAndGenerateExplanation(char1: String, char2: String): String? {
        findRelationship(char1, char2).let { relationships ->
            return generateExplanation(char1, char2, relationships)
        }

    }

}
