package org.example;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Instruction;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BytecodeAnalyzer {
    private static final String DIRECTORY_PATH = "FilesToAnalyze";

    public static void main(String[] args) {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Указанная директория не существует или не является директорией.");
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<String> bytecodeInfo = new ArrayList<>();

        File[] classFiles = directory.listFiles((dir, name) -> name.endsWith(".class"));
        if (classFiles != null) {
            for (File classFile : classFiles) {
                executorService.submit(() -> {
                    try {
                        List<String> fileInfo = new ArrayList<>();
                        fileInfo.add("Анализ файла: " + classFile.getName());
                        analyzeClass(classFile.getPath(), fileInfo);

                        // Используем WatermarkDetector для обнаружения возможностей вложения ЦВЗ
                        WatermarkDetector detector = new WatermarkDetector();
                        List<String> watermarkResults = detector.detectWatermarkPossibilities(classFile.getPath());
                        fileInfo.addAll(watermarkResults);

                        synchronized (bytecodeInfo) {
                            bytecodeInfo.addAll(fileInfo);
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка при чтении файла: " + classFile.getName());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Неожиданная ошибка: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (String info : bytecodeInfo) {
            System.out.println(info);
        }
    }

    private static void analyzeClass(String classFilePath, List<String> bytecodeInfo) throws IOException {
        JavaClass javaClass = new ClassParser(classFilePath).parse();
        for (org.apache.bcel.classfile.Method method : javaClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), null);
            bytecodeInfo.add("Метод: " + methodGen.getName() + ", Дескриптор: " + methodGen.getSignature());

            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                for (Instruction instruction : instructionList.getInstructions()) {
                    bytecodeInfo.add("    Инструкция: " + instruction.toString());
                }
            }
        }
    }
}