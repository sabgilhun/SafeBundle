package com.sabgil.processor.common

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

abstract class Step<I : Parameterizable, O : Parameterizable> {

    abstract fun process(rootElement: Element, env: ProcessingEnvironment, input: I): O
}