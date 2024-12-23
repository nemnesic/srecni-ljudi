package com.nemnesic.srecniljudi.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SupabaseService(
    @Value("\${supabase.url}") private val supabaseUrl: String,
    @Value("\${supabase.key}") private val supabaseKey: String
) {


    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun getRelationship(character1: String, character2: String): String? {
        val url = "$supabaseUrl/rest/v1/relationships?character1=eq.$character1&character2=eq.$character2"
        val request = Request.Builder()
            .url(url)
            .header("apikey", supabaseKey)
            .header("Authorization", "Bearer $supabaseKey")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val body = response.body?.string()
                val data = objectMapper.readTree(body)
                return if (data.isArray && data.size() > 0) {
                    data[0]["relationship"].asText()
                } else {
                    null
                }
            } else {
                throw Exception("Failed to fetch data: ${response.message}")
            }
        }
    }

    fun saveRelationship(character1: String, character2: String, relationship: String?) {
        val url = "$supabaseUrl/rest/v1/relationships"
        val payload = mapOf(
            "character1" to character1,
            "character2" to character2,
            "relationship" to relationship
        )
        val requestBody = objectMapper.writeValueAsString(payload).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .header("apikey", supabaseKey)
            .header("Authorization", "Bearer $supabaseKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to save data: ${response.message}")
            }
        }
    }
}
