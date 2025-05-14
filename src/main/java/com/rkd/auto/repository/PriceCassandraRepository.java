package com.rkd.auto.repository;

import com.rkd.auto.cassandra.PriceCassandra;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface PriceCassandraRepository
        extends ReactiveCassandraRepository<PriceCassandra, String> {
}