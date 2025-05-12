package com.rkd.auto.definition;

public final class ApiDefinition {

    private ApiDefinition() {
    }

    public static final class Plate {
        public static final String POST_PLATE_STATUS = "/plate-status";
    }

    public static final class Revenue {
        public static final String GET_REVENUE = "/revenue";
    }

    public static final class Spot {
        public static final String POST_SPOT_STATUS = "/spot-status";
    }

    public static final class Vehicle {
        public static final String POST_WEBHOOK = "/webhook";
    }
}
