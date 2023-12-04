package com.example.camundakotlin.beeregates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class OrderBeer : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        println("Order from: ${execution.getVariable("consultant")} ${execution.getVariable("client")}")
        if (Random.nextBoolean()) {
            throw Exception("NOOOO I cant order beer")
        }
    }
}