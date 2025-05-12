package com.rkd.auto.definition;

public final class MessageDefinition {

    private MessageDefinition() {
    }

    public static final class Plate {
        public static final String LICENSE_PLATE_CANNOT_BLANK = "The 'license_plate' field cannot be blank";
    }

    public static final class Revenue {
        public static final String DATE_CANNOT_NULL = "The 'date' field cannot be null";
        public static final String SECTOR_CANNOT_BLANK = "The 'sector' field cannot be blank";
    }

    public static final class Spot {
        public static final String LAT_CANNOT_NULL = "The 'lat' field cannot be null";
        public static final String LNG_CANNOT_NULL = "The 'lng' field cannot be null";
    }

    public static final class Vehicle {
        public static final String LICENSE_PLATE_CANNOT_BLANK = "The 'license_plate' field cannot be blank";
        public static final String EVENT_TYPE_CANNOT_BLANK = "The 'event_type' field cannot be blank";
        public static final String ENTRY_TIME_CANNOT_BE_IN_THE_PAST = "The 'entry_time' field cannot be in the past";
        public static final String EXIT_TIME_CANNOT_BE_IN_THE_PAST = "The 'exit_time' field cannot be in the past";
        public static final String LAT_CANNOT_NULL = "The 'lat' field cannot be null";
        public static final String LNG_CANNOT_NULL = "The 'lng' field cannot be null";
    }
}