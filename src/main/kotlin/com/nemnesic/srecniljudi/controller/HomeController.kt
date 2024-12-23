package com.nemnesic.srecniljudi.controller

import com.nemnesic.srecniljudi.service.DataLoaderService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/home")
class HomeController(val dataLoaderService: DataLoaderService) {

    @RequestMapping
    fun home(model: Model): String {
        model.addAttribute("characters", dataLoaderService.loadCharactersFromCsvFile())
        return "home"
    }
}