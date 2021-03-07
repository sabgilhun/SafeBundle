package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.Parameterizable
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

abstract class Step<I : Parameterizable, O : Parameterizable> {

    abstract fun process(rootElement: Element, env: ProcessingEnvironment, input: I): O
}