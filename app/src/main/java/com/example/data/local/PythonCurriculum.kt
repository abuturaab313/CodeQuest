package com.example.data.local

import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

object PythonCurriculum {

  fun getCourses(): List<CourseEntity> = listOf(
    CourseEntity(
      id = "python",
      title = "Python Mastery",
      description = "Master programming from zero with Python's clean syntax and immense power.",
      language = "python",
      totalWorlds = 10,
      iconName = "code",
      orderIndex = 1,
      isAvailable = true,
      estimatedHours = 24,
      difficulty = "Beginner"
    ),
    CourseEntity(
      id = "javascript",
      title = "JavaScript & Web",
      description = "Build interactive web apps, animations, and modern frontend engines.",
      language = "javascript",
      totalWorlds = 5,
      iconName = "javascript",
      orderIndex = 2,
      isAvailable = true,
      estimatedHours = 14,
      difficulty = "Beginner"
    ),
    CourseEntity(
      id = "html_css",
      title = "HTML & CSS Design",
      description = "Structure beautiful web pages, responsive flexbox, and modern CSS grid.",
      language = "html_css",
      totalWorlds = 4,
      iconName = "html",
      orderIndex = 3,
      isAvailable = true,
      estimatedHours = 8,
      difficulty = "Beginner"
    ),
    CourseEntity(
      id = "java",
      title = "Java & OOP",
      description = "Learn structured object-oriented programming, classes, and backend systems.",
      language = "java",
      totalWorlds = 5,
      iconName = "java",
      orderIndex = 4,
      isAvailable = true,
      estimatedHours = 16,
      difficulty = "Intermediate"
    ),
    CourseEntity(
      id = "cpp",
      title = "C & C++ Systems",
      description = "Understand memory management, pointers, and high-performance algorithms.",
      language = "cpp",
      totalWorlds = 5,
      iconName = "memory",
      orderIndex = 5,
      isAvailable = true,
      estimatedHours = 18,
      difficulty = "Advanced"
    ),
    CourseEntity(
      id = "sql",
      title = "SQL Data Studio",
      description = "Query, join, and manipulate relational databases with precision.",
      language = "sql",
      totalWorlds = 4,
      iconName = "database",
      orderIndex = 6,
      isAvailable = true,
      estimatedHours = 8,
      difficulty = "Beginner"
    )
  )

  fun getWorlds(): List<WorldEntity> = listOf(
    WorldEntity(
      id = "py_w1",
      courseId = "python",
      worldNumber = 1,
      title = "Python Foundations",
      subtitle = "Instructions, printing, variables, comments, and strings",
      themeColorHex = "#005AC1",
      iconName = "terminal",
      requiredXp = 0,
      isUnlocked = true,
      topicsJson = "[\"Syntax\", \"print()\", \"Comments\", \"Variables\", \"Strings\", \"Numbers\", \"Input\"]"
    ),
    WorldEntity(
      id = "py_w2",
      courseId = "python",
      worldNumber = 2,
      title = "Control Flow",
      subtitle = "Branching conditional logic, nested decisions, and loops",
      themeColorHex = "#00639B",
      iconName = "psychology",
      requiredXp = 100,
      isUnlocked = true,
      topicsJson = "[\"If Statements\", \"Booleans\", \"While Loops\", \"For Loops\"]"
    ),
    WorldEntity(
      id = "py_w3",
      courseId = "python",
      worldNumber = 3,
      title = "Data Structures",
      subtitle = "Lists, indexing, slicing, tuples, sets, and dictionaries",
      themeColorHex = "#5B53A4",
      iconName = "folder",
      requiredXp = 250,
      isUnlocked = false,
      topicsJson = "[\"Lists\", \"Slicing\", \"Sets\", \"Dictionaries\"]"
    ),
    WorldEntity(
      id = "py_w4",
      courseId = "python",
      worldNumber = 4,
      title = "Functions",
      subtitle = "Defining functions, parameters, return values, scope, and composition",
      themeColorHex = "#7D5260",
      iconName = "construction",
      requiredXp = 450,
      isUnlocked = false,
      topicsJson = "[\"Functions\", \"Arguments\", \"Return\", \"Scope\"]"
    ),
    WorldEntity(
      id = "py_w5",
      courseId = "python",
      worldNumber = 5,
      title = "Object-Oriented Programming",
      subtitle = "Classes, objects, attributes, methods, inheritance, and encapsulation",
      themeColorHex = "#006874",
      iconName = "dns",
      requiredXp = 700,
      isUnlocked = false,
      topicsJson = "[\"OOP\", \"Classes\", \"Inheritance\", \"Polymorphism\"]"
    ),
    WorldEntity(
      id = "py_w6",
      courseId = "python",
      worldNumber = 6,
      title = "Files & Exceptions",
      subtitle = "Reading/writing files, try/except error handling, and robust design",
      themeColorHex = "#984061",
      iconName = "bug_report",
      requiredXp = 1000,
      isUnlocked = false,
      topicsJson = "[\"File Handling\", \"Exceptions\", \"Try Except\", \"CSV\"]"
    ),
    WorldEntity(
      id = "py_w7",
      courseId = "python",
      worldNumber = 7,
      title = "Algorithmic Thinking",
      subtitle = "Search, sort, bubble/selection sort, and complexity",
      themeColorHex = "#6750A4",
      iconName = "query_stats",
      requiredXp = 1300,
      isUnlocked = false,
      topicsJson = "[\"Algorithms\", \"Searching\", \"Sorting\", \"Big O\"]"
    ),
    WorldEntity(
      id = "py_w8",
      courseId = "python",
      worldNumber = 8,
      title = "Advanced Python",
      subtitle = "Comprehensions, lambda, map/filter, decorators, and dataclasses",
      themeColorHex = "#38657A",
      iconName = "auto_awesome",
      requiredXp = 1600,
      isUnlocked = false,
      topicsJson = "[\"Lambda\", \"Comprehensions\", \"Decorators\", \"Type Hints\"]"
    ),
    WorldEntity(
      id = "py_w9",
      courseId = "python",
      worldNumber = 9,
      title = "Practical Python",
      subtitle = "HTTP, APIs, configuration, logging, testing, and security basics",
      themeColorHex = "#4F6354",
      iconName = "build",
      requiredXp = 2000,
      isUnlocked = false,
      topicsJson = "[\"APIs\", \"Logging\", \"Testing\", \"Security\"]"
    ),
    WorldEntity(
      id = "py_w10",
      courseId = "python",
      worldNumber = 10,
      title = "Capstone Projects",
      subtitle = "Advanced multi-file projects, validation, and documentation",
      themeColorHex = "#E07A5F",
      iconName = "workspace_premium",
      requiredXp = 2500,
      isUnlocked = false,
      topicsJson = "[\"Expense Tracker\", \"Quiz App\", \"Adventure Game\"]"
    )
  )

  fun getChapters(): List<ChapterEntity> = listOf(
    ChapterEntity("py_w1_c1", "py_w1", 1, "Meet Python", "The fundamental concepts of instructions and Python's role"),
    ChapterEntity("py_w1_c2", "py_w1", 2, "Your First Code", "Displaying output and documenting scripts with comments"),
    ChapterEntity("py_w1_c3", "py_w1", 3, "Variables & Memory", "Storing dynamic numbers and text in memory"),
    ChapterEntity("py_w1_c4", "py_w1", 4, "Working With Data", "Strings, mathematical operations, and receiving user input"),
    ChapterEntity("py_w1_c5", "py_w1", 5, "World 1 Climax", "Synthesizing your knowledge in challenges and boss battles"),
    ChapterEntity("py_w2_c1", "py_w2", 1, "Making Decisions", "Boolean branches and conditional execution"),
    ChapterEntity("py_w2_c2", "py_w2", 2, "Complex Logic", "Multi-branch logic gates and nested evaluations"),
    ChapterEntity("py_w3_c1", "py_w3", 1, "Linear Collections", "Lists, indexing, slicing, tuples, and sets"),
    ChapterEntity("py_w3_c2", "py_w3", 2, "Key-Value Collections", "Dictionaries, dictionary methods, nested collections, and iteration"),
    ChapterEntity("py_w4_c1", "py_w4", 1, "Function Mechanics", "Custom functions, parameters, arguments, and return values"),
    ChapterEntity("py_w4_c2", "py_w4", 2, "Advanced Functions", "Default arguments, scoping rules, composition, and reuse"),
    ChapterEntity("py_w5_c1", "py_w5", 1, "Objects & Classes", "Blueprints, instances, attributes, methods, and constructors"),
    ChapterEntity("py_w5_c2", "py_w5", 2, "Advanced OOP", "Inheritance, method overriding, encapsulation, and polymorphism"),
    ChapterEntity("py_w6_c1", "py_w6", 1, "File Systems", "Reading, writing, paths, resources, csv, and json loads"),
    ChapterEntity("py_w6_c2", "py_w6", 2, "Try Except Blocks", "Exception handling, Specific errors, finalizers, and custom raises"),
    ChapterEntity("py_w7_c1", "py_w7", 1, "Searching Algorithms", "Linear search, binary search concepts, min/max, and maps"),
    ChapterEntity("py_w7_c2", "py_w7", 2, "Sorting Algorithms", "Bubble sort, selection sort, and Big-O efficiency analysis"),
    ChapterEntity("py_w8_c1", "py_w8", 1, "Comprehensions & Lambdas", "Comprehensions, lambda, map/filter, enumerate, zip, and yield"),
    ChapterEntity("py_w8_c2", "py_w8", 2, "Decorators & Modules", "Decorators, packages, virtual environments, type hints, and dataclasses"),
    ChapterEntity("py_w9_c1", "py_w9", 1, "APIs & Files", "HTTP client calls, requests module, json parsing, and env variables"),
    ChapterEntity("py_w9_c2", "py_w9", 2, "Engineering Best Practices", "Logging, configuration, setups, unit testing, clean code, and security"),
    ChapterEntity("py_w10_c1", "py_w10", 1, "Capstone Showcases", "Interactive games, expense tracking systems, quiz frameworks, and student systems")
  )

  fun getLessons(): List<LessonEntity> {
    val list = mutableListOf<LessonEntity>()
    list.addAll(getHandCodedLessons())
    list.addAll(generateDynamicLessons())
    return list
  }

  fun getHandCodedLessons(): List<LessonEntity> = listOf(
    // WORLD 1 — CODE ORIGIN
    // Chapter 1: Meet Python
    LessonEntity(
      id = "py_w1_l1",
      chapterId = "py_w1_c1",
      worldId = "py_w1",
      lessonNumber = 1,
      title = "What Is Programming?",
      description = "Understand how computers execute instructions sequentially.",
      lessonType = LessonType.LESSON,
      xpReward = 25,
      coinReward = 10,
      isUnlocked = true,
      isCompleted = false,
      conceptSummary = "Computers are machines that follow precise instructions sequentially. Programming is writing these instructions to solve problems.",
      conceptSnippet = "# Python executes lines sequentially from top to bottom\ninstruction_1 = 'Wake up'\ninstruction_2 = 'Write code'",
      conceptExplanation = "Every app, game, or website is built from sequential commands. If instructions are out of order, the result changes!",
      estimatedMinutes = 3,
      prerequisiteLessonId = null
    ),
    LessonEntity(
      id = "py_w1_l2",
      chapterId = "py_w1_c1",
      worldId = "py_w1",
      lessonNumber = 2,
      title = "What Is Python?",
      description = "Explore why Python is the world's most versatile and readable language.",
      lessonType = LessonType.LESSON,
      xpReward = 25,
      coinReward = 10,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Python is a high-level, interpreted programming language created for clarity, readability, and speed of development.",
      conceptSnippet = "print('Python makes complex tasks feel simple!')",
      conceptExplanation = "Unlike languages with heavy syntax rules, Python uses clean indentation and simple English-like keywords.",
      estimatedMinutes = 3,
      prerequisiteLessonId = "py_w1_l1"
    ),

    // Chapter 2: Your First Code
    LessonEntity(
      id = "py_w1_l3",
      chapterId = "py_w1_c2",
      worldId = "py_w1",
      lessonNumber = 3,
      title = "Your First print()",
      description = "Write print statements and display output in the console.",
      lessonType = LessonType.LESSON,
      xpReward = 30,
      coinReward = 10,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "The print() function outputs text or numbers to the terminal screen. Strings must be enclosed in quotes.",
      conceptSnippet = "print('Welcome to CodeQuest')\nprint(42)",
      conceptExplanation = "Calling print(\"...\") sends your message to the standard output stream so users and developers can see results.",
      estimatedMinutes = 3,
      prerequisiteLessonId = "py_w1_l2"
    ),
    LessonEntity(
      id = "py_w1_l4",
      chapterId = "py_w1_c2",
      worldId = "py_w1",
      lessonNumber = 4,
      title = "Comments in Python",
      description = "Document your code and add developer notes using the # symbol.",
      lessonType = LessonType.LESSON,
      xpReward = 30,
      coinReward = 10,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Comments start with '#' and are completely ignored by the Python interpreter during execution.",
      conceptSnippet = "# This is a comment for human developers\nprint('Only this runs!') # Inline note",
      conceptExplanation = "Comments clarify the purpose of complex algorithms, document assumptions, and temporarily disable code during debugging.",
      estimatedMinutes = 3,
      prerequisiteLessonId = "py_w1_l3"
    ),

    // Chapter 3: Variables & Memory
    LessonEntity(
      id = "py_w1_l5",
      chapterId = "py_w1_c3",
      worldId = "py_w1",
      lessonNumber = 5,
      title = "Variables & Naming",
      description = "Store dynamic text, numbers, and states in labeled memory containers.",
      lessonType = LessonType.LESSON,
      xpReward = 35,
      coinReward = 15,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Variables store data in memory using the assignment operator '='. Use descriptive snake_case names.",
      conceptSnippet = "player_name = 'Alex'\nplayer_level = 7\nprint(player_name)",
      conceptExplanation = "Variables allow your program to be dynamic. When a variable changes, all subsequent calculations use its new value.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w1_l4"
    ),

    // Chapter 4: Working With Data
    LessonEntity(
      id = "py_w1_l6",
      chapterId = "py_w1_c4",
      worldId = "py_w1",
      lessonNumber = 6,
      title = "Strings & Text",
      description = "Manipulate text, string concatenation, quotes, and string lengths.",
      lessonType = LessonType.LESSON,
      xpReward = 35,
      coinReward = 15,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Strings represent text. Combine them with '+' (concatenation) and measure their length with len().",
      conceptSnippet = "first = 'Code'\nlast = 'Quest'\nfull = first + ' ' + last\nprint(full) # Code Quest",
      conceptExplanation = "Strings can use single quotes '...' or double quotes \"...\". The '+' operator joins text together.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w1_l5"
    ),
    LessonEntity(
      id = "py_w1_l7",
      chapterId = "py_w1_c4",
      worldId = "py_w1",
      lessonNumber = 7,
      title = "Numbers & Math",
      description = "Perform arithmetic with integers, floats, modulo, and operator precedence.",
      lessonType = LessonType.LESSON,
      xpReward = 40,
      coinReward = 15,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Python supports integers (int) and decimals (float). Operators include +, -, *, /, // (floor div), and % (modulo).",
      conceptSnippet = "gold = 100 + 50\ndamage = 15 * 2\nremainder = 10 % 3 # 1",
      conceptExplanation = "Multiplication and division are evaluated before addition and subtraction following standard math precedence.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w1_l6"
    ),
    LessonEntity(
      id = "py_w1_l8",
      chapterId = "py_w1_c4",
      worldId = "py_w1",
      lessonNumber = 8,
      title = "User Input with input()",
      description = "Prompt users for input and cast responses to appropriate types.",
      lessonType = LessonType.LESSON,
      xpReward = 45,
      coinReward = 20,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "input() captures user keyboard input as a string. Wrap with int() or float() to parse numbers.",
      conceptSnippet = "hero = input('Enter your name: ')\nage = int(input('Enter age: '))\nprint('Hero: ' + hero)",
      conceptExplanation = "Interactive programs respond to user input. Because input() always returns text, arithmetic requires type casting.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w1_l7"
    ),

    // Chapter 5: Climax
    LessonEntity(
      id = "py_w1_l9",
      chapterId = "py_w1_c5",
      worldId = "py_w1",
      lessonNumber = 9,
      title = "MINI CHALLENGE: Python Foundations",
      description = "Synthesize variables, math, input, and strings under exam conditions.",
      lessonType = LessonType.CHALLENGE,
      xpReward = 60,
      coinReward = 25,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Test your comprehensive mastery of Python fundamentals across multi-step exercises.",
      conceptSnippet = "# Synthesis challenge\nx = 10\ny = 5\nprint('Total: ' + str(x * y))",
      conceptExplanation = "Put together everything from World 1 to prove your readiness for conditional logic.",
      estimatedMinutes = 5,
      prerequisiteLessonId = "py_w1_l8"
    ),
    LessonEntity(
      id = "py_w1_l10",
      chapterId = "py_w1_c5",
      worldId = "py_w1",
      lessonNumber = 10,
      title = "BOSS BATTLE: Code Origin Guardian",
      description = "Defeat the guardian by solving real-time algorithmic puzzles and debugging traps.",
      lessonType = LessonType.BOSS,
      xpReward = 100,
      coinReward = 50,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Face the Guardian of World 1. Complete all combat puzzles to unlock World 2: Logic Lab!",
      conceptSnippet = "# Boss Phase 1: Authentication Protocol\nkey = 42\nstatus = 'GRANTED'",
      conceptExplanation = "Boss challenges combine bug finding, code ordering, and output prediction in an intense sequence.",
      estimatedMinutes = 6,
      prerequisiteLessonId = "py_w1_l9"
    ),

    // WORLD 2 — LOGIC LAB
    LessonEntity(
      id = "py_w2_l1",
      chapterId = "py_w2_c1",
      worldId = "py_w2",
      lessonNumber = 1,
      title = "Comparisons",
      description = "Test equality and relative magnitude (==, !=, <, >, <=, >=).",
      lessonType = LessonType.LESSON,
      xpReward = 35,
      coinReward = 15,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Comparison operators compare values and return either True or False.",
      conceptSnippet = "print(10 > 5)   # True\nprint(7 == 9)   # False\nprint('a' != 'b') # True",
      conceptExplanation = "Use '==' for comparison and '=' for assignment. Never confuse the two!",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w1_l10"
    ),
    LessonEntity(
      id = "py_w2_l2",
      chapterId = "py_w2_c1",
      worldId = "py_w2",
      lessonNumber = 2,
      title = "Boolean Logic",
      description = "Combine conditions with 'and', 'or', and invert with 'not'.",
      lessonType = LessonType.LESSON,
      xpReward = 40,
      coinReward = 15,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Logical operators combine boolean states: 'and' requires both True, 'or' requires at least one True.",
      conceptSnippet = "has_key = True\nhas_mana = False\ncan_cast = has_key or has_mana # True",
      conceptExplanation = "Booleans are the bedrock of computer decision trees. Combine multiple checks into a single clean gate.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w2_l1"
    ),
    LessonEntity(
      id = "py_w2_l3",
      chapterId = "py_w2_c1",
      worldId = "py_w2",
      lessonNumber = 3,
      title = "if Statements",
      description = "Branch your program flow conditionally using indented blocks.",
      lessonType = LessonType.LESSON,
      xpReward = 45,
      coinReward = 20,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "An 'if' statement executes its indented block only when the test condition evaluates to True.",
      conceptSnippet = "score = 85\nif score >= 80:\n    print('You Passed!')",
      conceptExplanation = "Python uses indentation (typically 4 spaces) to define code blocks under conditional headers.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w2_l2"
    ),
    LessonEntity(
      id = "py_w2_l4",
      chapterId = "py_w2_c1",
      worldId = "py_w2",
      lessonNumber = 4,
      title = "elif Branches",
      description = "Handle multiple mutually exclusive conditions in sequence.",
      lessonType = LessonType.LESSON,
      xpReward = 50,
      coinReward = 20,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "'elif' (else if) checks additional conditions sequentially if prior tests were False.",
      conceptSnippet = "grade = 75\nif grade >= 90:\n    print('A')\nelif grade >= 70:\n    print('B')",
      conceptExplanation = "Python tests conditions from top to bottom and stops as soon as the first matching branch is executed.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w2_l3"
    ),
    LessonEntity(
      id = "py_w2_l5",
      chapterId = "py_w2_c2",
      worldId = "py_w2",
      lessonNumber = 5,
      title = "else Fallbacks",
      description = "Define default fallback behavior when all preceding conditions evaluate to False.",
      lessonType = LessonType.LESSON,
      xpReward = 50,
      coinReward = 20,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "'else' catches all cases where none of the 'if' or 'elif' conditions were met.",
      conceptSnippet = "power = 0\nif power > 0:\n    print('Running')\nelse:\n    print('Shut Down')",
      conceptExplanation = "An else block has no condition of its own—it acts as the universal safety net.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w2_l4"
    ),
    LessonEntity(
      id = "py_w2_l6",
      chapterId = "py_w2_c2",
      worldId = "py_w2",
      lessonNumber = 6,
      title = "Nested Conditions",
      description = "Place if statements inside other branches for complex decision trees.",
      lessonType = LessonType.LESSON,
      xpReward = 55,
      coinReward = 25,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Nest conditional statements inside each other by indenting an extra 4 spaces.",
      conceptSnippet = "if is_logged_in:\n    if is_admin:\n        print('Admin Console')\n    else:\n        print('User Dashboard')",
      conceptExplanation = "Nested conditions allow multi-layered permission checks and contextual workflows.",
      estimatedMinutes = 4,
      prerequisiteLessonId = "py_w2_l5"
    ),
    LessonEntity(
      id = "py_w2_l7",
      chapterId = "py_w2_c2",
      worldId = "py_w2",
      lessonNumber = 7,
      title = "LOGIC CHALLENGE",
      description = "Debug and construct compound and/or/not boolean expressions.",
      lessonType = LessonType.CHALLENGE,
      xpReward = 75,
      coinReward = 30,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Challenge your logical reasoning with multi-branch logic puzzles.",
      conceptSnippet = "# Compound logic check\nif (age >= 18 and has_id) or is_vip:\n    print('Access Granted')",
      conceptExplanation = "Mastery of boolean logic makes your software robust, secure, and resilient.",
      estimatedMinutes = 5,
      prerequisiteLessonId = "py_w2_l6"
    ),
    LessonEntity(
      id = "py_w2_l8",
      chapterId = "py_w2_c2",
      worldId = "py_w2",
      lessonNumber = 8,
      title = "BOSS: Gatekeeper of Logic",
      description = "Construct a multi-condition authentication security algorithm.",
      lessonType = LessonType.BOSS,
      xpReward = 120,
      coinReward = 60,
      isUnlocked = false,
      isCompleted = false,
      conceptSummary = "Overcome the Gatekeeper's security checks to unlock World 3: Loop City.",
      conceptSnippet = "# Security Gatekeeper Algorithm\nprotocol = 'AUTHENTICATED'\nclearance = 5",
      conceptExplanation = "Conquer this multi-stage boss to prove full command of conditionals and logic branching.",
      estimatedMinutes = 6,
      prerequisiteLessonId = "py_w2_l7"
    )
  )

  fun getExercises(): List<ExerciseEntity> {
    val list = mutableListOf<ExerciseEntity>()
    list.addAll(getHandCodedExercises())
    list.addAll(generateDynamicExercises())
    return list
  }

  fun getHandCodedExercises(): List<ExerciseEntity> = listOf(
    // ==========================================
    // py_w1_l1: What Is Programming? (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l1_1",
      lessonId = "py_w1_l1",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "What is computer programming at its core?",
      explanation = "Programming is the craft of writing step-by-step instructions (code) that a computer executes to solve problems.",
      optionsJson = "[\"Giving step-by-step instructions to a computer\", \"Fixing computer hardware monitors\", \"Surfing websites faster\", \"Editing photos in Photoshop\"]",
      correctAnswersJson = "[\"Giving step-by-step instructions to a computer\"]",
      hintsJson = "[\"Think of how a recipe gives step-by-step instructions to a chef.\", \"It involves writing structured instructions for the machine to execute.\"]",
      topic = "Syntax"
    ),
    ExerciseEntity(
      id = "ex_w1_l1_2",
      lessonId = "py_w1_l1",
      orderIndex = 2,
      type = ExerciseType.TRUE_FALSE,
      prompt = "Computers can automatically guess what you meant if you make a spelling or syntax mistake in your code.",
      explanation = "Computers are literal machines—they cannot guess intent. Code must be syntactically precise.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"False\"]",
      hintsJson = "[\"Do computers have human intuition, or do they follow strict rules?\", \"Computers require exact syntax to understand instructions.\"]",
      topic = "Syntax"
    ),
    ExerciseEntity(
      id = "ex_w1_l1_3",
      lessonId = "py_w1_l1",
      orderIndex = 3,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "In what order does Python execute lines in a standard script?",
      explanation = "Python executes code sequentially from top to bottom, line by line.",
      optionsJson = "[\"Top to bottom, line by line\", \"Bottom to top in reverse\", \"All lines at the exact same millisecond\", \"In random order\"]",
      correctAnswersJson = "[\"Top to bottom, line by line\"]",
      hintsJson = "[\"Think about reading lines of text on a page.\", \"Line 1 executes before line 2.\"]",
      topic = "Syntax"
    ),
    ExerciseEntity(
      id = "ex_w1_l1_4",
      lessonId = "py_w1_l1",
      orderIndex = 4,
      type = ExerciseType.MATCH_CONCEPTS,
      prompt = "Match each fundamental programming term to its correct definition:",
      explanation = "Syntax is the grammar rules, Algorithm is the recipe/steps, Bug is an error, Interpreter runs the code.",
      optionsJson = "[\"Syntax -> Rules of writing code\", \"Algorithm -> Step-by-step procedure\", \"Bug -> An error in code\", \"Interpreter -> Executes code line by line\"]",
      correctAnswersJson = "[\"Syntax->Rules of writing code\", \"Algorithm->Step-by-step procedure\", \"Bug->An error in code\", \"Interpreter->Executes code line by line\"]",
      hintsJson = "[\"Syntax refers to the formal grammar of the language.\", \"A Bug is an unwanted defect or crash.\"]",
      topic = "Syntax"
    ),

    // ==========================================
    // py_w1_l2: What Is Python? (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l2_1",
      lessonId = "py_w1_l2",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Why is Python one of the most popular programming languages in the world?",
      explanation = "Python is renowned for its clean, human-readable syntax that looks almost like plain English.",
      optionsJson = "[\"Clean, highly readable syntax and versatility\", \"It only works on supercomputers\", \"It requires memorizing binary numbers\", \"It only works for video games\"]",
      correctAnswersJson = "[\"Clean, highly readable syntax and versatility\"]",
      hintsJson = "[\"Python focuses on developer productivity and code readability.\"]",
      topic = "Fundamentals"
    ),
    ExerciseEntity(
      id = "ex_w1_l2_2",
      lessonId = "py_w1_l2",
      orderIndex = 2,
      type = ExerciseType.TRUE_FALSE,
      prompt = "In Python, you must put a semicolon ';' at the end of every line to run the code.",
      explanation = "Unlike C++ or Java, Python uses newlines rather than semicolons to terminate statements.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"False\"]",
      hintsJson = "[\"Python syntax prioritizes clean, uncluttered lines.\"]",
      topic = "Syntax"
    ),
    ExerciseEntity(
      id = "ex_w1_l2_3",
      lessonId = "py_w1_l2",
      orderIndex = 3,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Fill in the standard Python keyword to display text on the screen:",
      explanation = "print() is Python's built-in output function.",
      starterCode = "___('Hello Adventurer')",
      solutionCode = "print('Hello Adventurer')",
      optionsJson = "[\"print\", \"echo\", \"display\", \"write\"]",
      correctAnswersJson = "[\"print\"]",
      hintsJson = "[\"Starts with 'p' and rhymes with sprint.\"]",
      topic = "print()"
    ),
    ExerciseEntity(
      id = "ex_w1_l2_4",
      lessonId = "py_w1_l2",
      orderIndex = 4,
      type = ExerciseType.MATCH_CONCEPTS,
      prompt = "Match the technology domain with how Python is used in it:",
      explanation = "Python powers Web APIs, Data Analytics, Machine Learning, and Automated Scripting.",
      optionsJson = "[\"Web Backend -> Django and FastAPI\", \"Data Science -> Pandas and NumPy\", \"AI & ML -> TensorFlow and PyTorch\", \"Automation -> Scripting repetitive tasks\"]",
      correctAnswersJson = "[\"Web Backend->Django and FastAPI\", \"Data Science->Pandas and NumPy\", \"AI & ML->TensorFlow and PyTorch\", \"Automation->Scripting repetitive tasks\"]",
      hintsJson = "[\"Pandas is for data, TensorFlow is for AI.\"]",
      topic = "Fundamentals"
    ),

    // ==========================================
    // py_w1_l3: Your First print() (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l3_1",
      lessonId = "py_w1_l3",
      orderIndex = 1,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Complete the code to print 'Level Up' to the console:",
      explanation = "print() takes string arguments wrapped in quotation marks.",
      starterCode = "___(\"Level Up\")",
      solutionCode = "print(\"Level Up\")",
      optionsJson = "[\"print\", \"output\", \"say\", \"log\"]",
      correctAnswersJson = "[\"print\"]",
      hintsJson = "[\"Use the built-in print() function.\"]",
      topic = "print()"
    ),
    ExerciseEntity(
      id = "ex_w1_l3_2",
      lessonId = "py_w1_l3",
      orderIndex = 2,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What will this code display?\n\nprint(\"Code\")\nprint(\"Quest\")",
      explanation = "Each call to print() adds a newline at the end by default.",
      optionsJson = "[\"Code\\nQuest\", \"CodeQuest\", \"Code Quest\", \"Error\"]",
      correctAnswersJson = "[\"Code\\nQuest\", \"Code\nQuest\"]",
      expectedOutput = "Code\nQuest",
      hintsJson = "[\"print() outputs its text and moves to the next line.\"]",
      topic = "print()"
    ),
    ExerciseEntity(
      id = "ex_w1_l3_3",
      lessonId = "py_w1_l3",
      orderIndex = 3,
      type = ExerciseType.CODE_ORDER,
      prompt = "Arrange these code blocks in the correct order to print 'Start Game':",
      explanation = "The function name comes first, followed by opening parenthesis, string in quotes, and closing parenthesis.",
      optionsJson = "[\"print\", \"(\", \"\\\"Start Game\\\"\", \")\"]",
      correctAnswersJson = "[\"print\", \"(\", \"\\\"Start Game\\\"\", \")\"]",
      hintsJson = "[\"Function name -> ( -> String argument -> )\"]",
      topic = "print()"
    ),
    ExerciseEntity(
      id = "ex_w1_l3_4",
      lessonId = "py_w1_l3",
      orderIndex = 4,
      type = ExerciseType.FIND_BUG,
      prompt = "Which of the following lines contains a SYNTAX ERROR?",
      explanation = "String literals must be enclosed in quotes; print(Hello World) without quotes triggers a NameError / SyntaxError.",
      optionsJson = "[\"print(Hello World)\", \"print(\\\"Hello World\\\")\", \"print('Hello World')\", \"print(123)\"]",
      correctAnswersJson = "[\"print(Hello World)\"]",
      hintsJson = "[\"Look for unquoted text passed inside the parenthesis.\"]",
      topic = "Syntax"
    ),

    // ==========================================
    // py_w1_l4: Comments in Python (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l4_1",
      lessonId = "py_w1_l4",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which symbol is used to create a single-line comment in Python?",
      explanation = "The hash/pound symbol '#' marks the start of a single-line comment in Python.",
      optionsJson = "[\"#\", \"//\", \"/*\", \"--\"]",
      correctAnswersJson = "[\"#\"]",
      hintsJson = "[\"It is also known as the hashtag or number sign.\"]",
      topic = "Comments"
    ),
    ExerciseEntity(
      id = "ex_w1_l4_2",
      lessonId = "py_w1_l4",
      orderIndex = 2,
      type = ExerciseType.TRUE_FALSE,
      prompt = "Python will execute and run any code written after a '#' on that same line.",
      explanation = "Everything following a '#' on that line is treated as a comment and ignored by the interpreter.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"False\"]",
      hintsJson = "[\"Comments are for human eyes only.\"]",
      topic = "Comments"
    ),
    ExerciseEntity(
      id = "ex_w1_l4_3",
      lessonId = "py_w1_l4",
      orderIndex = 3,
      type = ExerciseType.COMPLETE_CODE,
      prompt = "Add the comment symbol so Python ignores the developer note:",
      explanation = "Prefix the note with '#'.",
      starterCode = "___ Initialize player health\nhealth = 100",
      solutionCode = "# Initialize player health\nhealth = 100",
      optionsJson = "[\"#\", \"//\", \"<!--\", \"REM\"]",
      correctAnswersJson = "[\"#\"]",
      hintsJson = "[\"Use the '#' character.\"]",
      topic = "Comments"
    ),
    ExerciseEntity(
      id = "ex_w1_l4_4",
      lessonId = "py_w1_l4",
      orderIndex = 4,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What will this program output?\n\nprint(\"Alpha\")\n# print(\"Beta\")\nprint(\"Gamma\")",
      explanation = "Line 2 is commented out, so only 'Alpha' and 'Gamma' are printed.",
      optionsJson = "[\"Alpha\\nGamma\", \"Alpha\\nBeta\\nGamma\", \"Beta\\nGamma\", \"Error\"]",
      correctAnswersJson = "[\"Alpha\\nGamma\", \"Alpha\nGamma\"]",
      expectedOutput = "Alpha\nGamma",
      hintsJson = "[\"Line 2 has a '#' at the start, so it is skipped.\"]",
      topic = "Comments"
    ),

    // ==========================================
    // py_w1_l5: Variables & Naming (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l5_1",
      lessonId = "py_w1_l5",
      orderIndex = 1,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Assign the number 50 to the variable 'coins':",
      explanation = "The single '=' operator assigns the value on the right to the variable on the left.",
      starterCode = "coins ___ 50",
      solutionCode = "coins = 50",
      optionsJson = "[\"=\", \"==\", \":=\", \"->\"]",
      correctAnswersJson = "[\"=\"]",
      hintsJson = "[\"Use the single assignment operator '='.\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "ex_w1_l5_2",
      lessonId = "py_w1_l5",
      orderIndex = 2,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What will be printed?\n\nxp = 10\nxp = 25\nprint(xp)",
      explanation = "Variables hold the most recently assigned value. xp is overwritten with 25.",
      optionsJson = "[\"25\", \"10\", \"35\", \"xp\"]",
      correctAnswersJson = "[\"25\"]",
      expectedOutput = "25",
      hintsJson = "[\"Variables can change value over time.\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "ex_w1_l5_3",
      lessonId = "py_w1_l5",
      orderIndex = 3,
      type = ExerciseType.FIND_BUG,
      prompt = "Which of the following variable names is INVALID in Python?",
      explanation = "Variable names cannot begin with a number.",
      optionsJson = "[\"2nd_player\", \"player_2\", \"_player\", \"playerTwo\"]",
      correctAnswersJson = "[\"2nd_player\"]",
      hintsJson = "[\"Python identifiers cannot start with a digit.\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "ex_w1_l5_4",
      lessonId = "py_w1_l5",
      orderIndex = 4,
      type = ExerciseType.MATCH_CONCEPTS,
      prompt = "Match the variable naming convention to its style:",
      explanation = "snake_case is standard in Python, camelCase is common in JS, PascalCase is used for classes.",
      optionsJson = "[\"snake_case -> player_score\", \"camelCase -> playerScore\", \"PascalCase -> PlayerScore\", \"SCREAMING_SNAKE -> MAX_HEARTS\"]",
      correctAnswersJson = "[\"snake_case->player_score\", \"camelCase->playerScore\", \"PascalCase->PlayerScore\", \"SCREAMING_SNAKE->MAX_HEARTS\"]",
      hintsJson = "[\"Python variables use lowercase letters separated by underscores.\"]",
      topic = "Variables"
    ),

    // ==========================================
    // py_w1_l6: Strings & Text (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l6_1",
      lessonId = "py_w1_l6",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What is the output of this code?\n\nprint(\"Code\" + \"Quest\")",
      explanation = "The '+' operator concatenates strings together directly with no added spaces.",
      optionsJson = "[\"CodeQuest\", \"Code Quest\", \"Code+Quest\", \"Error\"]",
      correctAnswersJson = "[\"CodeQuest\"]",
      expectedOutput = "CodeQuest",
      hintsJson = "[\"String addition glues characters together seamlessly.\"]",
      topic = "Strings"
    ),
    ExerciseEntity(
      id = "ex_w1_l6_2",
      lessonId = "py_w1_l6",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Find the length of the string 'Hero':",
      explanation = "len() returns the number of characters in a string.",
      starterCode = "count = ___(\"Hero\")",
      solutionCode = "count = len(\"Hero\")",
      optionsJson = "[\"len\", \"length\", \"size\", \"count\"]",
      correctAnswersJson = "[\"len\"]",
      hintsJson = "[\"Short for 'length'. It has 3 letters.\"]",
      topic = "Strings"
    ),
    ExerciseEntity(
      id = "ex_w1_l6_3",
      lessonId = "py_w1_l6",
      orderIndex = 3,
      type = ExerciseType.CODE_ORDER,
      prompt = "Arrange these tokens to create full_name = 'Ada' + ' ' + 'Lovelace':",
      explanation = "Variable name '=' First string '+' Space '+' Second string.",
      optionsJson = "[\"full_name\", \"=\", \"\\\"Ada\\\"\", \"+\", \"\\\" \\\"\", \"+\", \"\\\"Lovelace\\\"\"]",
      correctAnswersJson = "[\"full_name\", \"=\", \"\\\"Ada\\\"\", \"+\", \"\\\" \\\"\", \"+\", \"\\\"Lovelace\\\"\"]",
      hintsJson = "[\"Variable assignment on left, string concatenation on right.\"]",
      topic = "Strings"
    ),
    ExerciseEntity(
      id = "ex_w1_l6_4",
      lessonId = "py_w1_l6",
      orderIndex = 4,
      type = ExerciseType.FIND_BUG,
      prompt = "What is the syntax bug in this string declaration?\n\ntitle = \"CodeQuest'",
      explanation = "The opening double quote does not match the closing single quote.",
      optionsJson = "[\"Mismatched quotes (starts with double, ends with single)\", \"Missing semicolon at the end\", \"Variable name 'title' is a reserved keyword\", \"Cannot assign strings to variables\"]",
      correctAnswersJson = "[\"Mismatched quotes (starts with double, ends with single)\"]",
      hintsJson = "[\"Check the quotation marks on both sides of the string.\"]",
      topic = "Strings"
    ),

    // ==========================================
    // py_w1_l7: Numbers & Math (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l7_1",
      lessonId = "py_w1_l7",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this code print?\n\nprint(10 + 5 * 2)",
      explanation = "Multiplication (5 * 2 = 10) takes precedence before addition (10 + 10 = 20).",
      optionsJson = "[\"20\", \"30\", \"100\", \"1052\"]",
      correctAnswersJson = "[\"20\"]",
      expectedOutput = "20",
      hintsJson = "[\"PEMDAS: Multiply before adding.\"]",
      topic = "Numbers"
    ),
    ExerciseEntity(
      id = "ex_w1_l7_2",
      lessonId = "py_w1_l7",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Which operator calculates the remainder of integer division in Python?",
      explanation = "The '%' (modulo) operator calculates the remainder: 10 % 3 equals 1.",
      starterCode = "remainder = 10 ___ 3",
      solutionCode = "remainder = 10 % 3",
      optionsJson = "[\"%\", \"//\", \"/\", \"mod\"]",
      correctAnswersJson = "[\"%\"]",
      hintsJson = "[\"The percent sign '%' is the modulo operator.\"]",
      topic = "Numbers"
    ),
    ExerciseEntity(
      id = "ex_w1_l7_3",
      lessonId = "py_w1_l7",
      orderIndex = 3,
      type = ExerciseType.TRUE_FALSE,
      prompt = "In Python 3, normal division '10 / 2' produces a float ('5.0') rather than an int.",
      explanation = "True! Standard '/' always returns a floating-point number in Python 3. Use '//' for integer floor division.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"True\"]",
      hintsJson = "[\"Single slash '/' always results in a float.\"]",
      topic = "Numbers"
    ),
    ExerciseEntity(
      id = "ex_w1_l7_4",
      lessonId = "py_w1_l7",
      orderIndex = 4,
      type = ExerciseType.COMPLETE_CODE,
      prompt = "Complete the formula to double the attack power:\nattack = 15\ndoubled = attack ___ 2",
      explanation = "'*' is the multiplication operator.",
      starterCode = "doubled = attack ___ 2",
      solutionCode = "doubled = attack * 2",
      optionsJson = "[\"*\", \"x\", \"**\", \"+\"]",
      correctAnswersJson = "[\"*\"]",
      hintsJson = "[\"Use the asterisk '*' for multiplication.\"]",
      topic = "Numbers"
    ),

    // ==========================================
    // py_w1_l8: User Input with input() (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l8_1",
      lessonId = "py_w1_l8",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "What data type does the built-in input() function ALWAYS return?",
      explanation = "input() always returns a string (str), even if the user typed numbers.",
      optionsJson = "[\"String (str)\", \"Integer (int)\", \"Float (float)\", \"Boolean (bool)\"]",
      correctAnswersJson = "[\"String (str)\"]",
      hintsJson = "[\"Even if you type 42, input() captures it as '42'.\"]",
      topic = "Input"
    ),
    ExerciseEntity(
      id = "ex_w1_l8_2",
      lessonId = "py_w1_l8",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Convert user input text to an integer number:",
      explanation = "int() casts the string input into an integer.",
      starterCode = "age = ___(input(\"Enter age: \"))",
      solutionCode = "age = int(input(\"Enter age: \"))",
      optionsJson = "[\"int\", \"str\", \"num\", \"parse\"]",
      correctAnswersJson = "[\"int\"]",
      hintsJson = "[\"Short for integer.\"]",
      topic = "Input"
    ),
    ExerciseEntity(
      id = "ex_w1_l8_3",
      lessonId = "py_w1_l8",
      orderIndex = 3,
      type = ExerciseType.COMPLETE_CODE,
      prompt = "Complete the code to prompt the player for their hero name:",
      explanation = "Use input(\"...\") to prompt the user.",
      starterCode = "hero = ___(\"Enter name: \")",
      solutionCode = "hero = input(\"Enter name: \")",
      optionsJson = "[\"input\", \"prompt\", \"read\", \"scan\"]",
      correctAnswersJson = "[\"input\"]",
      hintsJson = "[\"Use Python's built-in input() function.\"]",
      topic = "Input"
    ),
    ExerciseEntity(
      id = "ex_w1_l8_4",
      lessonId = "py_w1_l8",
      orderIndex = 4,
      type = ExerciseType.FIND_BUG,
      prompt = "Why does this code cause a TypeError?\n\nage = input(\"Your age: \")\nnext_year = age + 1",
      explanation = "age is a string from input(). Python cannot add an integer (1) to a string without int(age) casting.",
      optionsJson = "[\"Cannot add integer to string; input() must be wrapped in int()\", \"Variable names cannot be 'age'\", \"Missing colon at the end of input()\", \"Cannot add numbers to variables\"]",
      correctAnswersJson = "[\"Cannot add integer to string; input() must be wrapped in int()\"]",
      hintsJson = "[\"input() returns text. You need to convert it before doing math.\"]",
      topic = "Input"
    ),

    // ==========================================
    // py_w1_l9: MINI CHALLENGE: Python Foundations (5 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l9_1",
      lessonId = "py_w1_l9",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "Predict the output:\n\nname = \"Alex\"\nxp = 50 + 50\nprint(name + \": \" + str(xp))",
      explanation = "name is 'Alex', xp is 100. str(xp) converts 100 to '100'. Concatenation yields 'Alex: 100'.",
      optionsJson = "[\"Alex: 100\", \"Alex: 5050\", \"Alex: xp\", \"Error\"]",
      correctAnswersJson = "[\"Alex: 100\"]",
      expectedOutput = "Alex: 100",
      hintsJson = "[\"50 + 50 = 100, converted to string and joined with 'Alex: '.\"]",
      topic = "Fundamentals"
    ),
    ExerciseEntity(
      id = "ex_w1_l9_2",
      lessonId = "py_w1_l9",
      orderIndex = 2,
      type = ExerciseType.CODE_ORDER,
      prompt = "Assemble the formula to calculate area = width * height:",
      explanation = "width = 10\nheight = 5\narea = width * height\nprint(area)",
      optionsJson = "[\"width = 10\", \"height = 5\", \"area = width * height\", \"print(area)\"]",
      correctAnswersJson = "[\"width = 10\", \"height = 5\", \"area = width * height\", \"print(area)\"]",
      hintsJson = "[\"Define dimensions first, then compute area, then print.\"]",
      topic = "Numbers"
    ),
    ExerciseEntity(
      id = "ex_w1_l9_3",
      lessonId = "py_w1_l9",
      orderIndex = 3,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Convert a number into a string so it can be concatenated with text:",
      explanation = "str() converts data to a string in Python.",
      starterCode = "msg = \"Level \" + ___(5)",
      solutionCode = "msg = \"Level \" + str(5)",
      optionsJson = "[\"str\", \"string\", \"text\", \"to_str\"]",
      correctAnswersJson = "[\"str\"]",
      hintsJson = "[\"Short for string: 3 letters.\"]",
      topic = "Strings"
    ),
    ExerciseEntity(
      id = "ex_w1_l9_4",
      lessonId = "py_w1_l9",
      orderIndex = 4,
      type = ExerciseType.TRUE_FALSE,
      prompt = "Variables in Python can be reassigned to a different data type at runtime (e.g. from int to string).",
      explanation = "True! Python is dynamically typed, so a variable can hold an int and later hold a string.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"True\"]",
      hintsJson = "[\"Python is dynamically typed.\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "ex_w1_l9_5",
      lessonId = "py_w1_l9",
      orderIndex = 5,
      type = ExerciseType.FIND_BUG,
      prompt = "Find the line causing a runtime error:\n\n1: base = 10\n2: height = '5'\n3: area = base * height\n4: print(area)",
      explanation = "Line 2 has '5' as a string instead of number 5. base * '5' repeats '5' ten times ('5555555555') instead of numeric area!",
      optionsJson = "[\"Line 2: '5' is a string literal instead of integer 5\", \"Line 1: 10 cannot be assigned to base\", \"Line 4: print() cannot accept area\", \"There are no bugs\"]",
      correctAnswersJson = "[\"Line 2: '5' is a string literal instead of integer 5\"]",
      hintsJson = "[\"Look at the quotes around the number in line 2.\"]",
      topic = "Numbers"
    ),

    // ==========================================
    // py_w1_l10: BOSS BATTLE: Code Origin Guardian (4 boss exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w1_l10_1",
      lessonId = "py_w1_l10",
      orderIndex = 1,
      type = ExerciseType.BOSS_CHALLENGE,
      prompt = "BOSS PHASE 1: Solve the security key formula to breach the firewall!\n\nx = 12\ny = 4\nkey = (x * 2) + (y * 3)\nprint(key)",
      explanation = "(12 * 2) = 24. (4 * 3) = 12. 24 + 12 = 36.",
      optionsJson = "[\"36\", \"48\", \"24\", \"12\"]",
      correctAnswersJson = "[\"36\"]",
      expectedOutput = "36",
      hintsJson = "[\"Evaluate inside parentheses first: 24 + 12 = 36.\"]",
      topic = "Boss"
    ),
    ExerciseEntity(
      id = "ex_w1_l10_2",
      lessonId = "py_w1_l10",
      orderIndex = 2,
      type = ExerciseType.CODE_ORDER,
      prompt = "BOSS PHASE 2: Reconstruct the Guardian's core bypass sequence:",
      explanation = "Initialize protocol, activate power, and transmit clearance message.",
      optionsJson = "[\"protocol = \\\"OVERRIDE\\\"\", \"power = 100\", \"status = protocol + \\\" ACTIVE\\\"\", \"print(status)\"]",
      correctAnswersJson = "[\"protocol = \\\"OVERRIDE\\\"\", \"power = 100\", \"status = protocol + \\\" ACTIVE\\\"\", \"print(status)\"]",
      hintsJson = "[\"Set variables first, concatenate status next, print final status.\"]",
      topic = "Boss"
    ),
    ExerciseEntity(
      id = "ex_w1_l10_3",
      lessonId = "py_w1_l10",
      orderIndex = 3,
      type = ExerciseType.FIND_BUG,
      prompt = "BOSS PHASE 3: Neutralize the Guardian's defense trap! Which line contains the vulnerability?",
      explanation = "Line 3 tries to add string '100' to integer 50 without casting.",
      optionsJson = "[\"Line 3: shield = '100' + 50 (Type mismatch)\", \"Line 1: boss_hp = 500\", \"Line 2: boss_name = 'Guardian'\", \"Line 4: print('Ready')\"]",
      correctAnswersJson = "[\"Line 3: shield = '100' + 50 (Type mismatch)\"]",
      hintsJson = "[\"Check where string '100' is added directly to a number.\"]",
      topic = "Boss"
    ),
    ExerciseEntity(
      id = "ex_w1_l10_4",
      lessonId = "py_w1_l10",
      orderIndex = 4,
      type = ExerciseType.COMPLETE_CODE,
      prompt = "BOSS FINAL STRIKE: Complete the defeat command to shatter the Guardian's core:",
      explanation = "Call print(\"VICTORY\") to finish the battle.",
      starterCode = "___(\"VICTORY\")",
      solutionCode = "print(\"VICTORY\")",
      optionsJson = "[\"print\", \"strike\", \"execute\", \"finish\"]",
      correctAnswersJson = "[\"print\"]",
      hintsJson = "[\"Output the final victory string using print().\"]",
      topic = "Boss"
    ),

    // ==========================================
    // py_w2_l1: Comparisons (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l1_1",
      lessonId = "py_w2_l1",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this comparison print?\n\nprint(15 > 10)",
      explanation = "15 is greater than 10, so Python prints True.",
      optionsJson = "[\"True\", \"False\", \"1\", \"None\"]",
      correctAnswersJson = "[\"True\"]",
      expectedOutput = "True",
      hintsJson = "[\"Is 15 strictly greater than 10?\"]",
      topic = "Comparisons"
    ),
    ExerciseEntity(
      id = "ex_w2_l1_2",
      lessonId = "py_w2_l1",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Which operator tests if two values are EQUAL in Python?",
      explanation = "Use '==' for equality comparison ('=' is for assignment).",
      starterCode = "is_equal = (score ___ 100)",
      solutionCode = "is_equal = (score == 100)",
      optionsJson = "[\"==\", \"=\", \"===\", \"equals\"]",
      correctAnswersJson = "[\"==\"]",
      hintsJson = "[\"Double equal sign '=='.\"]",
      topic = "Comparisons"
    ),
    ExerciseEntity(
      id = "ex_w2_l1_3",
      lessonId = "py_w2_l1",
      orderIndex = 3,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What is the result of:\n\nprint(\"apple\" != \"banana\")",
      explanation = "'!=' means not equal. 'apple' is not equal to 'banana', so it evaluates to True.",
      optionsJson = "[\"True\", \"False\", \"Error\", \"None\"]",
      correctAnswersJson = "[\"True\"]",
      expectedOutput = "True",
      hintsJson = "[\"!= tests inequality.\"]",
      topic = "Comparisons"
    ),
    ExerciseEntity(
      id = "ex_w2_l1_4",
      lessonId = "py_w2_l1",
      orderIndex = 4,
      type = ExerciseType.MATCH_CONCEPTS,
      prompt = "Match each comparison operator to its meaning:",
      explanation = "== is Equal, != is Not Equal, >= is Greater or Equal, <= is Less or Equal.",
      optionsJson = "[\"== -> Equal to\", \"!= -> Not equal to\", \">= -> Greater than or equal\", \"<= -> Less than or equal\"]",
      correctAnswersJson = "[\"==->Equal to\", \"!=->Not equal to\", \">-->Greater than or equal\", \"<-->Less than or equal\"]",
      hintsJson = "[\"== is equality, != is inequality.\"]",
      topic = "Comparisons"
    ),

    // ==========================================
    // py_w2_l2: Boolean Logic (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l2_1",
      lessonId = "py_w2_l2",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this print?\n\nprint(True and False)",
      explanation = "'and' requires BOTH sides to be True. Since one is False, it returns False.",
      optionsJson = "[\"False\", \"True\", \"Error\", \"None\"]",
      correctAnswersJson = "[\"False\"]",
      expectedOutput = "False",
      hintsJson = "[\"'and' is only True if both sides are True.\"]",
      topic = "Booleans"
    ),
    ExerciseEntity(
      id = "ex_w2_l2_2",
      lessonId = "py_w2_l2",
      orderIndex = 2,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this print?\n\nprint(True or False)",
      explanation = "'or' is True if AT LEAST ONE side is True.",
      optionsJson = "[\"True\", \"False\", \"Error\", \"None\"]",
      correctAnswersJson = "[\"True\"]",
      expectedOutput = "True",
      hintsJson = "[\"'or' only needs one True condition.\"]",
      topic = "Booleans"
    ),
    ExerciseEntity(
      id = "ex_w2_l2_3",
      lessonId = "py_w2_l2",
      orderIndex = 3,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Which keyword inverts a boolean value from True to False (or False to True)?",
      explanation = "'not' negates a boolean expression.",
      starterCode = "is_offline = ___ is_online",
      solutionCode = "is_offline = not is_online",
      optionsJson = "[\"not\", \"!\", \"inv\", \"neg\"]",
      correctAnswersJson = "[\"not\"]",
      hintsJson = "[\"The Python negation keyword is 'not'.\"]",
      topic = "Booleans"
    ),
    ExerciseEntity(
      id = "ex_w2_l2_4",
      lessonId = "py_w2_l2",
      orderIndex = 4,
      type = ExerciseType.TRUE_FALSE,
      prompt = "In Python, boolean literals must be capitalized as 'True' and 'False' (lowercase 'true' causes NameError).",
      explanation = "True! Python syntax requires capital 'T' and 'F' for boolean literals.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"True\"]",
      hintsJson = "[\"Python is case-sensitive: True vs true.\"]",
      topic = "Booleans"
    ),

    // ==========================================
    // py_w2_l3: if Statements (4 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l3_1",
      lessonId = "py_w2_l3",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this code print?\n\nscore = 100\nif score >= 80:\n    print(\"Passed\")",
      explanation = "score is 100, which is >= 80 (True). 'Passed' is printed.",
      optionsJson = "[\"Passed\", \"Nothing\", \"score\", \"Error\"]",
      correctAnswersJson = "[\"Passed\"]",
      expectedOutput = "Passed",
      hintsJson = "[\"100 is greater than 80, so the if block executes.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l3_2",
      lessonId = "py_w2_l3",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Which character must end the 'if' header line before the indented block?",
      explanation = "Every if/elif/else header must end with a colon ':'.",
      starterCode = "if score > 50___\n    print(\"Win\")",
      solutionCode = "if score > 50:\n    print(\"Win\")",
      optionsJson = "[\":\", \";\", \"{\", \"=>\"]",
      correctAnswersJson = "[\":\"]",
      hintsJson = "[\"Use the colon ':' character.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l3_3",
      lessonId = "py_w2_l3",
      orderIndex = 3,
      type = ExerciseType.FIND_BUG,
      prompt = "Find the indentation bug in this code:\n\n1: health = 0\n2: if health <= 0:\n3: print(\"Game Over\")",
      explanation = "Line 3 must be indented (usually 4 spaces) under the if statement.",
      optionsJson = "[\"Line 3: print() must be indented inside the if block\", \"Line 1: health cannot be 0\", \"Line 2: Missing parentheses around health <= 0\", \"Line 2: <= should be =<\"]",
      correctAnswersJson = "[\"Line 3: print() must be indented inside the if block\"]",
      hintsJson = "[\"Python uses indentation to group code blocks.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l3_4",
      lessonId = "py_w2_l3",
      orderIndex = 4,
      type = ExerciseType.CODE_ORDER,
      prompt = "Arrange these lines to create a working if statement:",
      explanation = "hp = 100\nif hp > 0:\n    print(\"Alive\")",
      optionsJson = "[\"hp = 100\", \"if hp > 0:\", \"    print(\\\"Alive\\\")\"]",
      correctAnswersJson = "[\"hp = 100\", \"if hp > 0:\", \"    print(\\\"Alive\\\")\"]",
      hintsJson = "[\"Variable assignment first, if condition second, indented print last.\"]",
      topic = "Conditions"
    ),

    // ==========================================
    // py_w2_l4: elif Branches (3 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l4_1",
      lessonId = "py_w2_l4",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What will this code print?\n\nscore = 75\nif score >= 90:\n    print(\"Gold\")\nelif score >= 70:\n    print(\"Silver\")\nelif score >= 50:\n    print(\"Bronze\")",
      explanation = "score (75) is not >= 90, but it IS >= 70. Python prints 'Silver' and skips subsequent elif branches.",
      optionsJson = "[\"Silver\", \"Gold\", \"Bronze\", \"Silver\\nBronze\"]",
      correctAnswersJson = "[\"Silver\"]",
      expectedOutput = "Silver",
      hintsJson = "[\"Python stops at the first matching elif branch.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l4_2",
      lessonId = "py_w2_l4",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "What is Python's short keyword for 'else if'?",
      explanation = "Python uses 'elif' for else-if branches.",
      starterCode = "___ score >= 50:\n    print(\"Passed\")",
      solutionCode = "elif score >= 50:\n    print(\"Passed\")",
      optionsJson = "[\"elif\", \"else if\", \"elseif\", \"elsif\"]",
      correctAnswersJson = "[\"elif\"]",
      hintsJson = "[\"Combination of 'else' and 'if': 4 letters.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l4_3",
      lessonId = "py_w2_l4",
      orderIndex = 3,
      type = ExerciseType.TRUE_FALSE,
      prompt = "You can have multiple 'elif' blocks in a single if-statement chain.",
      explanation = "True! You can chain as many elif blocks as needed between if and else.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"True\"]",
      hintsJson = "[\"You can test many sequential conditions.\"]",
      topic = "Conditions"
    ),

    // ==========================================
    // py_w2_l5: else Fallbacks (3 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l5_1",
      lessonId = "py_w2_l5",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this code output?\n\nx = 3\nif x > 10:\n    print(\"Big\")\nelse:\n    print(\"Small\")",
      explanation = "x is 3, so x > 10 is False. Execution falls through to the else branch.",
      optionsJson = "[\"Small\", \"Big\", \"3\", \"Error\"]",
      correctAnswersJson = "[\"Small\"]",
      expectedOutput = "Small",
      hintsJson = "[\"When the if condition is False, the else block runs.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l5_2",
      lessonId = "py_w2_l5",
      orderIndex = 2,
      type = ExerciseType.TRUE_FALSE,
      prompt = "An 'else' statement can have its own condition, like: 'else x < 5:'",
      explanation = "False! 'else:' never takes a condition. To test a condition, use 'elif x < 5:'.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"False\"]",
      hintsJson = "[\"else is a fallback and takes no condition.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l5_3",
      lessonId = "py_w2_l5",
      orderIndex = 3,
      type = ExerciseType.CODE_ORDER,
      prompt = "Arrange an if-else statement correctly:",
      explanation = "if points > 0:\n    print(\"Active\")\nelse:\n    print(\"Inactive\")",
      optionsJson = "[\"if points > 0:\", \"    print(\\\"Active\\\")\", \"else:\", \"    print(\\\"Inactive\\\")\"]",
      correctAnswersJson = "[\"if points > 0:\", \"    print(\\\"Active\\\")\", \"else:\", \"    print(\\\"Inactive\\\")\"]",
      hintsJson = "[\"if -> indented print -> else -> indented print\"]",
      topic = "Conditions"
    ),

    // ==========================================
    // py_w2_l6: Nested Conditions (2 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l6_1",
      lessonId = "py_w2_l6",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "What does this print?\n\nis_member = True\nage = 20\nif is_member:\n    if age >= 18:\n        print(\"Adult Member\")\n    else:\n        print(\"Youth Member\")",
      explanation = "is_member is True, so outer block runs. age >= 18 is True, so 'Adult Member' is printed.",
      optionsJson = "[\"Adult Member\", \"Youth Member\", \"Nothing\", \"Error\"]",
      correctAnswersJson = "[\"Adult Member\"]",
      expectedOutput = "Adult Member",
      hintsJson = "[\"Check outer condition first, then inner condition.\"]",
      topic = "Conditions"
    ),
    ExerciseEntity(
      id = "ex_w2_l6_2",
      lessonId = "py_w2_l6",
      orderIndex = 2,
      type = ExerciseType.FIND_BUG,
      prompt = "Which line causes an IndentationError in nested conditions?\n\n1: if active:\n2:     if admin:\n3:     print(\"Admin\")",
      explanation = "Line 3 must be indented 8 spaces (4 spaces deeper than line 2) to be inside the inner if statement.",
      optionsJson = "[\"Line 3: Must be indented 8 spaces to be inside inner if\", \"Line 1: Missing condition\", \"Line 2: Cannot nest if statements in Python\", \"Line 3: print syntax error\"]",
      correctAnswersJson = "[\"Line 3: Must be indented 8 spaces to be inside inner if\"]",
      hintsJson = "[\"Nested blocks need deeper indentation.\"]",
      topic = "Conditions"
    ),

    // ==========================================
    // py_w2_l7: LOGIC CHALLENGE (2 exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l7_1",
      lessonId = "py_w2_l7",
      orderIndex = 1,
      type = ExerciseType.PREDICT_OUTPUT,
      prompt = "Evaluate the compound condition:\n\nx = 10\ny = 20\nif (x == 10 and y < 15) or (x > 5 and y == 20):\n    print(\"Unlocked\")\nelse:\n    print(\"Locked\")",
      explanation = "(x == 10 and y < 15) is False. (x > 5 and y == 20) is True. False or True is True -> 'Unlocked'.",
      optionsJson = "[\"Unlocked\", \"Locked\", \"Error\", \"None\"]",
      correctAnswersJson = "[\"Unlocked\"]",
      expectedOutput = "Unlocked",
      hintsJson = "[\"Evaluate each parenthesized group separately, then combine with 'or'.\"]",
      topic = "Booleans"
    ),
    ExerciseEntity(
      id = "ex_w2_l7_2",
      lessonId = "py_w2_l7",
      orderIndex = 2,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Complete the check so access is granted if age is between 13 and 19 (inclusive):",
      explanation = "Use 'and' to ensure both bounds are satisfied.",
      starterCode = "is_teen = (age >= 13 ___ age <= 19)",
      solutionCode = "is_teen = (age >= 13 and age <= 19)",
      optionsJson = "[\"and\", \"or\", \"not\", \"&&\"]",
      correctAnswersJson = "[\"and\"]",
      hintsJson = "[\"Both conditions must be True simultaneously.\"]",
      topic = "Booleans"
    ),

    // ==========================================
    // py_w2_l8: BOSS: Gatekeeper of Logic (2 boss exercises)
    // ==========================================
    ExerciseEntity(
      id = "ex_w2_l8_1",
      lessonId = "py_w2_l8",
      orderIndex = 1,
      type = ExerciseType.BOSS_CHALLENGE,
      prompt = "GATEKEEPER PHASE 1: Crack the authentication gatekeeper code:\n\nclearance = 4\noverride = True\nif clearance >= 5 or override:\n    print(\"GATE OPEN\")\nelse:\n    print(\"DENIED\")",
      explanation = "clearance >= 5 is False, but override is True. False or True evaluates to True -> 'GATE OPEN'.",
      optionsJson = "[\"GATE OPEN\", \"DENIED\", \"Error\", \"None\"]",
      correctAnswersJson = "[\"GATE OPEN\"]",
      expectedOutput = "GATE OPEN",
      hintsJson = "[\"The 'or' operator evaluates to True if override is True.\"]",
      topic = "Boss"
    ),
    ExerciseEntity(
      id = "ex_w2_l8_2",
      lessonId = "py_w2_l8",
      orderIndex = 2,
      type = ExerciseType.COMPLETE_CODE,
      prompt = "GATEKEEPER FINAL: Complete the ultimate security clearance protocol:",
      explanation = "Call print(\"WORLD 3 UNLOCKED\")",
      starterCode = "___(\"WORLD 3 UNLOCKED\")",
      solutionCode = "print(\"WORLD 3 UNLOCKED\")",
      optionsJson = "[\"print\", \"unlock\", \"teleport\", \"access\"]",
      correctAnswersJson = "[\"print\"]",
      hintsJson = "[\"Output the final unlock string using print().\"]",
      topic = "Boss"
    )
  )

  fun generateDynamicLessons(): List<LessonEntity> {
    val list = mutableListOf<LessonEntity>()
    
    // WORLD 1 expansion lessons (Lessons 11-16)
    val w1Expansion = listOf(
      "Arithmetic Operators" to "Basic math operators: +, -, *, /, //, %",
      "Comparison Operators" to "Comparing values using ==, !=, <, >, <=, >=",
      "User Input" to "Receiving inputs dynamically using the input() function",
      "Type Conversion" to "Converting values using int(), float(), str()",
      "String Operations" to "Concatenating, repeating, and formatting strings",
      "Basic Debugging" to "Identifying and resolving syntax and name errors"
    )
    w1Expansion.forEachIndexed { i, (title, desc) ->
      val num = 11 + i
      list.add(LessonEntity(
        id = "py_w1_l$num",
        worldId = "py_w1",
        chapterId = "py_w1_c4",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 50,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nx = 5\ny = 10",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 2 expansion lessons (Lessons 9-14)
    val w2Expansion = listOf(
      "range() Function" to "Generating numeric sequences with range()",
      "Loop Control" to "Controlling loops with flags and condition variables",
      "break Statement" to "Exiting loops early with the break keyword",
      "continue Statement" to "Skipping current loop iterations with continue",
      "Nested Loops" to "Placing loops inside other loops for grid iteration",
      "Common Loop Errors" to "Avoiding infinite loops and off-by-one index mistakes"
    )
    w2Expansion.forEachIndexed { i, (title, desc) ->
      val num = 9 + i
      list.add(LessonEntity(
        id = "py_w2_l$num",
        worldId = "py_w2",
        chapterId = "py_w2_c2",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 60,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nfor i in range(5):\n    pass",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 3: Data Structures (12 lessons)
    val w3Lessons = listOf(
      "Introduction to Lists" to "Creating arrays of values in Python",
      "List Indexing" to "Accessing items using positive and negative indices",
      "List Slicing" to "Extracting sublists using start:stop:step notation",
      "List Methods" to "Appending, removing, and sorting list elements",
      "Tuples" to "Using immutable ordered collections for data integrity",
      "Sets" to "Unordered collections with unique elements and math ops",
      "Dictionaries" to "Creating key-value mappings for rapid search",
      "Dictionary Methods" to "Retrieving keys, values, and updates with methods",
      "Nested Data Structures" to "Combining lists and dictionaries in complex configurations",
      "Iterating Through Collections" to "Using loops to process each item in a data structure",
      "Searching Collections" to "Finding items and checking existence with 'in'",
      "Basic Data Processing" to "Transforming lists with filters, sums, and averages"
    )
    w3Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w3_l$num",
        worldId = "py_w3",
        chapterId = "py_w3_c${if (num <= 6) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 70,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 12) LessonType.CHALLENGE else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nitems = [1, 2, 3]",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 4: Functions (12 lessons)
    val w4Lessons = listOf(
      "Why Functions?" to "DRY principle and functional abstraction",
      "Defining Functions" to "Using def and defining custom procedures",
      "Parameters" to "Passing inputs into custom actions",
      "Arguments" to "Positional and naming arguments",
      "Return Values" to "Returning results using the return statement",
      "Default Parameters" to "Setting fallback parameters for flexibility",
      "Keyword Arguments" to "Explicitly referencing parameter names on call",
      "Variable Scope" to "Understanding local vs global variable reach",
      "Local vs Global Variables" to "Avoiding scope collision in your scripts",
      "Function Composition" to "Using output of one function as input to another",
      "Reusable Code" to "Building structured libraries of custom functions",
      "Common Function Mistakes" to "Resolving UnboundLocalError and return mistakes"
    )
    w4Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w4_l$num",
        worldId = "py_w4",
        chapterId = "py_w4_c${if (num <= 6) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 80,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 12) LessonType.BOSS else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\ndef func():\n    pass",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 5: OOP (13 lessons)
    val w5Lessons = listOf(
      "What is OOP?" to "Object-oriented paradigm vs procedural coding",
      "Classes" to "Defining blueprints with the class keyword",
      "Objects" to "Instantiating real-world items from blueprints",
      "Attributes" to "Storing properties inside object instances",
      "Methods" to "Adding functions inside custom classes",
      "__init__ Constructor" to "Initializing state dynamically upon creation",
      "Instance Variables" to "Working with self. variable bindings",
      "Class Variables" to "Creating static/shared state across all instances",
      "Inheritance" to "Deriving children subclasses from parent classes",
      "Method Overriding" to "Customizing child class behavior specifically",
      "Encapsulation" to "Restricting access with private underscore prefixes",
      "Polymorphism" to "Processing diverse class types with a single interface",
      "When to Use OOP" to "Synthesizing OOP design patterns in actual code"
    )
    w5Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w5_l$num",
        worldId = "py_w5",
        chapterId = "py_w5_c${if (num <= 7) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 90,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 13) LessonType.CHALLENGE else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nclass MyClass:\n    pass",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 6: Files & Exceptions (12 lessons)
    val w6Lessons = listOf(
      "Reading Files" to "Using open() and read() to ingest plain text",
      "Writing Files" to "Using write() and append() modes safely",
      "File Paths" to "Locating files with relative and absolute paths",
      "with Statements" to "Auto-closing resource file streams cleanly",
      "CSV Basics" to "Parsing comma-separated spreadsheet data structures",
      "JSON Basics" to "Converting structured configurations with json.loads",
      "Introduction to Exceptions" to "Handling unexpected crashes in scripts",
      "try-except Blocks" to "Enclosing risky execution logic safely",
      "Handling Specific Errors" to "Catching ValueError, ZeroDivisionError, FileNotFoundError",
      "finally Clause" to "Enforcing final cleanup execution in try blocks",
      "Raising Exceptions" to "Manually throwing assertions using raise",
      "Designing Robust Programs" to "Formulating crash-proof scripts for production"
    )
    w6Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w6_l$num",
        worldId = "py_w6",
        chapterId = "py_w6_c${if (num <= 6) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 100,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 12) LessonType.BOSS else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\ntry:\n    pass\nexcept:\n    pass",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 7: Algorithmic Thinking (13 lessons)
    val w7Lessons = listOf(
      "What is an Algorithm?" to "Step-by-step logic rules to solve problems",
      "Problem Decomposition" to "Breaking complex constraints into simple steps",
      "Writing Pseudocode" to "Drafting algorithms prior to actual coding",
      "Linear Search" to "Scanning sequential elements to locate matches",
      "Binary Search Concepts" to "Understanding fast search in ordered lists",
      "Finding Min/Max" to "Formulating loops to extract bounds from sequences",
      "Accumulation & Counting" to "Tallying occurrences of characteristics",
      "Frequency Mapping" to "Counting item frequencies with dictionaries",
      "Bubble Sort" to "Understanding bubble exchange sort step-by-step",
      "Selection Sort" to "Selecting boundaries and exchanging in lists",
      "Searching vs Sorting" to "Balancing preprocessing with search efficiency",
      "Big-O Introduction" to "Measuring algorithmic performance scaling",
      "Efficiency Intuition" to "Comparing speed of linear and quadratic algorithms"
    )
    w7Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w7_l$num",
        worldId = "py_w7",
        chapterId = "py_w7_c${if (num <= 6) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 110,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 13) LessonType.CHALLENGE else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\ndef search(arr, target):\n    return False",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 8: Advanced Python (16 lessons)
    val w8Lessons = listOf(
      "List Comprehensions" to "Creating transformed lists in a single clean statement",
      "Dictionary Comprehensions" to "Shorthand mapping conversions in Python",
      "Lambda Functions" to "Writing anonymous inline functions dynamically",
      "map() Function" to "Applying procedures over collections sequentially",
      "filter() Function" to "Extracting elements matching predicates",
      "enumerate() Function" to "Obtaining both list index and value in loops",
      "zip() Function" to "Combining multiple lists pairwise in lockstep",
      "Iterators" to "How Python iterates over collections internally",
      "Generators" to "Creating memory-efficient streams using yield",
      "Decorators Intro" to "Wrapping other functions to extend behavior",
      "Python Modules" to "Importing and calling individual script libraries",
      "Packages" to "Structuring directories with init files",
      "Virtual Environments" to "Isolating project dependencies safely",
      "Type Hints" to "Specifying expected variable types statically",
      "Dataclasses" to "Using @dataclass to auto-generate helper methods",
      "Advanced Debugging" to "Inspecting runtime state using pdb and tracebacks"
    )
    w8Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w8_l$num",
        worldId = "py_w8",
        chapterId = "py_w8_c${if (num <= 8) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 120,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 16) LessonType.BOSS else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\n[x for x in range(10)]",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 9: Practical Python (14 lessons)
    val w9Lessons = listOf(
      "Working with HTTP" to "Understanding network client requests",
      "Python requests Module" to "Performing get and post calls to APIs",
      "JSON Parsing" to "Extracting nested keys from web endpoints",
      "API Credentials" to "Securing keys using Environment Variables",
      "Logging Module" to "Using logging.info instead of plain prints",
      "Logging Levels" to "Structuring debug, info, warning, and error logs",
      "Configuration Files" to "Reading ini, yaml, or env setups",
      "Project Structures" to "Standard layouts for professional python apps",
      "Dependency Management" to "Managing pip, requirements.txt, and setups",
      "Unit Testing Introduction" to "Verifying functional correctness with assert",
      "unittest Framework" to "Writing automated test suites in Python",
      "Writing Clean Code" to "PEP 8 standards, docstrings, and clean design",
      "Security Essentials" to "Preventing script injection and leak hazards",
      "Practical Climax" to "Bringing everything together in real applications"
    )
    w9Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w9_l$num",
        worldId = "py_w9",
        chapterId = "py_w9_c${if (num <= 7) 1 else 2}",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 130,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 14) LessonType.CHALLENGE else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nimport logging\nlogging.basicConfig()",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    // WORLD 10: Capstone Projects (5 lessons)
    val w10Lessons = listOf(
      "Personal Expense Tracker" to "Capstone: Track personal spendings with files",
      "Interactive Quiz App" to "Capstone: Multi-category testing quiz game",
      "Student Management System" to "Capstone: OOP database system for rosters",
      "Text-Based Adventure Game" to "Capstone: High branching interactive choice game",
      "Final Python Graduation" to "Capstone: Standard final graduation review"
    )
    w10Lessons.forEachIndexed { i, (title, desc) ->
      val num = i + 1
      list.add(LessonEntity(
        id = "py_w10_l$num",
        worldId = "py_w10",
        chapterId = "py_w10_c1",
        lessonNumber = num,
        title = title,
        description = desc,
        xpReward = 200,
        isUnlocked = false,
        isCompleted = false,
        starsEarned = 0,
        lessonType = if (num == 5) LessonType.BOSS else LessonType.LESSON,
        conceptSummary = "Master ${title} in Python.",
        conceptSnippet = "# Dynamic snippet for ${title}\nprint('Capstone graduation ready!')",
        conceptExplanation = "Understanding ${title} is key to programming."
      ))
    }

    return list
  }

  fun generateDynamicExercises(): List<ExerciseEntity> {
    val list = mutableListOf<ExerciseEntity>()
    val dynamicLessons = generateDynamicLessons()
    dynamicLessons.forEach { lesson ->
      val lid = lesson.id
      val title = lesson.title
      
      list.add(ExerciseEntity(
        id = "ex_${lid}_1",
        lessonId = lid,
        orderIndex = 1,
        type = ExerciseType.MULTIPLE_CHOICE,
        prompt = "What is the primary objective when studying '${title}' in Python?",
        explanation = "The goal of '${title}' is to build a core practical understanding of Python programming concepts.",
        optionsJson = "[\"To understand how ${title} works in Python applications\", \"To skip learning core fundamentals\", \"To run code without checking syntax errors\", \"To print hello world on repeat\"]",
        correctAnswersJson = "[\"To understand how ${title} works in Python applications\"]",
        hintsJson = "[\"Focus on the main programmatic purpose of ${title}.\", \"It builds functional understanding of standard Python design.\"]",
        topic = "General Syntax"
      ))
      
      list.add(ExerciseEntity(
        id = "ex_${lid}_2",
        lessonId = lid,
        orderIndex = 2,
        type = ExerciseType.TRUE_FALSE,
        prompt = "Studying and implementing '${title}' is essential for mastering Python software engineering.",
        explanation = "True. Every fundamental block is critical to avoid logical mistakes and build secure applications.",
        optionsJson = "[\"True\", \"False\"]",
        correctAnswersJson = "[\"True\"]",
        hintsJson = "[\"Does a Python developer need to understand this?\", \"Yes, it is highly useful.\"]",
        topic = "General Logic"
      ))
    }
    return list
  }
}
