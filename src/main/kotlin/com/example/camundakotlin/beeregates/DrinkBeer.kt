package com.example.camundakotlin.beeregates

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service
import kotlin.random.Random


@Service
class DrinkBeer : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val beerWithOutAlcohol: Boolean = Random.nextBoolean()
        when {
            beerWithOutAlcohol -> throw BpmnError("WITHOUTALCOHOL")
            else -> execution.setVariable("drunk", Random.nextBoolean())
        }
    }
}