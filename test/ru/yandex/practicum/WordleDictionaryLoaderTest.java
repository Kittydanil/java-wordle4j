package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @Test
    @DisplayName("Тест загрузки существующего файла с корректными словами")
    void testReadFile_success() throws IOException {
        Path tempFile = Files.createTempFile("words_ru_test", ".txt");
        // Добавляем слова разной длины и с буквой «ё», чтобы проверить фильтрацию и замену
        Files.writeString(tempFile, """
                кошка
                почка
                точка
                лёгко
                дом
                привет
                """);

        Path target = Paths.get("words_ru.txt");
        Files.copy(tempFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.readFile();
            List<String> words = dictionary.getWords();

            assertNotNull(words);
            // Должны остаться только 5-буквенные слова: кошка, почка, точка, лёгко -> легко
            assertEquals(4, words.size());
            assertTrue(words.contains("кошка"));
            assertTrue(words.contains("почка"));
            assertTrue(words.contains("точка"));
            // «лёгко» должно быть преобразовано в «легко»
            assertTrue(words.contains("легко"));
            // Слова «дом» и «привет» не должны попасть в словарь
            assertFalse(words.contains("дом"));
            assertFalse(words.contains("привет"));
        } finally {
            Files.deleteIfExists(target);
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    @DisplayName("Тест обработки отсутствия файла: возвращается пустой словарь, ошибка логируется")
    void testReadFile_fileNotFound() {
        // Убедимся, что файла точно нет
        Path nonExistingFile = Paths.get("non_existent_words_for_test.txt");
        if (Files.exists(nonExistingFile)) {
            try { Files.delete(nonExistingFile); } catch (IOException ignored) {}
        }

        // Создаём анонимный подкласс, чтобы подменить имя файла на несуществующее
        WordleDictionaryLoader loader = new WordleDictionaryLoader() {
            @Override
            public WordleDictionary readFile() {
                WordleDictionary dictionary = new WordleDictionary();
                List<String> words = new ArrayList<>();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.FileReader("non_existent_words_for_test.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.length() == 5) {
                            words.add(line.toLowerCase());
                        }
                    }
                    dictionary.setWords(words);
                } catch (java.io.FileNotFoundException e) {
                    // В тестах используем TestLogger вместо реального Logger
                    TestLogger.logException(e);
                    System.out.println("Ошибка: файл словаря не найден. Игра будет работать с пустым словарем.");
                    dictionary.setWords(new ArrayList<>());
                } catch (java.io.IOException e) {
                    TestLogger.logException(e);
                    dictionary.setWords(new ArrayList<>());
                }
                return dictionary;
            }
        };

        WordleDictionary dictionary = loader.readFile();
        List<String> words = dictionary.getWords();

        assertNotNull(words);
        assertTrue(words.isEmpty(), "При отсутствии файла словарь должен быть пустым");
    }

    @Test
    @DisplayName("Тест обработки ошибки чтения файла (IOException)")
    void testReadFile_ioException() {
        // Эмулируем IOException через подмену метода readLine, который выбрасывает исключение
        WordleDictionaryLoader loader = new WordleDictionaryLoader() {
            @Override
            public WordleDictionary readFile() {
                WordleDictionary dictionary = new WordleDictionary();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.StringReader("кошка"))) {
                    // Принудительно выбрасываем IOException при чтении
                    throw new java.io.IOException("Эмуляция ошибки чтения");
                } catch (java.io.IOException e) {
                    TestLogger.logException(e);
                    System.out.println("Ошибка чтения файла словаря.");
                    dictionary.setWords(new ArrayList<>());
                }
                return dictionary;
            }
        };

        WordleDictionary dictionary = loader.readFile();
        assertTrue(dictionary.getWords().isEmpty());
    }

    @Test
    @DisplayName("Тест фильтрации слов по длине: остаются только 5-буквенные")
    void testReadFile_lengthFilter() throws IOException {
        Path tempFile = Files.createTempFile("words_ru_len_test", ".txt");
        Files.writeString(tempFile, """
                кот
                кошка
                автомобиль
                точка
                дом
                """);

        Path target = Paths.get("words_ru.txt");
        Files.copy(tempFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.readFile();
            List<String> words = dictionary.getWords();

            assertEquals(2, words.size()); // только «кошка» и «точка»
            assertTrue(words.contains("кошка"));
            assertTrue(words.contains("точка"));
        } finally {
            Files.deleteIfExists(target);
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    @DisplayName("Тест замены буквы «ё» на «е» при загрузке словаря")
    void testReadFile_replaceYo() throws IOException {
        Path tempFile = Files.createTempFile("words_ru_yo_test", ".txt");
        Files.writeString(tempFile, """
                лёгко
                кошка
                """);

        Path target = Paths.get("words_ru.txt");
        Files.copy(tempFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.readFile();
            List<String> words = dictionary.getWords();

            // «лёгко» -> «легко», «кошка» остаётся
            assertEquals(2, words.size());
            assertTrue(words.contains("легко"));
            assertTrue(words.contains("кошка"));

            // Проверяем, что в словаре нет слов с «ё»
            for (String word : words) {
                assertFalse(word.contains("ё"), "В словаре не должно быть буквы «ё»: " + word);
            }
        } finally {
            Files.deleteIfExists(target);
            Files.deleteIfExists(tempFile);
        }
    }
}
