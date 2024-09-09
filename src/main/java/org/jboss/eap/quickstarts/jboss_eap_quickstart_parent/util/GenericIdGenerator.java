package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class GenericIdGenerator {

    private final AtomicLong counter = new AtomicLong();

    public long getNextId() {
        return counter.incrementAndGet();
    }
}
