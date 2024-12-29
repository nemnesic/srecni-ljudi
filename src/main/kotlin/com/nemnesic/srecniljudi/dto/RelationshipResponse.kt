package com.nemnesic.srecniljudi.dto

import com.nemnesic.srecniljudi.CharacterRelationship

data class RelationshipResponse(
    val character1: String,
    val character2: String,
    val userPrompt: String,
    val chatGptResponse: String,
    val relationships: List<CharacterRelationship>,
    val cachedResponse: Boolean
) {
}
