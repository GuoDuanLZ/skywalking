package org.apache.skywalking.apm.plugin.kotlin.coroutine.v1

import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.asContextElement
import org.apache.skywalking.apm.agent.core.context.ContextManager
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class TracingContextElement : AbstractCoroutineContextElement(Key),
        ThreadContextElement<AbstractSpan?> {
    companion object Key : CoroutineContext.Key<TracingContextElement> {
        const val SUSPEND_CALL = "KotlinSuspend"
    }

    var snapshot: ContextSnapshot? = null

    override fun updateThreadContext(context: CoroutineContext): AbstractSpan? {
        snapshot ?: return null

        val span = ContextManager.createLocalSpan(SUSPEND_CALL)
        span.setComponent(ComponentsDefine.KT_SUSPEND)
        ContextManager.continued(snapshot)
        snapshot = null
        return span
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: AbstractSpan?) {
        oldState ?: return
        ContextManager.stopSpan(oldState)
    }
}