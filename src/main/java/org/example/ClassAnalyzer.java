package org.example;

import org.example.MyClassVisitor;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClassAnalyzer {
    public void analyzeClassFile(String classFilePath) {
        try (FileInputStream fis = new FileInputStream(classFilePath)) {
            ClassReader classReader = new ClassReader(fis);
            MyClassVisitor classVisitor = new MyClassVisitor();
            classReader.accept(classVisitor, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String directoryPath = "C:/Users/NaughtyDragon/IdeaProjects/JavaClassAnalyzer/FilesToAnalyze";

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Указанный путь не является папкой.");
            return;
        }

        // Получаем все .class файлы в указанной папке
        File[] classFiles = directory.listFiles((dir, name) -> name.endsWith(".class"));
        if (classFiles != null) {
            for (File classFile : classFiles) {
                System.out.println("Анализируем файл: " + classFile.getName());
                ClassAnalyzer analyzer = new ClassAnalyzer();
                analyzer.analyzeClassFile(classFile.getAbsolutePath());
            }
        } else {
            System.out.println("В папке нет .class файлов.");
        }
    }
}