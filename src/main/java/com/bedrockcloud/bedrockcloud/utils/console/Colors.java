package com.bedrockcloud.bedrockcloud.utils.console;

public enum Colors {
    WHITE("white", 'f', "30m"),
    RED("red", 'c', "31m"),
    GREEN("green", 'a', "32m"),
    YELLOW("yellow", 'e', "33m"),
    MAGENTA("magenta", 'd', "35m"),
    CYAN("cyan", 'b', "36m"),
    GRAY("gray", '7', "37m"),
    DARK_GRAY("dark_gray", '8', "37m"),
    DARK_BLUE("dark_blue", '9', "34m"),
    LIGHT_BLUE("light_blue", '1', "34m"),
    LIGHT_GREEN("light_green", '2', "32m"),
    LIGHT_CYAN("light_cyan", '3', "36m"),
    LIGHT_RED("light_red", '4', "31m"),
    LIGHT_MAGENTA("light_magenta", '5', "35m"),
    LIGHT_YELLOW("light_yellow", '6', "33m"),
    BOLD("bold", 'l', "1m"),
    RESET_BOLD("reset_bold", 'r', "21m"),
    UNDERLINED("underlined", '_', "4m"),
    RESET("reset", 'r', "0m");

    private final String name;
    private final String javaCode;
    private final char index;

    Colors(final String name, final char index, final String javaCode) {
        this.name = name;
        this.index = index;
        this.javaCode = "\u001b[" + javaCode;
    }

    public static String toColor(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        for (final Colors color : values()) {
            text = text.replace("§" + color.index, color.javaCode);
        }
        return text;
    }

    public static String removeColor(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        for (final Colors color : values()) {
            text = text.replace("§" + color.index, "");
        }
        return text;
    }

    @Override
    public String toString() {
        return "Colors{name='" + this.name + '\'' + ", javaCode='" + this.javaCode + '\'' + ", index=" + this.index + '}';
    }

    public String getName() {
        return this.name;
    }

    public String getJavaCode() {
        return this.javaCode;
    }

    public char getIndex() {
        return this.index;
    }
}