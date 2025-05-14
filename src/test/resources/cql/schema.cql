
CREATE KEYSPACE IF NOT EXISTS auto_park_system
  WITH replication = {'class':'SimpleStrategy','replication_factor':1};

USE auto_park_system;

CREATE TABLE IF NOT EXISTS price_cache (
  sector     text PRIMARY KEY,
  price      decimal,
  updated_at timestamp
);
