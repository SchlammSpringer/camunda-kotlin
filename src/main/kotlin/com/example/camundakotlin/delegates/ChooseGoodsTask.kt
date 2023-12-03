package com.example.camundakotlin.delegates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class ChooseGoodsTask: JavaDelegate {
    override fun execute(execution: DelegateExecution?) {
        TODO("Not yet implemented")
    }
}