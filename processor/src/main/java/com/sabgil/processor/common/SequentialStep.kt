package com.sabgil.processor.common

import com.sabgil.processor.common.Parameterizable.Empty
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class SequentialStep(
    private val element: Element,
    private val env: ProcessingEnvironment
) {
    fun <O : Parameterizable> chain(step: Step<Empty, O>) = Bridge(processStep(step, Empty))

    private fun <I : Parameterizable, O : Parameterizable> processStep(
        step: Step<I, O>,
        input: I
    ) = step.process(element, env, input)

    inner class Bridge<I : Parameterizable>(private val input: I) {

        fun <O : Parameterizable> chain(step: Step<I, O>) =
            Bridge(this@SequentialStep.processStep(step, input))

        fun result() = input
    }
}