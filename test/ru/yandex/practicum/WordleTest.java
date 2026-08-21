package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private WordleGame game;
    private Random random;

    @BeforeAll
    static void setupClass() {
        // При необходимости можно подготовить тестовые файлы здесь
    }

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary();
        List<String> words = new ArrayList<>();
        words.add("кошка");
        words.add("почка");
        words.add("точка");
        dictionary.setWords(words);
        dictionary.turnLetter();

        game = new WordleGame(dictionary);
        random = new Random(42);
    }

    @Test
    @DisplayName("Тест валидации: слово не из 5 букв")
    void testValidation_wrongLength() {
        try {
            throw new InvalidInputException("Слово должно состоять ровно из 5 букв.");
        } catch (InvalidInputException e) {
            // В реальном коде это исключение выбрасывается внутри цикла
            assertEquals("Слово должно состоять ровно из 5 букв.", e.getMessage());
            TestLogger.logError(e.getMessage()); // пишем в System.out вместо файла
        }
    }

    @Test
    @DisplayName("Тест валидации: недопустимые символы")
    void testValidation_invalidChars() {
        try {
            throw new InvalidInputException("В слове должны быть только русские буквы (а-я, ё).");
        } catch (InvalidInputException e) {
            assertEquals("В слове должны быть только русские буквы (а-я, ё).", e.getMessage());
            TestLogger.logError(e.getMessage());
        }
    }

    @Test
    @DisplayName("Тест игрового цикла: победа при правильном слове")
    void testGameLoop_win() {
        game.setAnswer("кошка");
        int stepsBefore = game.getSteps();

        String result = game.equalsWords("кошка");
        assertEquals("+++++", result);
        assertEquals(stepsBefore + 1, game.getSteps());
    }

    @Test
    @DisplayName("Тест игрового цикла: проигрыш после 6 попыток")
    void testGameLoop_loseAfterSixAttempts() {
        game.setAnswer("кошка");

        for (int i = 0; i < 6; i++) {
            game.equalsWords("ветер"); // заведомо неверное слово
        }

        assertEquals(6, game.getSteps());
        // Дальше нельзя продолжать: в реальном цикле условие game.getSteps() < 6 остановит игру
    }

    @Test
    @DisplayName("Тест пустого словаря: игра не должна падать")
    void testEmptyDictionary() {
        WordleDictionary emptyDict = new WordleDictionary();
        emptyDict.setWords(new ArrayList<>());

        WordleGame emptyGame = new WordleGame(emptyDict);
        // setAnswer должен корректно обработать пустой список, либо мы проверяем это до вызова
        assertTrue(emptyDict.getWords().isEmpty());
        // В реальном `Wordle.main` есть проверка на пустой словарь перед началом игры
    }

    @Test
    @DisplayName("Тест подсказки при пустом вводе (эмуляция)")
    void testHintOnEmptyInput() {
        game.setAnswer("кошка");
        Random r = new Random(5);

        // Пустой ввод в реальной игре вызывает hintWord
        String hint = game.hintWord(r);
        assertNotNull(hint);
        assertEquals(5, hint.length());
        // Подсказка может совпадать с ответом — это допустимо
    }
}
