package org.example;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.SIPUSH;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEINTERFACE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WatermarkDetector {

    public List<String> detectWatermarkPossibilities(String classFilePath) throws IOException {
        List<String> results = new ArrayList<>();
        JavaClass javaClass = new ClassParser(classFilePath).parse();

        for (Method method : javaClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), null);
            results.add("Метод: " + methodGen.getName() + ", Дескриптор: " + methodGen.getSignature());

            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                for (Instruction instruction : instructionList.getInstructions()) {
                    results.add("    Инструкция: " + instruction.toString());
                    if (canEmbedWatermark(instruction)) {
                        results.add("        Возможность вложения водяного знака: Да");
                    } else {
                        results.add("        Возможность вложения водяного знака: Нет");
                    }
                }
            }

            if (isDummyMethod(methodGen)) {
                results.add("        Фиктивный метод: Да");
            } else {
                results.add("        Фиктивный метод: Нет");
            }
        }

        analyzeCallGraph(javaClass, results);

        return results;
    }

    private boolean canEmbedWatermark(Instruction instruction) {
        // Пример логики для обнаружения возможности вложения водяного знака
        if (instruction instanceof NOP) {
            return true;
        }

        if (instruction instanceof ICONST) {
            return true;
        }

        if (instruction instanceof BIPUSH) {
            return true;
        }

        if (instruction instanceof SIPUSH) {
            return true;
        }

        if (instruction instanceof LDC) {
            return true;
        }

        if (instruction instanceof LDC2_W) {
            return true;
        }

        return false;
    }

    private boolean isDummyMethod(MethodGen methodGen) {
        // Проверка, является ли метод фиктивным
        if (methodGen.getInstructionList().getLength() == 0) {
            return true;
        }

        // Дополнительные условия для фиктивных методов
        // Например, метод может содержать только NOP инструкции
        InstructionList instructionList = methodGen.getInstructionList();
        for (Instruction instruction : instructionList.getInstructions()) {
            if (!(instruction instanceof NOP)) {
                return false;
            }
        }

        return true;
    }

    private void analyzeCallGraph(JavaClass javaClass, List<String> results) {
        // Анализ графа вызовов
        for (Method method : javaClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), null);
            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                for (Instruction instruction : instructionList.getInstructions()) {
                    if (instruction instanceof INVOKEVIRTUAL ||
                            instruction instanceof INVOKESTATIC ||
                            instruction instanceof INVOKESPECIAL ||
                            instruction instanceof INVOKEINTERFACE) {
                        results.add("        Вызов метода: " + instruction.toString());
                        // Здесь можно добавить логику для анализа глубины графа вызовов
                        // Например, подсчет количества вызовов и определение точек вставки фиктивных методов
                    }
                }
            }
        }
    }
}