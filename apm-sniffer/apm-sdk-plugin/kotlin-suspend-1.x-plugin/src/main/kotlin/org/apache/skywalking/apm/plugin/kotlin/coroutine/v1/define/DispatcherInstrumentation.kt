package org.apache.skywalking.apm.plugin.kotlin.coroutine.v1.define

import kotlinx.coroutines.CoroutineDispatcher
import net.bytebuddy.description.NamedElement
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint
import org.apache.skywalking.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassEnhancePluginDefine
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch
import org.apache.skywalking.apm.agent.core.plugin.match.HierarchyMatch
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch

class DispatcherInstrumentation : ClassEnhancePluginDefine() {
    override fun enhanceClass(): ClassMatch {
        return HierarchyMatch.byHierarchyMatch("kotlinx.coroutines.CoroutineDispatcher")
    }

    override fun getConstructorsInterceptPoints(): Array<ConstructorInterceptPoint> {
        return arrayOf()
    }

    override fun getStaticMethodsInterceptPoints(): Array<StaticMethodsInterceptPoint> {
        return arrayOf()
    }

    override fun getInstanceMethodsInterceptPoints(): Array<InstanceMethodsInterceptPoint> {
        return arrayOf(DispatcherInterceptPoint)
    }

    override fun isBootstrapInstrumentation(): Boolean {
        return true
    }

    object DispatcherInterceptPoint : InstanceMethodsInterceptPoint {
        private const val INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.kotlin.coroutine.v1.DispatcherInterceptor"
        private const val ARGUMENT0_CLASS = "kotlin.coroutines.CoroutineContext"
        private const val ARGUMENT1_CLASS = "java.lang.Runnable"

        override fun getMethodsInterceptor(): String {
            return INTERCEPTOR_CLASS
        }

        override fun getMethodsMatcher(): ElementMatcher<MethodDescription> {
            return ElementMatchers.named<NamedElement>("dispatch")
                    .and(ArgumentTypeNameMatch.takesArgumentWithType(0, ARGUMENT0_CLASS))
                    .and(ArgumentTypeNameMatch.takesArgumentWithType(1, ARGUMENT1_CLASS))
        }

        override fun isOverrideArgs(): Boolean {
            return false
        }

    }
}