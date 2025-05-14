package com.rkd.auto.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("price_cache")
public class PriceCassandra {

    @PrimaryKey
    @Column("sector")
    private String sector;
    @Column("price")
    private BigDecimal price;
    @Column("updated_at")
    private Instant updatedAt;

    public PriceCassandra() {
    }

    public PriceCassandra(String sector,
                          BigDecimal price,
                          Instant updatedAt) {
        this.sector = sector;
        this.price = price;
        this.updatedAt = updatedAt;
    }

    public String getSector() {
        return sector;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
