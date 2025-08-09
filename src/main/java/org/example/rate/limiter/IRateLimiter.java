package org.example.rate.limiter;

import java.util.function.Supplier;

public interface IRateLimiter {

    void process(Supplier<String> runnable);
}
