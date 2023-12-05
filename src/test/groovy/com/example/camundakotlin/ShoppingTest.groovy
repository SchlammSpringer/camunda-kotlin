package com.example.camundakotlin

import static com.example.camundakotlin.Process.ActivityIds.*
import static com.example.camundakotlin.Process.Variables.*

import com.example.camundakotlin.delegates.*
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.test.Deployment
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension
import org.junit.jupiter.api.extension.RegisterExtension
import spock.lang.Specification

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import static org.camunda.bpm.engine.test.mock.Mocks.register

@Deployment(resources = 'process.bpmn')
class ShoppingTest extends Specification {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder(
            new ProcessCoverageInMemProcessEngineConfiguration()
                    .setHistory(ProcessEngineConfiguration.HISTORY_FULL)
    ).build()

    def setup() {
        CreateShoppingListTask createShoppingListTask = Mock()
        PrepareMeansOfPaymentTask prepareMeansOfPaymentTask = Mock()
        PrepareShoppingCartDepositTask prepareShoppingCartDepositTask = Mock()
        CheckShoppingCartMandatoryTask checkShoppingCartMandatoryTask = Mock()
        TakeShoppingCartTask takeShoppingCartTask = Mock()
        ChooseGoodsTask chooseGoodsTask = Mock()
        PayGoodsTask payGoodsTask = Mock()

        register "createShoppingListTask", createShoppingListTask
        register "prepareMeansOfPaymentTask", prepareMeansOfPaymentTask
        register "prepareShoppingCartDepositTask", prepareShoppingCartDepositTask
        register "checkShoppingCartMandatoryTask", checkShoppingCartMandatoryTask
        register "takeShoppingCartTask", takeShoppingCartTask
        register "chooseGoodsTask", chooseGoodsTask
        register "payGoodsTask", payGoodsTask

        createShoppingListTask.execute(_) >> { println("Creating shopping list") }
        prepareMeansOfPaymentTask.execute(_) >> { println("Preparing means of payment") }
        prepareShoppingCartDepositTask.execute(_) >> { println("Preparing shopping cart deposit") }
        checkShoppingCartMandatoryTask.execute(_) >> { println("Checking if shopping cart is mandatory") }
        takeShoppingCartTask.execute(_) >> { println("Taking shopping cart") }
        chooseGoodsTask.execute(_) >> { println("Choosing goods") }
        payGoodsTask.execute(_) >> { println("Paying for goods") }
    }

    def "start process with simplest completion path"() {
        RuntimeService runtimeService = runtimeService()
        given:
        def defaultValues = [
                (CART_NEEDED.variable)     : false,
                (CART_MANDATORY.variable)  : false,
                (CART_MANDATORY.variable)  : false,
                (SHOPPING_LIST.variable)   : ["Bread"],
                (ALL_GOODS_BOUGHT.variable): true
        ]

        when:
        def processInstance = runtimeService.startProcessInstanceByKey("Shopping", defaultValues)

        then:
        assertThat(processInstance).isEnded()
        assertThat(processInstance).hasPassedInOrder(
                CREATE_SHOPPING_LIST.variable, PREPARE_MEANS_OF_PAYMENT.variable, PREPARE_SHOPPING_COMPLETED.variable,
                CHOOSE_GOODS.variable, PAY_GOODS.variable, SHOPPING_COMPLETED.variable,
        )
        assertThat(processInstance).hasNotPassed(
                PREPARE_CART_DEPOSIT.variable, TAKE_CART.variable, CREATE_NEW_SHOPPING_LIST.variable
        )
    }
}
