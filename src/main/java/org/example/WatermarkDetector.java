package org.example;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import java.io.IOException;
import java.util.*;

public class WatermarkDetector {

    public List<String> detectWatermarkPossibilities(String classFilePath) throws IOException {
        List<String> results = new ArrayList<>();
        JavaClass javaClass = new ClassParser(classFilePath).parse();

        for (Method method : javaClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), null);
            results.add("Метод: " + methodGen.getName() + ", Дескриптор: " + methodGen.getSignature());

            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                int ifInstructionCount = 0;
                int constantLoadInstructionCount = 0;
                int arrayInstructionCount = 0;
                int methodCallInstructionCount = 0;

                for (InstructionHandle handle : instructionList.getInstructionHandles()) {
                    Instruction instruction = handle.getInstruction();
                    results.add("    Инструкция: " + instruction.toString());
                    if (canEmbedWatermark(instruction)) {
                        results.add("        Возможность вложения водяного знака: Да");
                        results.add("        Способ внедрения: Заменить на эквивалентную инструкцию с другим опкодом.");
                    } else {
                        results.add("        Возможность вложения водяного знака: Нет");
                    }

                    if (isIfInstruction(instruction)) {
                        ifInstructionCount++;
                        results.add("        Инструкция условного перехода: " + instruction.toString());
                    }

                    if (isConstantLoadInstruction(instruction)) {
                        constantLoadInstructionCount++;
                        results.add("        Инструкция загрузки константы: " + instruction.toString());
                    }

                    if (isArrayInstruction(instruction)) {
                        arrayInstructionCount++;
                        results.add("        Инструкция работы с массивом: " + instruction.toString());
                    }

                    if (isMethodCallInstruction(instruction)) {
                        methodCallInstructionCount++;
                        results.add("        Инструкция вызова метода: " + instruction.toString());
                    }
                }

                // Проверка количества инструкций для возможности внедрения ЦВЗ
                if (ifInstructionCount > 0) {
                    results.add("        Количество инструкций условных переходов: " + ifInstructionCount);
                    results.add("        Возможность вложения водяного знака через инструкции условных переходов: Да");
                    results.add("        Способ внедрения: Заменить условные операторы на эквивалентные с другой логикой.");
                } else {
                    results.add("        Возможность вложения водяного знака через инструкции условных переходов: Нет");
                }

                if (constantLoadInstructionCount > 0) {
                    results.add("        Количество инструкций загрузки констант: " + constantLoadInstructionCount);
                    results.add("        Возможность вложения водяного знака через инструкции загрузки констант: Да");
                    results.add("        Способ внедрения: Заменить значения констант на другие эквивалентные значения.");
                } else {
                    results.add("        Возможность вложения водяного знака через инструкции загрузки констант: Нет");
                }

                if (arrayInstructionCount > 0) {
                    results.add("        Количество инструкций работы с массивами: " + arrayInstructionCount);
                    results.add("        Возможность вложения водяного знака через инструкции работы с массивами: Да");
                    results.add("        Способ внедрения: Изменить порядок элементов массива или использовать другие индексы.");
                } else {
                    results.add("        Возможность вложения водяного знака через инструкции работы с массивами: Нет");
                }

                if (methodCallInstructionCount > 0) {
                    results.add("        Количество инструкций вызова методов: " + methodCallInstructionCount);
                    results.add("        Возможность вложения водяного знака через инструкции вызова методов: Да");
                    results.add("        Способ внедрения: Заменить вызовы методов на эквивалентные вызовы с другими параметрами.");
                } else {
                    results.add("        Возможность вложения водяного знака через инструкции вызова методов: Нет");
                }
            }
        }

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

    private boolean isIfInstruction(Instruction instruction) {
        // Проверка, является ли инструкция инструкцией условного перехода
        return instruction instanceof IFNULL ||
                instruction instanceof IFNONNULL ||
                instruction instanceof IFEQ ||
                instruction instanceof IFNE ||
                instruction instanceof IFLT ||
                instruction instanceof IFGE ||
                instruction instanceof IFGT ||
                instruction instanceof IFLE ||
                instruction instanceof IF_ICMPEQ ||
                instruction instanceof IF_ICMPNE ||
                instruction instanceof IF_ICMPLT ||
                instruction instanceof IF_ICMPGE ||
                instruction instanceof IF_ICMPGT ||
                instruction instanceof IF_ICMPLE ||
                instruction instanceof IF_ACMPEQ ||
                instruction instanceof IF_ACMPNE;
    }

    private boolean isConstantLoadInstruction(Instruction instruction) {
        // Проверка, является ли инструкция инструкцией загрузки константы
        return instruction instanceof LDC ||
                instruction instanceof BIPUSH ||
                instruction instanceof SIPUSH;
    }

    private boolean isArrayInstruction(Instruction instruction) {
        // Проверка, является ли инструкция инструкцией работы с массивами
        return instruction instanceof ALOAD ||
                instruction instanceof ASTORE ||
                instruction instanceof IALOAD ||
                instruction instanceof IASTORE;
    }

    private boolean isMethodCallInstruction(Instruction instruction) {
        // Проверка, является ли инструкция инструкцией вызова метода
        return instruction instanceof INVOKEVIRTUAL ||
                instruction instanceof INVOKESTATIC ||
                instruction instanceof INVOKESPECIAL ||
                instruction instanceof INVOKEINTERFACE;
    }
}