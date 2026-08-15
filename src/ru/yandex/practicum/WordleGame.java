package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг - ЕСТЬ
    всё что пользователь вводил - ЕСТЬ
    правильный ответ - ЕСТЬ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом - ЕСТЬ
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее - НАКОНЕЦ-ТО СДЕЛАЛ!!!

не забудьте про специальные типы исключений для игровых и неигровых ошибок - ещё не сделал!!!
 */
public class WordleGame {

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private Map<Integer, Character> positiveLetters;

    private List<Character> negativeLetters;

    private Map<Integer, Character> neutralLetters;

    public WordleGame(WordleDictionary dictionary) {
        this.dictionary = dictionary;
        steps = 0;
        positiveLetters = new HashMap<>();
        negativeLetters = new ArrayList<>();
        neutralLetters = new HashMap<>();
    }

    public String equalsWords(String input) {
        String word = turnLetter(input.toLowerCase());
        StringBuilder hint = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == answer.charAt(i)) {
                hint.append("+");
                if (!positiveLetters.containsKey(i)) {
                    positiveLetters.put(i, word.charAt(i));
                }
            } else if ((word.charAt(i) != answer.charAt(i)
                    && !answer.contains(String.valueOf(word.charAt(i))))) {
                hint.append("-");
                if (!negativeLetters.contains(word.charAt(i))) {
                    negativeLetters.add(word.charAt(i));
                }
            } else if ((word.charAt(i) != answer.charAt(i)
                    && answer.contains(String.valueOf(word.charAt(i))))) {
                hint.append("^");
                if (!neutralLetters.containsKey(i)) {
                    neutralLetters.put(i, word.charAt(i));
                }
            }
        }
        steps++;
        return hint.toString();
    }

    public String hintWord(Random random) {
        List<String> words = dictionary.getWords();
        List<String> toRemove = new ArrayList<>();

        if (steps == 1) {
            return dictionary.getWords().get(random.nextInt(dictionary.getWords().size()));
        } else {
            if (!negativeLetters.isEmpty()) {
                for (String word : dictionary.getWords()) {
                    for (Character negLetter : negativeLetters) {
                        if (word.contains(String.valueOf(negLetter))) {
                            if (!toRemove.contains(word)) {
                                toRemove.add(word);
                            }
                        }
                    }
                }
                words.removeAll(toRemove);
            }
            toRemove.clear();
            for (String word : words) {
                for (int i = 0; i < 5; i++) {
                    if (positiveLetters.get(i) != null) {
                        if (word.charAt(i) != positiveLetters.get(i)) {
                            toRemove.add(word);
                        }
                    }
                    if (neutralLetters.get(i) != null) {
                        if (word.charAt(i) == neutralLetters.get(i)) {
                            toRemove.add(word);
                        }
                    }
                }
            }
            words.removeAll(toRemove);
        }
        return words.get(random.nextInt(words.size()));
    } // нужно переделать логику подбора подходящих слов!!!

    private String turnLetter(String word) {
        String newWord;

        if (word.contains("ё")) {
            int index = word.indexOf("ё");
            newWord = new StringBuilder(word).replace(index, index + 1, "е").toString();
        } else {
            newWord = word;
        }
        return newWord;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Character> getNegativeLetters() {
        return negativeLetters;
    }

    public Map<Integer, Character> getPositiveLetters() {
        return positiveLetters;
    }

    public Map<Integer, Character> getNeutralLetters() {
        return neutralLetters;
    }
}