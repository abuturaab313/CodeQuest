package com.example.data.local

import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

object JavaScriptCurriculum {

  fun getCourse(): CourseEntity = CourseEntity(
    id = "javascript",
    title = "JavaScript Web Engine",
    description = "Master modern JavaScript from foundational syntax to interactive DOM dynamics and web apps.",
    language = "javascript",
    totalWorlds = 5,
    iconName = "javascript",
    orderIndex = 2,
    isAvailable = true,
    estimatedHours = 18,
    difficulty = "Beginner"
  )

  fun getWorlds(): List<WorldEntity> = listOf(
    WorldEntity(
      id = "js_w1",
      courseId = "javascript",
      worldNumber = 1,
      title = "JS Foundations",
      subtitle = "console.log, variables (let, const), data types, strings, numbers, booleans, operators, and basic debugging",
      themeColorHex = "#F59E0B",
      iconName = "terminal",
      requiredXp = 0,
      isUnlocked = true,
      topicsJson = "[\"console.log()\", \"let / const / var\", \"Primitives\", \"String Templates\", \"Number Operations\", \"Type Coercion\", \"Comparisons\", \"Debugging\"]"
    ),
    WorldEntity(
      id = "js_w2",
      courseId = "javascript",
      worldNumber = 2,
      title = "Logic & Loops",
      subtitle = "if/else, switch, ternary, while, for loops, break, continue, and Logic Guardian boss",
      themeColorHex = "#D97706",
      iconName = "device_hub",
      requiredXp = 150,
      isUnlocked = true,
      topicsJson = "[\"if / else / else if\", \"Strict Equality (===)\", \"Logical Operators\", \"Ternary Operator\", \"for / while Loops\", \"break / continue\", \"Logic Guardian Boss\"]"
    ),
    WorldEntity(
      id = "js_w3",
      courseId = "javascript",
      worldNumber = 3,
      title = "Arrays & Objects",
      subtitle = "Arrays, push/pop/slice, object literals, properties, methods, destructuring, and Object Overlord boss",
      themeColorHex = "#B45309",
      iconName = "data_object",
      requiredXp = 300,
      isUnlocked = true,
      topicsJson = "[\"Arrays\", \"Array Methods (push, pop, shift, unshift)\", \"Object Literals\", \"Dot vs Bracket Notation\", \"Object Methods\", \"Destructuring\", \"Object Overlord Boss\"]"
    ),
    WorldEntity(
      id = "js_w4",
      courseId = "javascript",
      worldNumber = 4,
      title = "Functions & Scope",
      subtitle = "Function declarations, parameters, return, arrow functions, scope, closures, array methods (map, filter, reduce)",
      themeColorHex = "#9A3412",
      iconName = "code",
      requiredXp = 500,
      isUnlocked = true,
      topicsJson = "[\"Function Declarations\", \"Arrow Functions (=>)\", \"Default Parameters\", \"Scope & Closures\", \"Higher-Order Functions\", \"map / filter / reduce\", \"Function Wizard Boss\"]"
    ),
    WorldEntity(
      id = "js_w5",
      courseId = "javascript",
      worldNumber = 5,
      title = "DOM & Interactive Web",
      subtitle = "DOM elements, querySelector, event listeners, buttons, forms, and validation",
      themeColorHex = "#92400E",
      iconName = "web",
      requiredXp = 700,
      isUnlocked = true,
      topicsJson = "[\"DOM Tree\", \"Selecting Elements\", \"Text Content\", \"Events\", \"Buttons\", \"Forms\", \"Validation\", \"DOM Projects\"]"
    )
  )

  fun getChapters(): List<ChapterEntity> = listOf(
    // World 1
    ChapterEntity("js_w1_c1", "js_w1", 1, "Welcome to JS", "Discover the web's universal programming language."),
    ChapterEntity("js_w1_c2", "js_w1", 2, "Variables & Types", "Store numbers, strings, and booleans in memory."),
    ChapterEntity("js_w1_c3", "js_w1", 3, "Decisions & Conditions", "Make dynamic branch decisions with if/else."),

    // World 2
    ChapterEntity("js_w2_c1", "js_w2", 1, "Conditional Branches", "Master complex if/else if logic trees."),
    ChapterEntity("js_w2_c2", "js_w2", 2, "Loops & Iteration", "Repeat tasks with for and while loops."),

    // World 3
    ChapterEntity("js_w3_c1", "js_w3", 1, "Working with Arrays", "Store ordered lists of items."),
    ChapterEntity("js_w3_c2", "js_w3", 2, "JavaScript Objects", "Model real-world entities with key-value pairs."),

    // World 4
    ChapterEntity("js_w4_c1", "js_w4", 1, "Function Basics", "Write reusable modular blocks."),
    ChapterEntity("js_w4_c2", "js_w4", 2, "Modern Arrow Functions", "Concise syntax and functional style."),

    // World 5
    ChapterEntity("js_w5_c1", "js_w5", 1, "DOM Selection & Manipulation", "Connect code to the HTML tree."),
    ChapterEntity("js_w5_c2", "js_w5", 2, "Events & Interactive UI", "Handle clicks, inputs, and form submissions.")
  )

  fun getLessons(): List<LessonEntity> = listOf(
    // World 1 Lessons
    LessonEntity("js_w1_l1", "js_w1_c1", "js_w1", 1, "What is JavaScript?", "The language that powers the interactive web.", LessonType.LESSON, 35, 10, true, false, 0, "JS Intro", "console.log(\"Hello JS\");", "JavaScript is the standard language of the interactive web.", 3),
    LessonEntity("js_w1_l2", "js_w1_c1", "js_w1", 2, "console.log()", "Printing output to the developer console.", LessonType.LESSON, 35, 10, true, false, 0, "Console Output", "console.log(\"Output\");", "Use console.log() to inspect output in JavaScript.", 3),
    LessonEntity("js_w1_l3", "js_w1_c2", "js_w1", 3, "Variables", "Storing data with named identifiers.", LessonType.LESSON, 40, 10, true, false, 0, "JS Variables", "let score = 100;", "Declare variables with let and const in modern JavaScript.", 3),
    LessonEntity("js_w1_l4", "js_w1_c2", "js_w1", 4, "let", "Declaring reassignable block-scoped variables.", LessonType.LESSON, 40, 10, true, false, 0, "let Keyword", "let count = 0; count++;", "let provides reassignable block-scoped binding.", 3),
    LessonEntity("js_w1_l5", "js_w1_c2", "js_w1", 5, "const", "Immutable variable bindings for safety.", LessonType.LESSON, 40, 10, true, false, 0, "const Keyword", "const PI = 3.14159;", "const prevents variable reassignment.", 3),
    LessonEntity("js_w1_l6", "js_w1_c2", "js_w1", 6, "Data Types", "Primitives: string, number, boolean, null, undefined.", LessonType.LESSON, 45, 15, true, false, 0, "Primitives", "typeof \"hello\" // 'string'", "Fundamental JS primitives.", 3),
    LessonEntity("js_w1_l7", "js_w1_c2", "js_w1", 7, "Strings", "String literals, quotes, and concatenation.", LessonType.LESSON, 45, 15, true, false, 0, "String Templates", "`Hello \${name}`", "Template literals embed expressions cleanly.", 3),
    LessonEntity("js_w1_l8", "js_w1_c2", "js_w1", 8, "Numbers", "Arithmetic operators, integers, and floats.", LessonType.LESSON, 45, 15, true, false, 0, "Numbers", "let total = price * quantity;", "Standard arithmetic in JavaScript.", 3),
    LessonEntity("js_w1_l9", "js_w1_c2", "js_w1", 9, "Booleans", "True and false logical values.", LessonType.LESSON, 40, 10, true, false, 0, "Booleans", "let isActive = true;", "Boolean truth flags.", 3),
    LessonEntity("js_w1_l10", "js_w1_c2", "js_w1", 10, "Operators", "Comparison and assignment operators.", LessonType.LESSON, 45, 15, true, false, 0, "Strict Equality", "x === 5", "Always prefer === over == to avoid type coercion bugs.", 3),
    LessonEntity("js_w1_l11", "js_w1_c3", "js_w1", 11, "Input Concepts", "Receiving user input safely in JS.", LessonType.LESSON, 45, 15, true, false, 0, "Input Handling", "prompt(\"Enter value\");", "User input retrieval.", 3),
    LessonEntity("js_w1_l12", "js_w1_c3", "js_w1", 12, "Conditions", "Basic if branching.", LessonType.LESSON, 50, 15, true, false, 0, "Conditionals", "if (score > 50) {}", "Branching on boolean evaluations.", 4),
    LessonEntity("js_w1_l13", "js_w1_c3", "js_w1", 13, "Basic Debugging", "Spotting ReferenceErrors and SyntaxErrors.", LessonType.BOSS, 80, 30, true, false, 0, "Debugging Boss", "// Fix the bug\nconst msg = 'Debugged!';", "Defeat the syntax errors to complete World 1.", 5),

    // World 2 Lessons
    LessonEntity("js_w2_l1", "js_w2_c1", "js_w2", 1, "if Statements", "Executing code conditionally.", LessonType.LESSON, 45, 15, true, false, 0, "if Blocks", "if (condition) {}", "Conditional branching.", 4),
    LessonEntity("js_w2_l2", "js_w2_c1", "js_w2", 2, "else", "Handling the fallback path.", LessonType.LESSON, 45, 15, true, false, 0, "else Blocks", "else { fallback(); }", "Default execution branch.", 4),
    LessonEntity("js_w2_l3", "js_w2_c1", "js_w2", 3, "else if", "Multi-branch decision chains.", LessonType.LESSON, 50, 15, true, false, 0, "else if", "else if (x > 10) {}", "Multi-tier decision trees.", 4),
    LessonEntity("js_w2_l4", "js_w2_c1", "js_w2", 4, "Logical Operators", "&& (AND), || (OR), and ! (NOT).", LessonType.LESSON, 50, 15, true, false, 0, "Logic Gates", "if (a && b) {}", "Composite logic expressions.", 4),
    LessonEntity("js_w2_l5", "js_w2_c2", "js_w2", 5, "for Loops", "Standard indexed iteration loop.", LessonType.LESSON, 55, 20, true, false, 0, "for Loops", "for (let i = 0; i < 5; i++) {}", "Index-based iteration.", 4),
    LessonEntity("js_w2_l6", "js_w2_c2", "js_w2", 6, "while Loops", "Repeating based on a boolean condition.", LessonType.LESSON, 55, 20, true, false, 0, "while Loops", "while (lives > 0) {}", "Conditional repetition.", 4),
    LessonEntity("js_w2_l7", "js_w2_c2", "js_w2", 7, "break", "Early termination of loops.", LessonType.LESSON, 45, 15, true, false, 0, "break Statement", "if (done) break;", "Exiting iteration loops early.", 4),
    LessonEntity("js_w2_l8", "js_w2_c2", "js_w2", 8, "continue", "Skipping to the next iteration.", LessonType.LESSON, 45, 15, true, false, 0, "continue Statement", "if (skip) continue;", "Bypassing remaining loop body.", 4),
    LessonEntity("js_w2_l9", "js_w2_c2", "js_w2", 9, "Nested Loops", "Grids and multi-dimensional iteration.", LessonType.BOSS, 90, 35, true, false, 0, "Nested Loops Boss", "// Grid builder\nfor(let r=0;r<3;r++) for(let c=0;c<3;c++) {}", "Conquer the Logic Guardian.", 5),

    // World 3 Lessons
    LessonEntity("js_w3_l1", "js_w3_c1", "js_w3", 1, "Arrays", "Creating ordered arrays.", LessonType.LESSON, 50, 15, true, false, 0, "JS Arrays", "const items = ['apple', 'banana'];", "Zero-indexed list collections.", 4),
    LessonEntity("js_w3_l2", "js_w3_c1", "js_w3", 2, "Array Methods", "push, pop, shift, and unshift.", LessonType.LESSON, 55, 20, true, false, 0, "Array Methods", "items.push('orange');", "Mutating and accessing array buffers.", 4),
    LessonEntity("js_w3_l3", "js_w3_c2", "js_w3", 3, "JS Objects", "Key-value dictionaries in JavaScript.", LessonType.LESSON, 60, 20, true, false, 0, "Objects", "const user = { name: 'Alex', level: 5 };", "Record and entity structures.", 4),
    LessonEntity("js_w3_l4", "js_w3_c2", "js_w3", 4, "Object Overlord Boss", "Conquer complex object schemas.", LessonType.BOSS, 100, 40, true, false, 0, "Object Boss", "user.skills.push('Coding');", "Conquer the Object Overlord.", 5),

    // World 4 Lessons
    LessonEntity("js_w4_l1", "js_w4_c1", "js_w4", 1, "Functions", "Declaring and calling reusable blocks.", LessonType.LESSON, 55, 20, true, false, 0, "Functions", "function add(a, b) { return a + b; }", "Modular subroutines.", 4),
    LessonEntity("js_w4_l2", "js_w4_c2", "js_w4", 2, "Arrow Functions", "ES6 arrow syntax and implicit return.", LessonType.LESSON, 60, 20, true, false, 0, "Arrow Syntax", "const multiply = (x, y) => x * y;", "Concise functional arrow notation.", 4),
    LessonEntity("js_w4_l3", "js_w4_c2", "js_w4", 3, "Higher Order Methods", "map, filter, and reduce.", LessonType.LESSON, 65, 25, true, false, 0, "map/filter", "nums.map(n => n * 2);", "Functional transformations.", 4),
    LessonEntity("js_w4_l4", "js_w4_c2", "js_w4", 4, "Function Wizard Boss", "Conquer closures and lambdas.", LessonType.BOSS, 110, 45, true, false, 0, "Wizard Battle", "const pipeline = nums.filter(x => x > 0).map(x => x * 2);", "Conquer the Function Wizard.", 5),

    // World 5 Lessons
    LessonEntity("js_w5_l1", "js_w5_c1", "js_w5", 1, "DOM Tree", "Understanding web page hierarchy.", LessonType.LESSON, 60, 20, true, false, 0, "DOM Tree", "document.body", "Browser document model.", 4),
    LessonEntity("js_w5_l2", "js_w5_c1", "js_w5", 2, "Selecting Elements", "querySelector and getElementById.", LessonType.LESSON, 60, 20, true, false, 0, "querySelector", "const btn = document.querySelector('#btn');", "Locating HTML elements.", 4),
    LessonEntity("js_w5_l3", "js_w5_c1", "js_w5", 3, "Changing Text", "textContent and innerHTML updates.", LessonType.LESSON, 65, 20, true, false, 0, "textContent", "btn.textContent = 'Clicked!';", "Updating DOM node values.", 4),
    LessonEntity("js_w5_l4", "js_w5_c2", "js_w5", 4, "Events", "addEventListener and event types.", LessonType.LESSON, 65, 25, true, false, 0, "Events", "btn.addEventListener('click', () => {});", "Reactive event dispatchers.", 4),
    LessonEntity("js_w5_l5", "js_w5_c2", "js_w5", 5, "DOM Guardian Boss", "Conquer the DOM Guardian Boss Challenge!", LessonType.BOSS, 120, 50, true, false, 0, "DOM Guardian", "form.addEventListener('submit', validate);", "Defeat the DOM Guardian.", 6)
  )

  fun getExercises(): List<ExerciseEntity> = listOf(
    ExerciseEntity(
      id = "js_ex_1",
      lessonId = "js_w1_l2",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which statement in JavaScript prints a message to the console?",
      explanation = "In JavaScript, console.log() is used to output text and data to the console.",
      optionsJson = "[\"console.log('Hello');\", \"print('Hello');\", \"echo 'Hello';\", \"System.out.println('Hello');\"]",
      correctAnswersJson = "[\"console.log('Hello');\"]",
      hintsJson = "[\"Look for the console object and log method.\"]",
      topic = "console.log"
    ),
    ExerciseEntity(
      id = "js_ex_2",
      lessonId = "js_w1_l4",
      orderIndex = 1,
      type = ExerciseType.FILL_IN_BLANK,
      prompt = "Complete the declaration to define a reassignable variable 'score' with initial value 10:",
      starterCode = "___ score = 10;\nscore = 15;",
      solutionCode = "let score = 10;\nscore = 15;",
      explanation = "'let' creates a block-scoped variable that can be reassigned.",
      optionsJson = "[\"let\", \"const\", \"fixed\", \"val\"]",
      correctAnswersJson = "[\"let\"]",
      hintsJson = "[\"Use 'let' for reassignable variables.\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "js_ex_3",
      lessonId = "js_w1_l5",
      orderIndex = 1,
      type = ExerciseType.TRUE_FALSE,
      prompt = "Variables declared with 'const' can be reassigned later in the program.",
      explanation = "'const' variables cannot be reassigned to a new value after initialization.",
      optionsJson = "[\"True\", \"False\"]",
      correctAnswersJson = "[\"False\"]",
      hintsJson = "[\"Think about what 'const' stands for (constant).\"]",
      topic = "Variables"
    ),
    ExerciseEntity(
      id = "js_ex_4",
      lessonId = "js_w4_l2",
      orderIndex = 1,
      type = ExerciseType.MULTIPLE_CHOICE,
      prompt = "Which of the following is a valid ES6 arrow function that doubles a number?",
      explanation = "ES6 arrow functions use the => fat arrow syntax: (x) => x * 2.",
      optionsJson = "[\"const double = (x) => x * 2;\", \"function double(x) -> x * 2;\", \"arrow double = x * 2;\", \"def double(x) => x * 2;\"]",
      correctAnswersJson = "[\"const double = (x) => x * 2;\"]",
      hintsJson = "[\"Look for the fat arrow => symbol.\"]",
      topic = "Functions"
    )
  )
}
