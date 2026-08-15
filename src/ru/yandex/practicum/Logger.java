package ru.yandex.practicum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Logger {
    private static final Path LOG_PATH = Paths.get("wordle_errors.log");

    //Логирует только сообщение (без стека)

    public static void logError(String message) {
        String logEntry = formatLogEntry("ERROR", message, null);
        writeToLog(logEntry);
    }

    //Логирует исключение с полным стеком, именем метода и номером строки

    public static void logException(Throwable t) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        // Берём первый элемент стека (место, где реально произошло исключение)
        StackTraceElement firstElement = (stackTrace != null && stackTrace.length > 0)
                ? stackTrace[0]
                : null;

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

        // Добавляем полный стек-трейс
        sb.append("Stack trace:\n")
                .append(java.util.Arrays.stream(stackTrace)
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));

        String fullMessage = sb.toString();
        String logEntry = formatLogEntry("EXCEPTION", fullMessage, t);
        writeToLog(logEntry);
    }

    private static String formatLogEntry(String level, String message, Throwable t) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return timestamp + " | " + level + "\n" + message + "\n";
    }

    private static void writeToLog(String content) {
        try {
            Files.writeString(LOG_PATH, content + System.lineSeparator(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Если не можем записать в файл — хотя бы выводим в консоль, чтобы не потерять ошибку
            System.err.println("Не удалось записать в лог-файл: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

