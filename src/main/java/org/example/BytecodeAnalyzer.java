package org.example;

import org.apache.bcel.classfile.ClassParser; // Импортируем класс для парсинга .class файлов
import org.apache.bcel.classfile.JavaClass; // Импортируем класс, представляющий Java класс
import org.apache.bcel.generic.MethodGen; // Импортируем класс для генерации методов
import org.apache.bcel.generic.InstructionList; // Импортируем класс для работы со списком инструкций
import org.apache.bcel.generic.Instruction; // Импортируем класс, представляющий инструкцию

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BytecodeAnalyzer {

    // Путь к директории, содержащей файлы .class для анализа
    private static final String DIRECTORY_PATH = "FilesToAnalyze";

    public static void main(String[] args) {
        // Создаем объект File для указанной директории
        File directory = new File(DIRECTORY_PATH);

        // Проверяем, существует ли директория и является ли она директорией
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Указанная директория не существует или не является директорией.");
            return; // Завершаем выполнение, если директория недоступна
        }

        // Список для хранения информации о байт-коде
        List<String> bytecodeInfo = new ArrayList<>();

        // Получаем все файлы .class в директории
        File[] classFiles = directory.listFiles((dir, name) -> name.endsWith(".class"));
        if (classFiles != null) {
            // Проходим по каждому файлу .class
            for (File classFile : classFiles) {
                try {
                    // Добавляем информацию о текущем файле в список
                    bytecodeInfo.add("Анализ файла: " + classFile.getName());
                    // Анализируем класс и добавляем информацию в список
                    analyzeClass(classFile.getPath(), bytecodeInfo);
                } catch (IOException e) {
                    // Обрабатываем исключения, возникающие при чтении файла
                    System.err.println("Ошибка при чтении файла: " + classFile.getName());
                    e.printStackTrace(); // Выводим стек вызовов для отладки
                }
            }
        }

        // Выводим информацию о байт-коде
        for (String info : bytecodeInfo) {
            System.out.println(info);
        }
    }

    // Метод для анализа класса
    private static void analyzeClass(String classFilePath, List<String> bytecodeInfo) throws IOException {
        // Парсим класс из файла .class
        JavaClass javaClass = new ClassParser(classFilePath).parse();

        // Проходим по всем методам класса
        for (org.apache.bcel.classfile.Method method : javaClass.getMethods()) {
            // Создаем объект MethodGen для текущего метода
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), null);
            // Добавляем информацию о методе в список
            bytecodeInfo.add("Метод: " + methodGen.getName() + ", Дескриптор: " + methodGen.getSignature());

            // Получаем список инструкций для метода
            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                // Проходим по всем инструкциям в методе
                for (Instruction instruction : instructionList.getInstructions()) {
                    // Добавляем информацию о каждой инструкции в список
                    bytecodeInfo.add("    Инструкция: " + instruction.toString());
                }
            }
        }
    }
}