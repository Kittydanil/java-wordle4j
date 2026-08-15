package ru.yandex.practicum;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

// Заглушка для тестов: вместо записи в файл пишет в System.out
public class TestLogger {
    private static final PrintWriter OUT = new PrintWriter(System.out);

    public static void logError(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        OUT.println(timestamp + " | ERROR | " + message);
        OUT.flush();
    }

    public static void logException(Throwable t) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        StackTraceElement firstElement = (stackTrace != null && stackTrace.length > 0) ? stackTrace[0] : null;

        StringBuilder sb = new StringBuilder();
        sb.append("Exception class: ").append(t.getClass().getName()).append("\n");
        sb.append("Message: ").append(t.getMessage()).append("\n");

        if (firstElement != null) {
            sb.append("Method: ").append(firstElement.getMethodName())
                    .append("\n")
                    .append("File: ").append(firstElement.getFileName())
                    .append("\n")
                    .append("Line: ").append(firstElement.getLineNumber())
                    .append("\n");
        } else {
            sb.append("Location: unknown\n");
        }

        sb.append("Stack trace:\n")
                .append(java.util.Arrays.stream(stackTrace)
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));

        String fullMessage = sb.toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        OUT.println(timestamp + " | EXCEPTION\n" + fullMessage);
        OUT.flush();
    }
}
