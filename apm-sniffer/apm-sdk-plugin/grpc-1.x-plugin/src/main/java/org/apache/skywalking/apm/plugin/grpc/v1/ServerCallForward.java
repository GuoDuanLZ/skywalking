package org.apache.skywalking.apm.plugin.grpc.v1;

import io.grpc.*;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;

import static org.apache.skywalking.apm.plugin.grpc.v1.Constants.*;

public class ServerCallForward extends ForwardingServerCall.SimpleForwardingServerCall {

    private final String operationPrefix;
    private final ContextSnapshot contextSnapshot;

    protected ServerCallForward(ServerCall delegate, ContextSnapshot contextSnapshot) {
        super(delegate);
        this.operationPrefix = OperationNameFormatUtil.formatOperationName(delegate.getMethodDescriptor()) + SERVER;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public void sendHeaders(Metadata responseHeaders) {
        delegate().sendHeaders(responseHeaders);
    }

    @Override
    public void close(Status status, Metadata trailers) {
        try {
            AbstractSpan span = ContextManager.createLocalSpan(operationPrefix + RESPONSE_OBSERVER_ON_ClOSE_OPERATION_NAME);
            SpanLayer.asRPCFramework(span);
            switch (status.getCode()) {
                case CANCELLED:
                case INVALID_ARGUMENT:
                case DEADLINE_EXCEEDED:
                case NOT_FOUND:
                case ALREADY_EXISTS:
                case PERMISSION_DENIED:
                case RESOURCE_EXHAUSTED:
                case FAILED_PRECONDITION:
                case ABORTED:
                case OUT_OF_RANGE:
                case DATA_LOSS:
                case UNAUTHENTICATED:
                case UNIMPLEMENTED:
                case UNAVAILABLE:
                    span.log(status.asRuntimeException());
                    Tags.STATUS_CODE.set(span, status.getCode().name());
                    break;
                case UNKNOWN:
                case INTERNAL:
                    span.errorOccurred().log(status.getCause());
                    Tags.STATUS_CODE.set(span, status.getCode().name());
                    break;
            }
            ContextManager.continued(contextSnapshot);
            super.close(status, trailers);
        } catch (Throwable t) {
            ContextManager.activeSpan().errorOccurred().log(t);
        } finally {
            ContextManager.stopSpan();
        }
    }
}
