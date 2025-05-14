package com.rkd.auto.initializer;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostgresInitializer {

    private final ConnectionFactory cf;

    public PostgresInitializer(ConnectionFactory cf) {
        this.cf = cf;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initSchemaAndData() {

        String[] ddl = {
                "DROP TABLE IF EXISTS revenue",
                "DROP TABLE IF EXISTS sector",
                "DROP TABLE IF EXISTS spot",
                "DROP TABLE IF EXISTS vehicle",

                """
            CREATE TABLE revenue (
                id BIGSERIAL PRIMARY KEY,
                sector VARCHAR NOT NULL,
                date DATE NOT NULL,
                amount DOUBLE PRECISION NOT NULL,
                currency VARCHAR NOT NULL,
                timestamp TIMESTAMPTZ NOT NULL
            )
            """,

                """
            CREATE TABLE sector (
                name VARCHAR PRIMARY KEY,
                base_price DOUBLE PRECISION NOT NULL,
                max_capacity INTEGER NOT NULL,
                open_hour TIME NOT NULL,
                close_hour TIME NOT NULL,
                duration_limit_minutes INTEGER NOT NULL
            )
            """,

                """
            CREATE TABLE spot (
                id BIGSERIAL PRIMARY KEY,
                sector VARCHAR NOT NULL,
                lat DOUBLE PRECISION NOT NULL,
                lng DOUBLE PRECISION NOT NULL,
                occupied BOOLEAN NOT NULL DEFAULT FALSE,
                license_plate VARCHAR
            )
            """,

                """
            CREATE TABLE vehicle (
                id BIGSERIAL PRIMARY KEY,
                license_plate VARCHAR NOT NULL,
                event_type VARCHAR NOT NULL,
                timestamp TIMESTAMPTZ NOT NULL,
                lat DOUBLE PRECISION,
                lng DOUBLE PRECISION
            )
            """
        };

        String[] dml = {

                """
            INSERT INTO sector (name, base_price, max_capacity, open_hour, close_hour, duration_limit_minutes) VALUES
            ('A', 10.0, 100, '08:00:00', '22:00:00', 240),
            ('B',  8.0,  80, '07:00:00', '21:00:00', 180),
            ('C',  6.0,  60, '06:00:00', '20:00:00', 120),
            ('D',  5.0,  50, '09:00:00', '19:00:00',  90),
            ('E',  4.0,  40, '10:00:00', '18:00:00',  60),
            ('F',  3.5,  30, '05:00:00', '17:00:00',  45),
            ('G',  3.0,  20, '06:30:00', '16:30:00',  30)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('A', -23.561000, -46.655000, FALSE),
            ('A', -23.561001, -46.655001, FALSE),
            ('A', -23.561002, -46.655002, FALSE),
            ('A', -23.561003, -46.655003, FALSE),
            ('A', -23.561004, -46.655004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('B', -23.562000, -46.656000, FALSE),
            ('B', -23.562001, -46.656001, FALSE),
            ('B', -23.562002, -46.656002, FALSE),
            ('B', -23.562003, -46.656003, FALSE),
            ('B', -23.562004, -46.656004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('C', -23.563000, -46.657000, FALSE),
            ('C', -23.563001, -46.657001, FALSE),
            ('C', -23.563002, -46.657002, FALSE),
            ('C', -23.563003, -46.657003, FALSE),
            ('C', -23.563004, -46.657004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('D', -23.564000, -46.658000, FALSE),
            ('D', -23.564001, -46.658001, FALSE),
            ('D', -23.564002, -46.658002, FALSE),
            ('D', -23.564003, -46.658003, FALSE),
            ('D', -23.564004, -46.658004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('E', -23.565000, -46.659000, FALSE),
            ('E', -23.565001, -46.659001, FALSE),
            ('E', -23.565002, -46.659002, FALSE),
            ('E', -23.565003, -46.659003, FALSE),
            ('E', -23.565004, -46.659004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('F', -23.566000, -46.660000, FALSE),
            ('F', -23.566001, -46.660001, FALSE),
            ('F', -23.566002, -46.660002, FALSE),
            ('F', -23.566003, -46.660003, FALSE),
            ('F', -23.566004, -46.660004, FALSE)
            """,

                """
            INSERT INTO spot (sector, lat, lng, occupied) VALUES
            ('G', -23.567000, -46.661000, FALSE),
            ('G', -23.567001, -46.661001, FALSE),
            ('G', -23.567002, -46.661002, FALSE),
            ('G', -23.567003, -46.661003, FALSE),
            ('G', -23.567004, -46.661004, FALSE)
            """
        };

        Mono.from(cf.create())
                .flatMapMany(conn ->
                        Flux.concat(
                                        runBatch(conn, ddl),
                                        runBatch(conn, dml))
                                .then(Mono.from(conn.close()))
                )
                .then()
                .doOnError(Throwable::printStackTrace)
                .block();
    }

    private static Flux<Void> runBatch(Connection conn, String[] sql) {
        return Flux.fromArray(sql)
                .concatMap(stmt -> execute(conn, stmt));
    }

    private static Mono<Void> execute(Connection conn, String query) {
        Statement st = conn.createStatement(query);
        return Mono.from(st.execute()).then();
    }
}
