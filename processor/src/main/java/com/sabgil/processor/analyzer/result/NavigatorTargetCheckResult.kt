package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.kelement.KotlinDelegateElement
import javax.lang.model.element.Element

data class NavigatorTargetCheckResult(
    val argumentsMap: Map<String, KotlinDelegateElement>,
    val targetElement: Element
) : Parameterizable