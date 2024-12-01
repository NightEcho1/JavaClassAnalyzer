package org.example;

import java.io.IOException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ClassAnalyzer <class-file>");
            return;
        }

        String classFilePath = args[0];
        analyzeClass(classFilePath);
    }

    private static void analyzeClass(String classFilePath) {
        try {
            ClassReader classReader = new ClassReader(classFilePath);
            classReader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    System.out.println("Class: " + name.replace('/', '.'));
                    System.out.println("Super Class: " + superName.replace('/', '.'));
                    if (interfaces != null) {
                        System.out.print("Interfaces: ");
                        for (String iface : interfaces) {
                            System.out.print(iface.replace('/', '.') + " ");
                        }
                        System.out.println();
                    }
                }

                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    System.out.println("Field: " + name + ", Descriptor: " + descriptor);
                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    System.out.println("Method: " + name + ", Descriptor: " + descriptor);
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}