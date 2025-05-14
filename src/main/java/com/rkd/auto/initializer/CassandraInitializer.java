package com.rkd.auto.initializer;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CassandraInitializer {

    private final CqlSession session;

    @Autowired
    public CassandraInitializer(CqlSession session) {
        this.session = session;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createPriceCacheTable() {
        session.execute(
                "CREATE TABLE IF NOT EXISTS price_cache (" +
                        "sector text PRIMARY KEY, " +
                        "price decimal, " +
                        "updated_at timestamp" +
                        ");"
        );
    }
}
