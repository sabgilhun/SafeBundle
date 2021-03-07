package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.Parameterizable.Empty
import com.sabgil.processor.ext.isAssignable
import com.sabgil.processor.ext.typeElement
import com.sabgil.processor.types.activityPackageName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


class NavigatorOwnerCheckStep : Step<Empty, Empty>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: Empty
    ): Empty {
        val elementTypeMirror = rootElement.asType()
        val activityTypeMirror = env.typeElement(activityPackageName).asType()

        if (!env.isAssignable(elementTypeMirror, activityTypeMirror)) {
            TODO("NavigatorOwnerCheckStep, error report")
        }

        return Empty
    }
}