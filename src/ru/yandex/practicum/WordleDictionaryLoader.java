package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла - ещё не добавил кодировку
    на выходе должен быть класс WordleDictionary - ЕСТЬ
 */
public class WordleDictionaryLoader {

    public WordleDictionary readFile() {
        WordleDictionary dictionary = new WordleDictionary();
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("words_ru.txt"))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.length() == 5) {
                    words.add(line);
                }
            }
            dictionary.setWords(words);
            dictionary.turnLetter();
        } catch (FileNotFoundException e) {
            Logger.logException(e);
            System.out.println("Ошибка: файл словаря не найден. Игра будет работать с пустым словарем.");
            dictionary.setWords(new ArrayList<>());
        } catch (IOException e) {
            Logger.logException(e);
            System.out.println("Ошибка чтения файла словаря. Игра будет работать с пустым словарем.");
            dictionary.setWords(new ArrayList<>());
        } catch (Exception e) {
            Logger.logException(e);
            System.out.println("Произошла непредвиденная ошибка при загрузке словаря.");
            dictionary.setWords(new ArrayList<>());
        }
        return dictionary;
    }
}
