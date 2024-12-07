package org.example;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class MyClassVisitor extends ClassVisitor {
    private List<String> fieldsInfo;
    private List<String> methodsInfo;
    private int fieldCount;
    private int methodCount;

    public MyClassVisitor() {
        super(Opcodes.ASM9);
        fieldsInfo = new ArrayList<>();
        methodsInfo = new ArrayList<>();
        fieldCount = 0;
        methodCount = 0;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        StringBuilder fieldInfo = new StringBuilder();
        fieldInfo.append("Field: ").append(name)
                .append(", Descriptor: ").append(descriptor)
                .append(", Access: ").append(getAccessModifiers(access));
        fieldsInfo.add(fieldInfo.toString());
        fieldCount++;
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        StringBuilder methodInfo = new StringBuilder();
        methodInfo.append("Method: ").append(name)
                .append(", Descriptor: ").append(descriptor)
                .append(", Access: ").append(getAccessModifiers(access));
        methodsInfo.add(methodInfo.toString());
        methodCount++;
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        System.out.println("Структура класса:");
        System.out.println("u4             зарезервировано;");
        System.out.println("u2             младшая_часть_номера_версии;");
        System.out.println("u2             старшая_часть_номера_версии;");
        System.out.println("u2             количество_константных_пулов;");
        System.out.println("cp_info        константный_пул[количество_константных_пулов-1];");
        System.out.println("u2             флаги_доступа;");
        System.out.println("u2             текущий_класс;");
        System.out.println("u2             предок;");
        System.out.println("u2             количество_интерфейсов;");
        System.out.println("u2             интерфейсы[количество_интерфейсов];");
        System.out.println("u2             количество_полей: " + fieldCount);
        System.out.println("field_info     поля[количество_полей]:");
        for (String field : fieldsInfo) {
            System.out.println("                " + field);
        }
        System.out.println("u2             количество_методов: " + methodCount);
        System.out.println("method_info    методы[количество_методов]:");
        for (String method : methodsInfo) {
            System.out.println("                " + method);
        }
        System.out.println("u2             количество_атрибутов;");
        System.out.println("attribute_info атрибут[количество_атрибутов];");
    }

    private String getAccessModifiers(int access) {
        StringBuilder modifiers = new StringBuilder();
        if ((access & Opcodes.ACC_PUBLIC) != 0) modifiers.append("public ");
        if ((access & Opcodes.ACC_PRIVATE) != 0) modifiers.append("private ");
        if ((access & Opcodes.ACC_PROTECTED) != 0) modifiers.append("protected ");
        if ((access & Opcodes.ACC_STATIC) != 0) modifiers.append("static ");
        if ((access & Opcodes.ACC_FINAL) != 0) modifiers.append("final ");
        if ((access & Opcodes.ACC_VOLATILE) != 0) modifiers.append("volatile ");
        if ((access & Opcodes.ACC_TRANSIENT) != 0) modifiers.append("transient ");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) modifiers.append("synthetic ");
        return modifiers.toString().trim();
    }
}