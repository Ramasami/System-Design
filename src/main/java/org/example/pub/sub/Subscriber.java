package org.example.pub.sub;

import java.util.function.Consumer;

public interface Subscriber {
    Consumer<Message> consume();
}
