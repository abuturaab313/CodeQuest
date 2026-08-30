package com.example.data.local

import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

object CCurriculum {

  fun getCourse(): CourseEntity = CourseEntity(
    id = "c",
    title = "C Systems & Memory",
    description = "Low-level memory architecture, pointer arithmetic, structs, buffer management, and standard I/O in C.",
    language = "c",
    totalWorlds = 2,
    iconName = "memory",
    orderIndex = 4,
    isAvailable = true,
    estimatedHours = 18,
    difficulty = "Advanced"
  )

  fun getWorlds(): List<WorldEntity> = listOf(
    WorldEntity(
      id = "c_w1",
      courseId = "c",
      worldNumber = 1,
      title = "C Foundations",
      subtitle = "Structure of a C program, main(), printf, scanf, data types, conditions, loops, and functions",
      themeColorHex = "#2563EB",
      iconName = "developer_board",
      requiredXp = 0,
      isUnlocked = true,
      topicsJson = "[\"main() Entry Point\", \"printf / scanf\", \"Data Types (int, float, char)\", \"Operators & Casting\", \"Control Flow\", \"Functions\", \"Arrays\"]"
    ),
    WorldEntity(
      id = "c_w2",
      courseId = "c",
      worldNumber = 2,
      title = "Pointers & Memory Sentinel",
      subtitle = "Pointers, memory addresses, arrays as pointers, structs, and Memory Sentinel boss",
      themeColorHex = "#1D4ED8",
      iconName = "security",
      requiredXp = 250,
      isUnlocked = true,
      topicsJson = "[\"Pointers\", \"Address-of & Dereference\", \"Arrays & Pointers\", \"Structs\", \"Memory Sentinel Boss\"]"
    )
  )

  fun getChapters(): List<ChapterEntity> = listOf(
    ChapterEntity("c_w1_c1", "c_w1", 1, "C Fundamentals", "Structure of a C program and basic I/O."),
    ChapterEntity("c_w1_c2", "c_w1", 2, "Control Flow & Functions", "Branching, loops, and modular functions in C."),
    ChapterEntity("c_w2_c1", "c_w2", 1, "Pointers & Memory Layout", "Direct memory access with pointer variables."),
    ChapterEntity("c_w2_c2", "c_w2", 2, "Memory Sentinel Boss", "Conquer the C Memory Sentinel Boss Challenge.")
  )

  fun getLessons(): List<LessonEntity> = listOf(
    LessonEntity("c_w1_l1", "c_w1_c1", "c_w1", 1, "The main() Function & printf", "Entry point of C programs and standard output.", LessonType.LESSON, 40, 15, true, false, 0, "printf format specifiers", "printf(\"Hello %s\\n\", name);", "Format specifiers in C control data output formatting.", 4),
    LessonEntity("c_w1_l2", "c_w1_c1", "c_w1", 2, "Data Types & Format Specifiers", "int (%d), float (%f), char (%c), and sizeof().", LessonType.LESSON, 45, 15, true, false, 0, "Types in C", "int x = 10; float f = 3.14f;", "C variables are statically typed and fixed in memory.", 4),
    LessonEntity("c_w1_l3", "c_w1_c2", "c_w1", 3, "Control Flow: if & loops", "Conditional logic, while loops, and for loops.", LessonType.LESSON, 50, 20, true, false, 0, "C Loops", "for (int i = 0; i < 5; i++) {}", "Iteration structure in C.", 4),
    LessonEntity("c_w1_l4", "c_w1_c2", "c_w1", 4, "Functions in C", "Prototypes, parameters passed by value, and return types.", LessonType.CHALLENGE, 80, 30, true, false, 0, "C Functions", "int add(int a, int b) { return a + b; }", "Declaring and defining functions in C.", 5),

    LessonEntity("c_w2_l1", "c_w2_c1", "c_w2", 1, "Pointers & Addresses", "Understanding & and * operators in memory.", LessonType.LESSON, 60, 25, true, false, 0, "Pointers in C", "int *ptr = &x;", "Pointer variables store hexadecimal memory addresses.", 5),
    LessonEntity("c_w2_l2", "c_w2_c1", "c_w2", 2, "Arrays & Pointer Arithmetic", "Continuous memory buffers and offsets.", LessonType.LESSON, 65, 25, true, false, 0, "Pointer Arithmetic", "*(arr + i) == arr[i]", "Array variables decay to pointers to their first elements.", 5),
    LessonEntity("c_w2_l3", "c_w2_c2", "c_w2", 3, "MEMORY SENTINEL Boss Battle", "Conquer the C Memory Sentinel Boss Challenge!", LessonType.BOSS, 120, 50, true, false, 0, "Boss Combat", "// Sentinel validation\nif (*ptr == target) unlock();", "Synthesize pointers and structures to conquer the Sentinel.", 6)
  )

  fun getExercises(): List<ExerciseEntity> = listOf(
    ExerciseEntity(
      id = "c_ex_1",
      lessonId = "c_w1_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which format specifier in C printf is used to print an integer?",
      explanation = "%d is the format specifier for decimal integers in C.",
      optionsJson = "[\"%d\", \"%s\", \"%f\", \"%c\"]",
      correctAnswersJson = "[\"%d\"]",
      hintsJson = "[\"Look for %d (decimal integer).\"]",
      topic = "Format Specifiers"
    ),
    ExerciseEntity(
      id = "c_ex_2",
      lessonId = "c_w2_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which operator is used to obtain the memory address of a variable in C?",
      explanation = "The address-of operator '&' returns the memory address of its operand.",
      optionsJson = "[\"&\", \"*\", \"->\", \".\"]",
      correctAnswersJson = "[\"&\"]",
      hintsJson = "[\"The ampersand '&' is the address-of operator.\"]",
      topic = "Pointers"
    )
  )
}
