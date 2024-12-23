package com.nemnesic.srecniljudi

data class CharacterRelationship(
    val character1: String,
    val character2: String,
    val relationship: String
) {
    override fun toString(): String {
        return "$character1 - $relationship - $character2"
    }

    fun toJson(): String {
        return "{\"character1\": \"$character1\", \"relationship\": \"$relationship\", \"character2\": \"$character2\"}"
    }
}