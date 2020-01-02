package org.apache.skywalking.apm.plugin.kotlin.coroutine.v1

import org.apache.skywalking.apm.agent.core.context.ContextManager
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult
import java.lang.reflect.Method
import kotlin.coroutines.CoroutineContext

class DispatcherInterceptor: InstanceMethodsAroundInterceptor{
    override fun beforeMethod(objInst: EnhancedInstance?, method: Method?, allArguments: Array<out Any>, argumentsTypes: Array<out Class<*>>?, result: MethodInterceptResult?) {
        val context = allArguments[0] as CoroutineContext
        context[TracingContextElement.Key]?.snapshot = if(ContextManager.isActive()){
            ContextManager.capture()
        }else null
    }

    override fun afterMethod(objInst: EnhancedInstance?, method: Method?, allArguments: Array<out Any>?, argumentsTypes: Array<out Class<*>>?, ret: Any?): Any? {
        return ret
    }

    override fun handleMethodException(objInst: EnhancedInstance?, method: Method?, allArguments: Array<out Any>?, argumentsTypes: Array<out Class<*>>?, t: Throwable?) {
    }

}