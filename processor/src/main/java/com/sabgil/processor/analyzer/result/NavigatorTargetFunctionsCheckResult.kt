package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.kelement.KotlinDelegateElement
import com.sabgil.processor.common.model.kelement.KotlinFunElement
import com.squareup.kotlinpoet.FunSpec
import javax.lang.model.element.Element

data class NavigatorTargetFunctionsCheckResult(
    val argumentsMap: Map<String, KotlinDelegateElement>,
    val targetElement: Element,
    val targetFunctionElements: List<KotlinFunElement>
) : Parameterizable