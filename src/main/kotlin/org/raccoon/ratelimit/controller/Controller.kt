package org.raccoon.ratelimit.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/api/items/change")
class Controller {
    @GetMapping
    fun getItems(): String{
        return "aa"
    }
}