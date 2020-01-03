package org.apache.skywalking.apm.plugin.grpc.v1;

import io.grpc.ForwardingServerCallListener;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;

import static org.apache.skywalking.apm.plugin.grpc.v1.Constants.*;

public class ServerCallListener extends ForwardingServerCallListener.SimpleForwardingServerCallListener {

    private final ContextSnapshot contextSnapshot;
    private final MethodDescriptor.MethodType methodType;
    private final String operationPrefix;

    protected ServerCallListener(ServerCall.Listener delegate, MethodDescriptor descriptor,
                                 ContextSnapshot contextSnapshot) {
        super(delegate);
        this.contextSnapshot = contextSnapshot;
        this.methodType = descriptor.getType();
        this.operationPrefix = OperationNameFormatUtil.formatOperationName(descriptor) + SERVER;
    }

    @Override
    public void onReady() {
        delegate().onReady();
    }

    @Override
    public void onMessage(Object message) {
        try {
            SpanLayer.asRPCFramework(ContextManager.createLocalSpan(operationPrefix + REQUEST_OBSERVER_ON_MESSAGE_OPERATION_NAME));
            ContextManager.continued(contextSnapshot);
            delegate().onMessage(message);
        } catch (Throwable t) {
            ContextManager.activeSpan().errorOccurred().log(t);
        } finally {
            ContextManager.stopSpan();
        }
    }

    @Override
    public void onComplete() {
        if (methodType != MethodDescriptor.MethodType.UNARY) {
            try {
                SpanLayer.asRPCFramework(ContextManager.createLocalSpan(operationPrefix + RESPONSE_OBSERVER_ON_COMPLETE_OPERATION_NAME));
                ContextManager.continued(contextSnapshot);
                delegate().onComplete();
            } catch (Throwable t) {
                ContextManager.activeSpan().errorOccurred().log(t);
            } finally {
                ContextManager.stopSpan();
            }
        } else {
            delegate().onComplete();
        }
    }

    @Override
    public void onCancel() {
        try {
            SpanLayer.asRPCFramework(ContextManager.createLocalSpan(operationPrefix + REQUEST_OBSERVER_ON_CANCEL_OPERATION_NAME));
            ContextManager.continued(contextSnapshot);
            delegate().onCancel();
        } catch (Throwable t) {
            ContextManager.activeSpan().errorOccurred().log(t);
        } finally {
            ContextManager.stopSpan();
        }
    }

    @Override
    public void onHalfClose() {
        try {
            SpanLayer.asRPCFramework(ContextManager.createLocalSpan(operationPrefix + REQUEST_OBSERVER_ON_COMPLETE_OPERATION_NAME));
            ContextManager.continued(contextSnapshot);
            delegate().onHalfClose();
        } catch (Throwable t) {
            ContextManager.activeSpan().errorOccurred().log(t);
        } finally {
            ContextManager.stopSpan();
        }
    }
}
