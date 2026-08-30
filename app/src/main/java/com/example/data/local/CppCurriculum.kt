package com.example.data.local

import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

object CppCurriculum {

  fun getCourse(): CourseEntity = CourseEntity(
    id = "cpp",
    title = "C++ Object Architect",
    description = "Modern C++, stream I/O, references, classes, encapsulation, STL collections, and game engine mechanics.",
    language = "cpp",
    totalWorlds = 2,
    iconName = "architecture",
    orderIndex = 5,
    isAvailable = true,
    estimatedHours = 20,
    difficulty = "Advanced"
  )

  fun getWorlds(): List<WorldEntity> = listOf(
    WorldEntity(
      id = "cpp_w1",
      courseId = "cpp",
      worldNumber = 1,
      title = "C++ Foundations",
      subtitle = "std::cout, std::cin, namespaces, references, conditions, loops, and functions",
      themeColorHex = "#8B5CF6",
      iconName = "architecture",
      requiredXp = 0,
      isUnlocked = true,
      topicsJson = "[\"std::cout / cin\", \"Namespaces\", \"Data Types\", \"References (&)\", \"Control Flow\", \"Functions\", \"STL Vectors\"]"
    ),
    WorldEntity(
      id = "cpp_w2",
      courseId = "cpp",
      worldNumber = 2,
      title = "Classes & Object Architect",
      subtitle = "Classes, constructors, destructors, member methods, and Object Architect boss",
      themeColorHex = "#7C3AED",
      iconName = "view_in_ar",
      requiredXp = 250,
      isUnlocked = true,
      topicsJson = "[\"Classes\", \"Constructors & Destructors\", \"Access Specifiers\", \"STL Containers\", \"Object Architect Boss\"]"
    )
  )

  fun getChapters(): List<ChapterEntity> = listOf(
    ChapterEntity("cpp_w1_c1", "cpp_w1", 1, "C++ Core Streams & References", "Streams, operators, and reference parameters."),
    ChapterEntity("cpp_w1_c2", "cpp_w1", 2, "Control Flow & STL Intro", "Loops, conditions, and std::vector basics."),
    ChapterEntity("cpp_w2_c1", "cpp_w2", 1, "Classes & Object Design", "Encapsulation, constructors, and methods."),
    ChapterEntity("cpp_w2_c2", "cpp_w2", 2, "Object Architect Boss", "Conquer the C++ Object Architect Boss Challenge.")
  )

  fun getLessons(): List<LessonEntity> = listOf(
    LessonEntity("cpp_w1_l1", "cpp_w1_c1", "cpp_w1", 1, "std::cout & Namespaces", "Stream output and using namespace std.", LessonType.LESSON, 40, 15, true, false, 0, "Streams in C++", "std::cout << \"Hello!\" << std::endl;", "Standard character output stream.", 4),
    LessonEntity("cpp_w1_l2", "cpp_w1_c1", "cpp_w1", 2, "References vs Pointers", "Pass-by-reference using & syntax in C++.", LessonType.LESSON, 45, 15, true, false, 0, "C++ References", "void modify(int &val) { val *= 2; }", "References create aliases without manual pointer dereferencing.", 4),
    LessonEntity("cpp_w1_l3", "cpp_w1_c2", "cpp_w1", 3, "Control Flow & Vectors", "for loops and dynamic std::vector buffers.", LessonType.LESSON, 50, 20, true, false, 0, "STL Vectors", "std::vector<int> nums = {1, 2, 3};", "Dynamic resizeable vector arrays in C++.", 4),
    LessonEntity("cpp_w1_l4", "cpp_w1_c2", "cpp_w1", 4, "Functions in C++", "Default arguments and function overloading.", LessonType.CHALLENGE, 80, 30, true, false, 0, "Overloaded Methods", "int add(int a, int b); double add(double a, double b);", "Polymorphic function overloading in C++.", 5),

    LessonEntity("cpp_w2_l1", "cpp_w2_c1", "cpp_w2", 1, "C++ Classes & Objects", "public/private access, fields, and constructors.", LessonType.LESSON, 60, 25, true, false, 0, "C++ OOP", "class Player { private: int hp; public: Player(int h) : hp(h) {} };", "Class blueprint definition with access modifiers.", 5),
    LessonEntity("cpp_w2_l2", "cpp_w2_c1", "cpp_w2", 2, "Constructors & Destructors", "Resource management (RAII) and lifecycle.", LessonType.LESSON, 65, 25, true, false, 0, "RAII & Destructors", "~Player() { /* cleanup */ }", "Deterministic object destruction when going out of scope.", 5),
    LessonEntity("cpp_w2_l3", "cpp_w2_c2", "cpp_w2", 3, "OBJECT ARCHITECT Boss Battle", "Conquer the C++ Object Architect Boss Challenge!", LessonType.BOSS, 120, 50, true, false, 0, "Boss Combat", "// Architect test\nPlayer p(100); p.takeDamage(20);", "Synthesize classes, constructors, and vectors to conquer the Boss.", 6)
  )

  fun getExercises(): List<ExerciseEntity> = listOf(
    ExerciseEntity(
      id = "cpp_ex_1",
      lessonId = "cpp_w1_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which stream object in C++ is standardly used to output text to the console?",
      explanation = "std::cout represents standard character output in C++.",
      optionsJson = "[\"std::cout\", \"std::cin\", \"std::cerr\", \"std::endl\"]",
      correctAnswersJson = "[\"std::cout\"]",
      hintsJson = "[\"Look for cout (character output).\"]",
      topic = "Streams"
    ),
    ExerciseEntity(
      id = "cpp_ex_2",
      lessonId = "cpp_w2_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "In C++, which keyword makes class members inaccessible from outside the class?",
      explanation = "The 'private' access specifier restricts visibility to member functions of that class.",
      optionsJson = "[\"private\", \"public\", \"protected\", \"internal\"]",
      correctAnswersJson = "[\"private\"]",
      hintsJson = "[\"Use 'private' for encapsulated fields.\"]",
      topic = "OOP & Encapsulation"
    )
  )
}
