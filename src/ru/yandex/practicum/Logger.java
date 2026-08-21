package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final Path LOG_PATH = Paths.get("wordle_errors.log");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void logException(Throwable t) {
        // Получаем полный вывод стека
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);  // Это даёт полный stack trace + всю цепочку причин
        }
        String fullStack = sw.toString();
        String logEntry = formatLogEntry(fullStack);
        writeToLog(logEntry);
    }

    private static String formatLogEntry(String message) {
        String timestamp = LocalDateTime.now()
                .format(FORMATTER);

        return timestamp + " | " + "ERROR" + "\n" + message + "\n";
    }

    private static void writeToLog(String content) {
        try {
            Files.writeString(LOG_PATH, content + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Если не можем записать в файл — хотя бы выводим в консоль, чтобы не потерять ошибку
            System.err.println("Не удалось записать в лог-файл: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

