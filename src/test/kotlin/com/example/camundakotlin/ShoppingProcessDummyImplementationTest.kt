package com.example.camundakotlin

import com.example.camundakotlin.Process.ActivityIds.*
import com.example.camundakotlin.Process.Variables.*
import com.example.camundakotlin.delegates.*
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.springframework.boot.test.context.SpringBootTest
import kotlin.math.min

@SpringBootTest
class ShoppingProcessDummyImplementationTest(
    private val runtimeService: RuntimeService,
    @MockkBean private val createShoppingListTask: CreateShoppingListTask,
    @MockkBean private val prepareMeansOfPaymentTask: PrepareMeansOfPaymentTask,
    @MockkBean private val prepareShoppingCartDepositTask: PrepareShoppingCartDepositTask,
    @MockkBean private val checkShoppingCartMandatoryTask: CheckShoppingCartMandatoryTask,
    @MockkBean private val takeShoppingCartTask: TakeShoppingCartTask,
    @MockkBean private val chooseGoodsTask: ChooseGoodsTask,
    @MockkBean private val payGoodsTask: PayGoodsTask,
) : DescribeSpec({
    /** All goods which may show up on a shopping list. */

    /** The names of all goods supported by this dummy implementation. */
    val names = listOf(
        "Water", "Bread", "Tomatoes", "Milk", "Soap", "Chewing gum", "Pencil", "College block",
        "Spoon", "Fork", "Knife", "Plate", "Tablecloth", "Chair", "Table", "Frying pan", "Stock pot"
    )

    /** Randomly chosen [names] optionally with a given [resultSize]. */
    fun randomNames(resultSize: Int = names.size / 2) =
        names.toMutableList().shuffled().subList(0, min(names.size, resultSize)).toList()


    every { createShoppingListTask.execute(any()) } just runs
    every { prepareMeansOfPaymentTask.execute(any()) } just runs
    every { prepareShoppingCartDepositTask.execute(any()) } just runs
    every { checkShoppingCartMandatoryTask.execute(any()) } just runs
    every { takeShoppingCartTask.execute(any()) } just runs
    every { chooseGoodsTask.execute(any()) } just runs
    every { payGoodsTask.execute(any()) } just runs

    describe("start process with") {
        it("simplest completion path") {
            val defaultValues = mapOf(
                CART_NEEDED.variable to false,
                CART_MANDATORY.variable to false,
                SHOPPING_LIST.variable to listOf("Bread"),
                ALL_GOODS_BOUGHT.variable to true
            )
            val processInstance: ProcessInstance = runtimeService.startProcessInstanceByKey(
                Process.NAME, defaultValues
            )

            assertThat(processInstance).isEnded
            assertThat(processInstance).hasPassedInOrder(
                CREATE_SHOPPING_LIST.variable, PREPARE_MEANS_OF_PAYMENT.variable, PREPARE_SHOPPING_COMPLETED.variable,
                CHOOSE_GOODS.variable, PAY_GOODS.variable, SHOPPING_COMPLETED.variable,
            )
            assertThat(processInstance).hasNotPassed(
                PREPARE_CART_DEPOSIT.variable, TAKE_CART.variable, CREATE_NEW_SHOPPING_LIST.variable
            )

        }

        it("cart needed but not mandatory") {
            val shoppingList = randomNames(5)
            val processVariables = mapOf(
                CART_NEEDED.variable to true,
                CART_MANDATORY.variable to false,
                SHOPPING_LIST.variable to shoppingList,
                ALL_GOODS_BOUGHT.variable to true,
            )

            val processInstance: ProcessInstance = runtimeService.startProcessInstanceByKey(
                Process.NAME, processVariables
            )

            assertThat(processInstance).isEnded
            assertThat(processInstance).hasNotPassed(CREATE_NEW_SHOPPING_LIST.variable)
            assertThat(processInstance).hasNotPassed(SHOPPING_FAILED.variable)
        }
    }
})