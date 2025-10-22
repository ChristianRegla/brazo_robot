package com.example.robot.model

enum class UnitType(val displayName: String, val conversionFactor: Float) {
    GRAMS("Gramos", 1f),
    KILOGRAMS("Kilogramos", 0.001f),
    POUNDS("Libras", 0.00220462f);
}