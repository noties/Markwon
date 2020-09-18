package io.noties.markwon.utils;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// utility class to print parsed Nodes hierarchy
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class DumpNodes {

    /**
     * Creates String representation of a node which will be used in output
     */
    public interface NodeProcessor {
        @NonNull
        String process(@NonNull Node node);
    }

    @NonNull
    @CheckResult
    public static String dump(@NonNull Node node) {
        return dump(node, null);
    }

    @NonNull
    @CheckResult
    public static String dump(@NonNull Node node, @Nullable NodeProcessor nodeProcessor) {

        final NodeProcessor processor = nodeProcessor != null
                ? nodeProcessor
                : new NodeProcessorToString();

        final Indent indent = new Indent();
        final StringBuilder builder = new StringBuilder();
        final Visitor visitor = (Visitor) Proxy.newProxyInstance(
                Visitor.class.getClassLoader(),
                new Class[]{Visitor.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {

                        final Node argument = (Node) args[0];

                        // initial indent
                        indent.appendTo(builder);

                        // node info
                        builder.append(processor.process(argument));

                        // @since 4.6.0 check for first child instead of casting to Block
                        //  (regular nodes can contain other nodes, for example Text)
                        if (argument.getFirstChild() != null) {
                            builder.append(" [\n");
                            indent.increment();
                            visitChildren((Visitor) proxy, argument);
                            indent.decrement();
                            indent.appendTo(builder);
                            builder.append("]\n");
                        } else {
                            builder.append("\n");
                        }

                        return null;
                    }
                });
        node.accept(visitor);
        return builder.toString();
    }

    private DumpNodes() {
    }

    private static class Indent {

        private int count;

        void increment() {
            count += 1;
        }

        void decrement() {
            count -= 1;
        }

        void appendTo(@NonNull StringBuilder builder) {
            for (int i = 0; i < count; i++) {
                builder
                        .append(' ')
                        .append(' ');
            }
        }
    }

    private static void visitChildren(@NonNull Visitor visitor, @NonNull Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
            // node after visiting it. So get the next node before visiting.
            Node next = node.getNext();
            node.accept(visitor);
            node = next;
        }
    }

    private static class NodeProcessorToString implements NodeProcessor {
        @NonNull
        @Override
        public String process(@NonNull Node node) {
            return node.toString();
        }
    }
}
