package com.ds.recipesapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecipesApiApplication

fun main(args: Array<String>) {
    runApplication<RecipesApiApplication>(*args)
}
