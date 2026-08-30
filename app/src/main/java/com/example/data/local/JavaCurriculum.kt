package com.example.data.local

import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

object JavaCurriculum {

  fun getCourse(): CourseEntity = CourseEntity(
    id = "java",
    title = "Java & Object Architecture",
    description = "Master robust, enterprise-grade object-oriented programming with Java. Learn classes, inheritance, polymorphism, and collections.",
    language = "java",
    totalWorlds = 3,
    iconName = "coffee",
    orderIndex = 3,
    isAvailable = true,
    estimatedHours = 24,
    difficulty = "Intermediate"
  )

  fun getWorlds(): List<WorldEntity> = listOf(
    WorldEntity(
      id = "java_w1",
      courseId = "java",
      worldNumber = 1,
      title = "Java Foundations",
      subtitle = "Public static void main, strong types, operators, strings, conditions, and loops",
      themeColorHex = "#EA580C",
      iconName = "terminal",
      requiredXp = 0,
      isUnlocked = true,
      topicsJson = "[\"Java Structure\", \"Data Types\", \"Operators\", \"Strings & .equals()\", \"Conditions\", \"Loops\"]"
    ),
    WorldEntity(
      id = "java_w2",
      courseId = "java",
      worldNumber = 2,
      title = "Methods & Collections",
      subtitle = "Custom static and instance methods, parameters, return values, and fixed arrays",
      themeColorHex = "#C2410C",
      iconName = "widgets",
      requiredXp = 200,
      isUnlocked = true,
      topicsJson = "[\"Method Signatures\", \"Parameters & Returns\", \"Arrays\", \"Array Algorithms\", \"ArrayList Intro\"]"
    ),
    WorldEntity(
      id = "java_w3",
      courseId = "java",
      worldNumber = 3,
      title = "OOP Foundations & Class Master",
      subtitle = "Classes, objects, constructors, encapsulation, getters/setters, and Class Master boss",
      themeColorHex = "#991B1B",
      iconName = "category",
      requiredXp = 450,
      isUnlocked = true,
      topicsJson = "[\"Classes & Objects\", \"Constructors\", \"Encapsulation\", \"Getters/Setters\", \"Inheritance Intro\", \"Polymorphism\", \"Class Master Boss\"]"
    )
  )

  fun getChapters(): List<ChapterEntity> = listOf(
    ChapterEntity("java_w1_c1", "java_w1", 1, "Core Java Syntax", "Structure of a Java program and main method."),
    ChapterEntity("java_w1_c2", "java_w1", 2, "Control Structures", "Branching logic and loops."),
    ChapterEntity("java_w2_c1", "java_w2", 1, "Modular Methods", "Decomposing problems into callable methods."),
    ChapterEntity("java_w2_c2", "java_w2", 2, "Array Collections", "Fixed-size data buffers in Java."),
    ChapterEntity("java_w3_c1", "java_w3", 1, "Object-Oriented Programming", "Building class blueprints and instantiating objects."),
    ChapterEntity("java_w3_c2", "java_w3", 2, "Class Master Quest", "Conquer the Java OOP Boss Challenge.")
  )

  fun getLessons(): List<LessonEntity> = listOf(
    // World 1
    LessonEntity("java_w1_l1", "java_w1_c1", "java_w1", 1, "Java Syntax & main()", "Public class and static void main entry point.", LessonType.LESSON, 40, 15, true, false, 0, "Main Method", "public static void main(String[] args) {}", "Java applications begin execution in the main method.", 4),
    LessonEntity("java_w1_l2", "java_w1_c1", "java_w1", 2, "Primitive Data Types", "int, double, boolean, char, and strong typing.", LessonType.LESSON, 40, 15, true, false, 0, "Java Types", "int count = 10; double price = 19.99;", "Java is statically typed.", 4),
    LessonEntity("java_w1_l3", "java_w1_c1", "java_w1", 3, "Operators & Arithmetic", "Integer division, modulus, and increment operators.", LessonType.LESSON, 40, 15, true, false, 0, "Arithmetic", "int sum = a + b;", "Java math operations.", 4),
    LessonEntity("java_w1_l4", "java_w1_c1", "java_w1", 4, "Strings & String Methods", "Immutable String objects and .equals() comparisons.", LessonType.LESSON, 45, 15, true, false, 0, "String Equality", "str1.equals(str2)", "Never compare Java strings with ==; use .equals().", 4),
    LessonEntity("java_w1_l5", "java_w1_c2", "java_w1", 5, "Conditions: if / else", "Evaluating boolean expressions and branch logic.", LessonType.LESSON, 45, 15, true, false, 0, "Branching", "if (age >= 18) {}", "Conditional branching in Java.", 4),
    LessonEntity("java_w1_l6", "java_w1_c2", "java_w1", 6, "Loops: for & while", "Iteration control in Java.", LessonType.CHALLENGE, 80, 30, true, false, 0, "Loops", "for (int i = 0; i < 5; i++) {}", "Iteration mechanisms in Java.", 5),

    // World 2
    LessonEntity("java_w2_l1", "java_w2_c1", "java_w2", 1, "Defining Methods", "Method signatures, parameters, and return types.", LessonType.LESSON, 50, 20, true, false, 0, "Methods", "public static int add(int a, int b) { return a + b; }", "Modular callable units in Java.", 5),
    LessonEntity("java_w2_l2", "java_w2_c1", "java_w2", 2, "Arrays in Java", "Declaring, allocating, and indexing fixed arrays.", LessonType.LESSON, 50, 20, true, false, 0, "Arrays", "int[] numbers = new int[5];", "Contiguous array storage.", 5),
    LessonEntity("java_w2_l3", "java_w2_c2", "java_w2", 3, "Array Traversal & Algorithms", "Finding maximums, sums, and linear searching.", LessonType.CHALLENGE, 85, 35, true, false, 0, "Traversal", "for (int num : numbers) {}", "Enhanced for loops and iteration.", 5),

    // World 3
    LessonEntity("java_w3_l1", "java_w3_c1", "java_w3", 1, "Classes & Objects", "Declaring classes, fields, and constructors.", LessonType.LESSON, 60, 25, true, false, 0, "OOP Classes", "public class Student { private String name; }", "Class blueprints and encapsulation.", 5),
    LessonEntity("java_w3_l2", "java_w3_c1", "java_w3", 2, "Encapsulation", "private fields and public getters/setters.", LessonType.LESSON, 65, 25, true, false, 0, "Encapsulation", "public String getName() { return name; }", "Information hiding via access modifiers.", 5),
    LessonEntity("java_w3_l3", "java_w3_c2", "java_w3", 3, "CLASS MASTER Boss Battle", "Conquer the Java OOP Class Master Boss Challenge!", LessonType.BOSS, 120, 50, true, false, 0, "Boss Battle", "// Class Master Challenge\nStudent s = new Student(\"Alex\", 100);", "Synthesize classes, constructors, and encapsulation.", 6)
  )

  fun getExercises(): List<ExerciseEntity> = listOf(
    ExerciseEntity(
      id = "java_ex_1",
      lessonId = "java_w1_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "What is the correct signature for the main entry point method in Java?",
      explanation = "Java standard entry point is: public static void main(String[] args).",
      optionsJson = "[\"public static void main(String[] args)\", \"def main(args):\", \"function main()\", \"int main(void)\"]",
      correctAnswersJson = "[\"public static void main(String[] args)\"]",
      hintsJson = "[\"Look for public static void main.\"]",
      topic = "Java Syntax"
    ),
    ExerciseEntity(
      id = "java_ex_2",
      lessonId = "java_w1_l4",
      orderIndex = 1,
      type = ExerciseType.TRUE_FALSE,
      prompt = "In Java, two String objects should be compared for content equality using .equals() instead of ==.",
      explanation = "== compares memory addresses (object references), while .equals() compares the actual string character contents.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"True\"]",
      hintsJson = "[\"Always use .equals() for content comparison.\"]",
      topic = "Strings"
    )
  )
}
