package com.bedrockcloud.bedrockcloud.software;

public enum SoftwareType {
    PROXY(0),
    SERVER(1);

    private final int value;

    SoftwareType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SoftwareType fromValue(int value) {
        for (SoftwareType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public static SoftwareType fromString(String value) {
        for (SoftwareType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}