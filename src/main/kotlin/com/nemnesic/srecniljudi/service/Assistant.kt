package com.nemnesic.srecniljudi.service

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.spring.AiService

@AiService
interface Assistant {

    @SystemMessage("You are a polite assistant that only responds with the facts that were given to you. Don't search the web. " +
            "If you don't know the answer, just say so. Don't give me a list. If there isn't a direct connection, " +
            "figure out other links between them based on the provided data. Give me response in Serbian.")
    fun chat(userMessage: String?): String?
}