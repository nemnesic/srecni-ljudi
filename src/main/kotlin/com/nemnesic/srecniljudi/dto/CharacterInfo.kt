package com.nemnesic.srecniljudi.dto

data class CharacterInfo(
    val name: String,
    val photo: String
) {
    override fun toString(): String {
        return "CharacterInfo(name='$name', photo='$photo')"
    }
}