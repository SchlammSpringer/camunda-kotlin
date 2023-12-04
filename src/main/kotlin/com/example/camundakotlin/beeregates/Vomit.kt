package com.example.camundakotlin.beeregates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service

@Service
class Vomit : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        println("Here the magic happens vomit: ${execution.getVariable("vomit")}");
    }
}