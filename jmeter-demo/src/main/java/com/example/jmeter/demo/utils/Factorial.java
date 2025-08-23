package com.example.jmeter.demo.utils;

public class Factorial {
    // Рекурсивная функция для вычисления факториала
    public static long factorial(int n) {
        // Проверка на отрицательные числа
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }
        // Базовый случай
        if (n == 0) {
            return 1;
        }
        // Рекурсивный вызов
        return n * factorial(n - 1);
    }
}
