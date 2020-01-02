package org.apache.skywalking.apm.plugin.kotlin.coroutine.v1

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor
import java.lang.reflect.Method
import kotlin.coroutines.CoroutineContext

class NewCoroutineContextInterceptor: StaticMethodsAroundInterceptor {
    init {
        this
    }

    override fun beforeMethod(clazz: Class<*>?, method: Method?, allArguments: Array<Any?>?, parameterTypes: Array<Class<*>?>?, result: MethodInterceptResult?){
    }

    override fun afterMethod(
        clazz: Class<*>?,
        method: Method?,
        allArguments: Array<Any?>?,
        parameterTypes: Array<Class<*>?>?,
        ret: Any
    ): Any{
        val context = ret as CoroutineContext
        return if(context[TracingContextElement.Key] != null) context else context + TracingContextElement()
    }

    override fun handleMethodException(clazz: Class<*>?, method: Method?, allArguments: Array<Any?>?, parameterTypes: Array<Class<*>?>?, t: Throwable?){
    }
}

