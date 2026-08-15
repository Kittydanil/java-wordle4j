package ru.yandex.practicum;

import java.util.Random;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader - ЕСТЬ
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader - ЕСТЬ
    затем создать игру WordleGame и передать ей словарь - ЕСТЬ
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру - ЕСТЬ
    вывести состояние игры и конечный результат - ЕСТЬ
 */
public class Wordle {

    public static WordleDictionaryLoader dictionaryLoader = new WordleDictionaryLoader();
    public static WordleDictionary dictionary = dictionaryLoader.readFile();
    public static WordleGame game = new WordleGame(dictionary);
    public static Scanner scanner = new Scanner(System.in);
    public static Random random = new Random();

    public static void main(String[] args) {
        boolean running = true;

        // Если словарь пустой, не можем выбрать слово — сообщаем и завершаем
        if (dictionary.getWords() == null || dictionary.getWords().isEmpty()) {
            System.out.println("Словарь пуст. Невозможно начать игру.");
            return;
        }

        game.setAnswer(dictionary.getWords().get(random.nextInt(dictionary.getWords().size())));

        try {
            while (running) {
                System.out.println("Введите слово:");
                System.out.print("> ");

                String word;
                try {
                    word = scanner.nextLine();
                } catch (Exception e) {
                    Logger.logException(e);
                    System.out.println("Произошла ошибка при чтении ввода. Попробуйте снова.");
                    continue;
                }

                // Проверка на пустой ввод
                if (word.isBlank()) {
                    // Пустой ввод трактуем как запрос подсказки
                    word = game.hintWord(random);
                    System.out.println("> Подсказка: " + word);
                    if (!word.equals(game.getAnswer())) {
                        System.out.println("> " + game.equalsWords(word));
                    } else {
                        System.out.println("Вы победили!");
                        running = false;
                    }
                    continue;
                }

                if (game.getSteps() < 6) {
                    try {
                        if (word.length() != 5) {
                            throw new InvalidInputException("Слово должно состоять ровно из 5 букв.");
                        }
                        if (!word.matches("[а-яё]+")) {
                            throw new InvalidInputException("В слове должны быть только русские буквы (а-я, ё).");
                        }

                        if (!word.equals(game.getAnswer())) {
                            System.out.println("> " + game.equalsWords(word));
                        } else {
                            System.out.println("Вы победили!");
                            running = false;
                        }
                    } catch (InvalidInputException e) {
                        Logger.logError(e.getMessage());
                        System.out.println("Ошибка ввода: " + e.getMessage());
                    } catch (Exception e) {
                        Logger.logException(e);
                        System.out.println("Произошла непредвиденная ошибка во время проверки слова.");
                    }
                } else {
                    System.out.println("Вы потратили все попытки!");
                    running = false;
                }
            }
        } finally {
            scanner.close();
        }

        System.out.println("Игра окончена!"
                + "\nПотрачено попыток: " + game.getSteps()
                + "\nЗагаданное слово: " + game.getAnswer());
    }
}