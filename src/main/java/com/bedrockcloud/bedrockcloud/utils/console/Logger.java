package com.bedrockcloud.bedrockcloud.utils.console;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private File cloudLog;

    public Logger() {
        this.cloudLog = new File("./local/cloud.log");
    }

    public void info(final String message) {
        log(LogLevel.INFO, message);
    }

    public void error(final String message) {
        log(LogLevel.ERROR, message);
    }

    public void debug(final String message) {
        log(LogLevel.DEBUG, message);
    }

    public void warning(final String message) {
        log(LogLevel.WARNING, message);
    }

    public void command(final String message) {
        log(LogLevel.COMMAND, message);
    }

    public void exception(final Exception e) {
        if (e != null) {
            log(LogLevel.EXCEPTION, getFullStackTrace(e));
        }
    }

    private static String getFullStackTrace(@NotNull final Throwable t) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            pw.println(t);
            t.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
            return "Error getting full stacktrace: " + e.getMessage();
        }
    }


    private void log(final LogLevel level, final String message) {
        String formattedMessage = String.format("%s[%s] » %s",
                Colors.toColor(Cloud.prefix),
                Colors.toColor(level.getColorJavaCode() + level.getName() + Colors.RESET.getJavaCode()),
                message);
        System.out.println(formattedMessage);

        try (FileWriter cloudLogWriter = new FileWriter(this.cloudLog, true)) {
            File file = new File("./local/config.yml");
            if (!file.exists()) return;
            if (!Utils.getConfig().getBoolean("enable-log")) return;

            String plainMessage = Colors.removeColor(formattedMessage);
            cloudLogWriter.append(plainMessage).append("\n");
            cloudLogWriter.flush();

            int maxSizeBytes = 12 * 1024 * 1024;
            if (this.cloudLog.length() > maxSizeBytes) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String currentDate = dateFormat.format(new Date());
                File logsDirectory = new File("./local/logs");
                if (!logsDirectory.exists()) {
                    logsDirectory.mkdirs();
                }
                File newLogFile = new File(logsDirectory, "cloud-" + currentDate + ".log");
                this.cloudLog.renameTo(newLogFile);
                this.cloudLog = new File("./local/cloud.log");
            }
        } catch (IOException ignored) {}
    }

    public enum LogLevel {
        INFO("INFO", Colors.GREEN),
        ERROR("ERROR", Colors.RED),
        DEBUG("DEBUG", Colors.BLUE1),
        WARNING("WARNING", Colors.YELLOW),
        COMMAND("COMMAND", Colors.CYAN),
        EXCEPTION("EXCEPTION", Colors.RED1);

        private final String name;
        private final Colors color;

        LogLevel(String name, Colors color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public String getColorJavaCode() {
            return color.getJavaCode();
        }
    }
}