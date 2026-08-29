package com.example.data.local

import com.example.data.models.ProjectDifficulty
import com.example.data.models.ProjectEntity
import org.json.JSONArray
import org.json.JSONObject

object ProjectCurriculum {

  fun getProjects(): List<ProjectEntity> {
    return listOf(
      createCalculatorProject(),
      createGuessingGameProject(),
      createQuizGameProject(),
      createUnitConverterProject(),
      createTodoListProject(),
      createGreetingGeneratorProject(),
      createGradeManagerProject(),
      createBankAccountProject(),
      createContactSearchProject(),
      createDataProcessorProject(),
      createApiDashboardProject(),
      createExpenseTrackerProject(),
      createQuizAppProject(),
      createStudentSystemProject(),
      createAdventureGameProject(),
      createFinalCapstoneProject()
    )
  }

  private fun createCalculatorProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", """# ==========================================
# Project 1: Modular Python Calculator
# ==========================================
# Instructions:
# 1. Implement operations in calculator_ops.py
# 2. Complete the user interaction loop below
# 3. Format output cleanly: "Result: <value>"

from calculator_ops import add, subtract, multiply, divide

def run_calculator():
    print("=== Python Calculator CLI ===")
    
    # Read operation and numbers
    op = input("Choose operation (+, -, *, /): ").strip()
    num1_str = input("Enter first number: ").strip()
    num2_str = input("Enter second number: ").strip()
    
    num1 = float(num1_str)
    num2 = float(num2_str)
    
    if op == '+':
        result = add(num1, num2)
        print("Result:", result)
    elif op == '-':
        result = subtract(num1, num2)
        print("Result:", result)
    elif op == '*':
        result = multiply(num1, num2)
        print("Result:", result)
    elif op == '/':
        result = divide(num1, num2)
        print("Result:", result)
    else:
        print("Error: Invalid operation")

if __name__ == "__main__":
    run_calculator()
""")
      put("calculator_ops.py", """# Mathematical operations for the calculator

def add(a, b):
    return a + b

def subtract(a, b):
    return a - b

def multiply(a, b):
    return a * b

def divide(a, b):
    if b == 0:
        return "Error: Division by zero"
    return a / b
""")
      put("README.md", """# Project: Python Calculator CLI

## Overview
Build a modular command-line calculator that takes user operations and operands, delegates arithmetic computation to a dedicated helper module (`calculator_ops.py`), and gracefully handles edge cases like division by zero.

## Requirements
1. **Addition (`+`)**: Add `a` and `b`.
2. **Subtraction (`-`)**: Subtract `b` from `a`.
3. **Multiplication (`*`)**: Multiply `a` and `b`.
4. **Division (`/`)**: Return the quotient, or `"Error: Division by zero"` if denominator is 0.
5. **Main Menu**: Read operation, then first number, then second number. Print `"Result: <value>"`.

## Rewards
- **250 XP**
- **50 CodeCoins**
- **Calculator Builder Badge**
""")
    }

    val tasks = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "task_calc_1")
        put("title", "Implement Addition & Subtraction")
        put("description", "Define add(a, b) and subtract(a, b) in calculator_ops.py returning the numeric sum and difference.")
        put("checkpoint", 1)
        put("xpReward", 40)
        put("hint", "Use standard + and - operators in calculator_ops.py")
        put("testIds", JSONArray().apply { put("test_calc_add"); put("test_calc_sub") })
      })
      put(JSONObject().apply {
        put("id", "task_calc_2")
        put("title", "Implement Multiplication & Safe Division")
        put("description", "Define multiply(a, b) and divide(a, b). Ensure divide returns 'Error: Division by zero' when denominator is 0.")
        put("checkpoint", 2)
        put("xpReward", 50)
        put("hint", "Check if b == 0 before dividing: if b == 0: return 'Error: Division by zero'")
        put("testIds", JSONArray().apply { put("test_calc_mul"); put("test_calc_div"); put("test_calc_div_zero") })
      })
      put(JSONObject().apply {
        put("id", "task_calc_3")
        put("title", "Connect CLI Interface")
        put("description", "Import functions into main.py and print formatted output 'Result: <value>' for calculations.")
        put("checkpoint", 3)
        put("xpReward", 60)
        put("hint", "Prompt with input() in order: operation, num1, num2. Then print 'Result:', result")
        put("testIds", JSONArray().apply { put("test_calc_full_add"); put("test_calc_full_div_zero") })
      })
    }

    val tests = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "test_calc_add")
        put("taskId", "task_calc_1")
        put("title", "Addition CLI Test (15 + 27)")
        put("input", "+\n15\n27\n")
        put("expectedOutput", "Result: 42")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_calc_sub")
        put("taskId", "task_calc_1")
        put("title", "Subtraction CLI Test (50 - 18)")
        put("input", "-\n50\n18\n")
        put("expectedOutput", "Result: 32")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_calc_mul")
        put("taskId", "task_calc_2")
        put("title", "Multiplication CLI Test (7 * 8)")
        put("input", "*\n7\n8\n")
        put("expectedOutput", "Result: 56")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_calc_div")
        put("taskId", "task_calc_2")
        put("title", "Division CLI Test (84 / 4)")
        put("input", "/\n84\n4\n")
        put("expectedOutput", "Result: 21")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_calc_div_zero")
        put("taskId", "task_calc_2")
        put("title", "Division by Zero Protection")
        put("input", "/\n100\n0\n")
        put("expectedOutput", "Division by zero")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_calc_full_add")
        put("taskId", "task_calc_3")
        put("title", "Large Operand Addition")
        put("input", "+\n999\n1\n")
        put("expectedOutput", "Result: 1000")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
      put(JSONObject().apply {
        put("id", "test_calc_full_div_zero")
        put("taskId", "task_calc_3")
        put("title", "Negative Division by Zero")
        put("input", "/\n-45\n0\n")
        put("expectedOutput", "Division by zero")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
    }

    val hints = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "hint_calc_1")
        put("taskId", "task_calc_1")
        put("level", 1)
        put("title", "Importing helper functions")
        put("content", "You can import functions from other project files using `from calculator_ops import add, subtract, multiply, divide`.")
      })
      put(JSONObject().apply {
        put("id", "hint_calc_2")
        put("taskId", "task_calc_2")
        put("level", 2)
        put("title", "Safe division pattern")
        put("content", "In `calculator_ops.py`, write:\n```python\ndef divide(a, b):\n    if b == 0:\n        return 'Error: Division by zero'\n    return a / b\n```")
      })
    }

    return ProjectEntity(
      id = "py_project_calc",
      title = "Python Calculator CLI",
      language = "python",
      difficulty = "BEGINNER",
      estimatedTime = "25 min",
      skillsJson = JSONArray().apply {
        put("Variables"); put("Input/Output"); put("Conditionals"); put("Functions"); put("Error Handling")
      }.toString(),
      prerequisitesJson = JSONArray().apply {
        put("py_w1_l1"); put("py_w1_l2"); put("py_w1_l3")
      }.toString(),
      description = "Build a modular command-line calculator with arithmetic operations and error handling.",
      instructions = "Implement arithmetic functions in calculator_ops.py and wire them up to the interactive terminal in main.py.",
      starterFilesJson = starterFiles.toString(),
      tasksJson = tasks.toString(),
      testsJson = tests.toString(),
      hintsJson = hints.toString(),
      xpReward = 250,
      coinReward = 50,
      badgeName = "Calculator Builder",
      badgeIcon = "calculate",
      completionCriteria = "Pass all 7 mathematical and CLI test cases",
      isUnlocked = true,
      isCompleted = false
    )
  }

  private fun createGuessingGameProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", """# ==========================================
# Project 2: Number Guessing Game Engine
# ==========================================
# Instructions:
# 1. Complete check_guess in game_logic.py
# 2. Run the game loop in main.py

from game_logic import check_guess, evaluate_game

def start_game():
    print("=== Number Guessing Game ===")
    secret_number = 42
    max_attempts = 3
    
    print("I'm thinking of a number between 1 and 100.")
    print("You have", max_attempts, "attempts.")
    
    attempts_used = 0
    won = False
    
    while attempts_used < max_attempts:
        guess_str = input("Enter your guess: ").strip()
        guess = int(guess_str)
        attempts_used = attempts_used + 1
        
        feedback = check_guess(secret_number, guess)
        print(feedback)
        
        if feedback == "Correct! You win!":
            won = True
            break
            
    summary = evaluate_game(won, secret_number, attempts_used)
    print(summary)

if __name__ == "__main__":
    start_game()
""")
      put("game_logic.py", """# Game feedback and validation logic

def check_guess(secret, guess):
    if guess > secret:
        return "Too high! Try a smaller number."
    elif guess < secret:
        return "Too low! Try a bigger number."
    else:
        return "Correct! You win!"

def evaluate_game(won, secret, attempts):
    if won:
        return "Victory in " + str(attempts) + " attempts! Great job!"
    else:
        return "Game Over! The secret number was " + str(secret) + "."
""")
      put("README.md", """# Project: Number Guessing Game Engine

## Overview
Create an interactive guessing game with hints, attempt limits, feedback logic, and game over evaluation.

## Requirements
1. **`check_guess(secret, guess)`**: Return `"Too high! Try a smaller number."`, `"Too low! Try a bigger number."`, or `"Correct! You win!"`.
2. **`evaluate_game(won, secret, attempts)`**: Return formatted victory or game over message.
3. **Game Loop**: Allow up to 3 attempts, exiting early on win.

## Rewards
- **300 XP**
- **60 CodeCoins**
- **Game Maker Badge**
""")
    }

    val tasks = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "task_guess_1")
        put("title", "Implement Feedback Conditions")
        put("description", "Write check_guess(secret, guess) in game_logic.py to return appropriate comparison strings.")
        put("checkpoint", 1)
        put("xpReward", 45)
        put("hint", "Compare guess > secret, guess < secret, and else return 'Correct! You win!'")
        put("testIds", JSONArray().apply { put("test_guess_high"); put("test_guess_low"); put("test_guess_win") })
      })
      put(JSONObject().apply {
        put("id", "task_guess_2")
        put("title", "Implement Game Evaluator")
        put("description", "Write evaluate_game(won, secret, attempts) returning formatted victory summary or game over notice.")
        put("checkpoint", 2)
        put("xpReward", 55)
        put("hint", "Format victory string with attempts count, or game over with secret number.")
        put("testIds", JSONArray().apply { put("test_guess_eval_win"); put("test_guess_eval_loss") })
      })
      put(JSONObject().apply {
        put("id", "task_guess_3")
        put("title", "Integrate Game Loop")
        put("description", "Run interactive loop up to max_attempts in main.py, terminating early on victory.")
        put("checkpoint", 3)
        put("xpReward", 60)
        put("hint", "Break out of loop when feedback indicates victory.")
        put("testIds", JSONArray().apply { put("test_guess_play_win"); put("test_guess_play_loss") })
      })
    }

    val tests = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "test_guess_high")
        put("taskId", "task_guess_1")
        put("title", "Too High Feedback")
        put("input", "80\n42\n")
        put("expectedOutput", "Too high")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_guess_low")
        put("taskId", "task_guess_1")
        put("title", "Too Low Feedback")
        put("input", "20\n42\n")
        put("expectedOutput", "Too low")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_guess_win")
        put("taskId", "task_guess_1")
        put("title", "First Try Win")
        put("input", "42\n")
        put("expectedOutput", "Correct! You win!")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_guess_eval_win")
        put("taskId", "task_guess_2")
        put("title", "Victory Summary")
        put("input", "42\n")
        put("expectedOutput", "Victory in 1 attempts")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_guess_eval_loss")
        put("taskId", "task_guess_2")
        put("title", "Game Over After 3 Attempts")
        put("input", "10\n20\n30\n")
        put("expectedOutput", "Game Over! The secret number was 42")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_guess_play_win")
        put("taskId", "task_guess_3")
        put("title", "Win on Attempt 2")
        put("input", "50\n42\n")
        put("expectedOutput", "Victory in 2 attempts")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
      put(JSONObject().apply {
        put("id", "test_guess_play_loss")
        put("taskId", "task_guess_3")
        put("title", "Loss on Out of Range Guesses")
        put("input", "1\n2\n3\n")
        put("expectedOutput", "Game Over")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
    }

    val hints = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "hint_guess_1")
        put("taskId", "task_guess_1")
        put("level", 1)
        put("title", "Comparison branching")
        put("content", "Use `if guess > secret:` and `elif guess < secret:` to return appropriate guidance strings.")
      })
    }

    return ProjectEntity(
      id = "py_project_guess",
      title = "Number Guessing Game",
      language = "python",
      difficulty = "BEGINNER",
      estimatedTime = "30 min",
      skillsJson = JSONArray().apply {
        put("Loops"); put("Conditionals"); put("State Tracking"); put("Functions"); put("CLI Game Loop")
      }.toString(),
      prerequisitesJson = JSONArray().apply { put("py_w1_l3"); put("py_w1_l4") }.toString(),
      description = "Create an interactive guessing game with hints, attempt limits, and replay logic.",
      instructions = "Write comparison logic in game_logic.py and manage the round attempts in main.py.",
      starterFilesJson = starterFiles.toString(),
      tasksJson = tasks.toString(),
      testsJson = tests.toString(),
      hintsJson = hints.toString(),
      xpReward = 300,
      coinReward = 60,
      badgeName = "Game Maker",
      badgeIcon = "sports_esports",
      completionCriteria = "Pass all 7 guess validation and game loop tests",
      isUnlocked = true,
      isCompleted = false
    )
  }

  private fun createQuizGameProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", """# ==========================================
# Project 3: CodeQuest Quiz Master
# ==========================================
from questions import get_questions
from quiz_engine import ask_question, calculate_grade

def run_quiz():
    print("=== CodeQuest Python Quiz ===")
    questions = get_questions()
    
    score = 0
    total = len(questions)
    
    for item in questions:
        q_text = item["question"]
        options = item["options"]
        correct_opt = item["answer"]
        
        print("\n" + q_text)
        for opt in options:
            print(opt)
            
        user_choice = input("Your answer (A/B/C): ").strip().upper()
        if user_choice == correct_opt:
            print("Correct! +1 point")
            score = score + 1
        else:
            print("Incorrect. Correct answer was:", correct_opt)
            
    grade = calculate_grade(score, total)
    print("\n=== Quiz Finished ===")
    print("Score:", score, "/", total)
    print("Grade:", grade)

if __name__ == "__main__":
    run_quiz()
""")
      put("questions.py", """# Quiz question database

def get_questions():
    q1 = {
        "question": "What is the keyword to define a function in Python?",
        "options": ["A) func", "B) def", "C) function"],
        "answer": "B"
    }
    q2 = {
        "question": "Which data type is ordered and changeable?",
        "options": ["A) list", "B) tuple", "C) set"],
        "answer": "A"
    }
    q3 = {
        "question": "How do you output text to the console in Python?",
        "options": ["A) echo()", "B) console.log()", "C) print()"],
        "answer": "C"
    }
    return [q1, q2, q3]
""")
      put("quiz_engine.py", """# Scoring and grade calculations

def calculate_grade(score, total):
    if total == 0:
        return "N/A"
    percentage = (score / total) * 100
    if percentage >= 90:
        return "A - Master Coder"
    elif percentage >= 60:
        return "B - Apprentice"
    else:
        return "C - Keep Practicing"
""")
      put("README.md", """# Project: CodeQuest Quiz Master

## Overview
Develop a terminal quiz game with multiple question items, scoring logic, and letter grade evaluations.

## Requirements
1. Define questions dictionary structure in `questions.py`.
2. Implement `calculate_grade(score, total)` in `quiz_engine.py`.
3. Run through all questions and report final score and grade.

## Rewards
- **350 XP**
- **75 CodeCoins**
- **Quiz Master Badge**
""")
    }

    val tasks = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "task_quiz_1")
        put("title", "Build Question Bank")
        put("description", "Construct question list with question text, options, and correct answer key in questions.py.")
        put("checkpoint", 1)
        put("xpReward", 40)
        put("hint", "Return list of dicts with question, options, answer keys.")
        put("testIds", JSONArray().apply { put("test_quiz_all_correct") })
      })
      put(JSONObject().apply {
        put("id", "task_quiz_2")
        put("title", "Grade Calculation Engine")
        put("description", "Implement calculate_grade(score, total) returning A, B, or C tier rank strings.")
        put("checkpoint", 2)
        put("xpReward", 50)
        put("hint", "Calculate (score / total) * 100 and branch with if/elif.")
        put("testIds", JSONArray().apply { put("test_quiz_partial"); put("test_quiz_low") })
      })
    }

    val tests = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "test_quiz_all_correct")
        put("taskId", "task_quiz_1")
        put("title", "Perfect Score (B, A, C)")
        put("input", "B\nA\nC\n")
        put("expectedOutput", "Score: 3 / 3\nGrade: A - Master Coder")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_quiz_partial")
        put("taskId", "task_quiz_2")
        put("title", "Passing Score (B, A, A)")
        put("input", "B\nA\nA\n")
        put("expectedOutput", "Score: 2 / 3\nGrade: B - Apprentice")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_quiz_low")
        put("taskId", "task_quiz_2")
        put("title", "Failing Score (A, B, A)")
        put("input", "A\nB\nA\n")
        put("expectedOutput", "Score: 0 / 3\nGrade: C - Keep Practicing")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
    }

    val hints = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "hint_quiz_1")
        put("taskId", "task_quiz_2")
        put("level", 1)
        put("title", "Percentage formula")
        put("content", "Percentage = `(score / total) * 100`. Then check `>= 90` for A and `>= 60` for B.")
      })
    }

    return ProjectEntity(
      id = "py_project_quiz",
      title = "CodeQuest Quiz Master",
      language = "python",
      difficulty = "INTERMEDIATE",
      estimatedTime = "35 min",
      skillsJson = JSONArray().apply {
        put("Lists"); put("Dictionaries"); put("Iteration"); put("Grading Algorithms"); put("Modular Code")
      }.toString(),
      prerequisitesJson = JSONArray().apply { put("py_w1_l4"); put("py_w1_l5") }.toString(),
      description = "Develop a terminal quiz game with multiple categories, scoring, and performance evaluation.",
      instructions = "Populate questions.py, implement grading in quiz_engine.py, and run the session in main.py.",
      starterFilesJson = starterFiles.toString(),
      tasksJson = tasks.toString(),
      testsJson = tests.toString(),
      hintsJson = hints.toString(),
      xpReward = 350,
      coinReward = 75,
      badgeName = "Quiz Master",
      badgeIcon = "quiz",
      completionCriteria = "Pass all interactive quiz session test cases",
      isUnlocked = true,
      isCompleted = false
    )
  }

  private fun createUnitConverterProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", """# ==========================================
# Project 4: Universal Unit Converter
# ==========================================
from converters import celsius_to_fahrenheit, fahrenheit_to_celsius, km_to_miles, miles_to_km

def main():
    print("=== Universal Unit Converter ===")
    print("1: Celsius to Fahrenheit")
    print("2: Fahrenheit to Celsius")
    print("3: Kilometers to Miles")
    print("4: Miles to Kilometers")
    
    choice = input("Select conversion (1-4): ").strip()
    val_str = input("Enter value: ").strip()
    val = float(val_str)
    
    if choice == "1":
        res = celsius_to_fahrenheit(val)
        print("Converted:", res)
    elif choice == "2":
        res = fahrenheit_to_celsius(val)
        print("Converted:", res)
    elif choice == "3":
        res = km_to_miles(val)
        print("Converted:", res)
    elif choice == "4":
        res = miles_to_km(val)
        print("Converted:", res)
    else:
        print("Error: Unknown conversion")

if __name__ == "__main__":
    main()
""")
      put("converters.py", """# Conversion formulas

def celsius_to_fahrenheit(c):
    return (c * 9 / 5) + 32

def fahrenheit_to_celsius(f):
    return (f - 32) * 5 / 9

def km_to_miles(km):
    return km * 0.621371

def miles_to_km(miles):
    return miles / 0.621371
""")
      put("README.md", """# Project: Universal Unit Converter

## Overview
Build a modular unit converter supporting temperature (Celsius <-> Fahrenheit) and distance (KM <-> Miles) conversions.

## Conversion Formulas
- `C to F`: `(C * 9 / 5) + 32`
- `F to C`: `(F - 32) * 5 / 9`
- `KM to Miles`: `KM * 0.621371`
- `Miles to KM`: `Miles / 0.621371`

## Rewards
- **275 XP**
- **55 CodeCoins**
- **Metric Pioneer Badge**
""")
    }

    val tasks = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "task_conv_1")
        put("title", "Temperature Converters")
        put("description", "Implement celsius_to_fahrenheit and fahrenheit_to_celsius in converters.py.")
        put("checkpoint", 1)
        put("xpReward", 40)
        put("hint", "Use formula: (c * 9 / 5) + 32 for Celsius to Fahrenheit")
        put("testIds", JSONArray().apply { put("test_conv_c_to_f"); put("test_conv_f_to_c") })
      })
      put(JSONObject().apply {
        put("id", "task_conv_2")
        put("title", "Distance Converters")
        put("description", "Implement km_to_miles and miles_to_km in converters.py.")
        put("checkpoint", 2)
        put("xpReward", 40)
        put("hint", "Multiply by 0.621371 for km to miles.")
        put("testIds", JSONArray().apply { put("test_conv_km_to_mi"); put("test_conv_mi_to_km") })
      })
    }

    val tests = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "test_conv_c_to_f")
        put("taskId", "task_conv_1")
        put("title", "100°C to °F (Boiling Point)")
        put("input", "1\n100\n")
        put("expectedOutput", "Converted: 212")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_conv_f_to_c")
        put("taskId", "task_conv_1")
        put("title", "32°F to °C (Freezing Point)")
        put("input", "2\n32\n")
        put("expectedOutput", "Converted: 0")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_conv_km_to_mi")
        put("taskId", "task_conv_2")
        put("title", "10 KM to Miles")
        put("input", "3\n10\n")
        put("expectedOutput", "Converted: 6.21371")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_conv_mi_to_km")
        put("taskId", "task_conv_2")
        put("title", "100 Miles to KM")
        put("input", "4\n62.1371\n")
        put("expectedOutput", "Converted: 100")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
    }

    val hints = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "hint_conv_1")
        put("taskId", "task_conv_1")
        put("level", 1)
        put("title", "Order of operations")
        put("content", "Remember parentheses for temperature conversion: `(f - 32) * 5 / 9`.")
      })
    }

    return ProjectEntity(
      id = "py_project_converter",
      title = "Universal Unit Converter",
      language = "python",
      difficulty = "BEGINNER",
      estimatedTime = "25 min",
      skillsJson = JSONArray().apply {
        put("Arithmetic Formulas"); put("Functions"); put("Floating Point"); put("CLI Parsing")
      }.toString(),
      prerequisitesJson = JSONArray().apply { put("py_w1_l2"); put("py_w1_l3") }.toString(),
      description = "Build a multi-unit conversion tool for temperature and length with input validation.",
      instructions = "Write conversion algorithms in converters.py and handle user selection in main.py.",
      starterFilesJson = starterFiles.toString(),
      tasksJson = tasks.toString(),
      testsJson = tests.toString(),
      hintsJson = hints.toString(),
      xpReward = 275,
      coinReward = 55,
      badgeName = "Metric Pioneer",
      badgeIcon = "straighten",
      completionCriteria = "Pass all 4 unit conversion test cases",
      isUnlocked = true,
      isCompleted = false
    )
  }

  private fun createTodoListProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", """# ==========================================
# Project 5: To-Do List CLI Manager
# ==========================================
from todo_manager import add_task, list_tasks, complete_task

def run_todo_app():
    tasks = []
    print("=== To-Do List CLI ===")
    
    # 1. Add sample tasks
    add_task(tasks, "Learn Python syntax")
    add_task(tasks, "Build Project Lab")
    add_task(tasks, "Defeat Boss Battle")
    
    # 2. Mark task 1 completed
    complete_task(tasks, 1)
    
    # 3. Print list
    list_tasks(tasks)

if __name__ == "__main__":
    run_todo_app()
""")
      put("todo_manager.py", """# To-Do list state operations

def add_task(tasks, title):
    tasks.append({"title": title, "done": False})

def complete_task(tasks, index):
    if index >= 0 and index < len(tasks):
        tasks[index]["done"] = True

def list_tasks(tasks):
    print("Total tasks:", len(tasks))
    for i in range(len(tasks)):
        item = tasks[i]
        status = "[X]" if item["done"] else "[ ]"
        print(str(i + 1) + ". " + status + " " + item["title"])
""")
      put("README.md", """# Project: To-Do List CLI Manager

## Overview
Construct a task management system that maintains an in-memory collection of tasks with done/undone status toggling and indexed listing.

## Requirements
1. `add_task(tasks, title)`: Appends task dictionary with `{"title": title, "done": False}`.
2. `complete_task(tasks, index)`: Marks task at index as `True`.
3. `list_tasks(tasks)`: Displays total tasks and numbered checklist items.

## Rewards
- **400 XP**
- **80 CodeCoins**
- **Productivity Architect Badge**
""")
    }

    val tasks = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "task_todo_1")
        put("title", "Add Task Operation")
        put("description", "Implement add_task(tasks, title) adding dictionary objects with title and done status.")
        put("checkpoint", 1)
        put("xpReward", 50)
        put("hint", "Use tasks.append({'title': title, 'done': False})")
        put("testIds", JSONArray().apply { put("test_todo_run") })
      })
      put(JSONObject().apply {
        put("id", "task_todo_2")
        put("title", "Complete & List Operations")
        put("description", "Implement complete_task(tasks, index) and list_tasks(tasks) with status symbols.")
        put("checkpoint", 2)
        put("xpReward", 70)
        put("hint", "Check bounds before updating tasks[index]['done'] = True")
        put("testIds", JSONArray().apply { put("test_todo_run"); put("test_todo_hidden") })
      })
    }

    val tests = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "test_todo_run")
        put("taskId", "task_todo_1")
        put("title", "Full To-Do Flow Test")
        put("input", "")
        put("expectedOutput", "Total tasks: 3\n1. [ ] Learn Python syntax\n2. [X] Build Project Lab\n3. [ ] Defeat Boss Battle")
        put("comparisonMode", "CONTAINS")
        put("isHidden", false)
      })
      put(JSONObject().apply {
        put("id", "test_todo_hidden")
        put("taskId", "task_todo_2")
        put("title", "Task Completed Status")
        put("input", "")
        put("expectedOutput", "[X] Build Project Lab")
        put("comparisonMode", "CONTAINS")
        put("isHidden", true)
      })
    }

    val hints = JSONArray().apply {
      put(JSONObject().apply {
        put("id", "hint_todo_1")
        put("taskId", "task_todo_2")
        put("level", 1)
        put("title", "List rendering format")
        put("content", "Format string: `str(i + 1) + '. ' + status + ' ' + item['title']`.")
      })
    }

    return ProjectEntity(
      id = "py_project_todo",
      title = "To-Do List CLI Manager",
      language = "python",
      difficulty = "INTERMEDIATE",
      estimatedTime = "40 min",
      skillsJson = JSONArray().apply {
        put("List of Dictionaries"); put("State Mutation"); put("Index Bounds"); put("String Formatting")
      }.toString(),
      prerequisitesJson = JSONArray().apply { put("py_w1_l4"); put("py_w1_l5") }.toString(),
      description = "Construct a task manager that adds, lists, marks complete, and tracks task statuses.",
      instructions = "Implement list mutations in todo_manager.py and run the CLI workflow in main.py.",
      starterFilesJson = starterFiles.toString(),
      tasksJson = tasks.toString(),
      testsJson = tests.toString(),
      hintsJson = hints.toString(),
      xpReward = 400,
      coinReward = 80,
      badgeName = "Productivity Architect",
      badgeIcon = "task_alt",
      completionCriteria = "Pass all task management and formatted listing tests",
      isUnlocked = true,
      isCompleted = false
    )
  }

  private fun createGreetingGeneratorProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def greet(name):\n    return f'Hello, {name}! Welcome to CodeQuest.'\n\nprint(greet('Learner'))")
      put("README.md", "# Greeting Generator\nCreate a program that greets users dynamically.")
    }
    return ProjectEntity(
      id = "greeting_generator",
      title = "Greeting Generator",
      difficulty = "BEGINNER",
      description = "Gather user inputs and generate personalized greetings dynamically.",
      instructions = "Complete the greet() function to return a formatted greeting.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 150,
      coinReward = 30
    )
  }

  private fun createGradeManagerProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def calculate_average(grades):\n    if not grades: return 0.0\n    return sum(grades) / len(grades)\n\nprint(calculate_average([90, 80, 100]))")
      put("README.md", "# Student Grade Manager\nTrack and average student test marks.")
    }
    return ProjectEntity(
      id = "grade_manager",
      title = "Student Grade Manager",
      difficulty = "BEGINNER",
      description = "Track and calculate average student grades locally.",
      instructions = "Complete the calculate_average() function to compute the mean grade.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 200,
      coinReward = 45
    )
  }

  private fun createBankAccountProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "class BankAccount:\n    def __init__(self, owner, balance=0.0):\n        self.owner = owner\n        self.balance = balance\n    def deposit(self, amount):\n        self.balance += amount\n        return self.balance\n    def withdraw(self, amount):\n        if amount <= self.balance:\n            self.balance -= amount\n        return self.balance")
      put("README.md", "# Bank Account System\nSimulate deposit and withdrawal operations using OOP.")
    }
    return ProjectEntity(
      id = "bank_account",
      title = "Bank Account System",
      difficulty = "INTERMEDIATE",
      description = "Simulate a secure online bank account using Object-Oriented principles.",
      instructions = "Implement BankAccount class with deposit and withdraw methods.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 300,
      coinReward = 60
    )
  }

  private fun createContactSearchProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def search_contacts(contacts, query):\n    return [c for c in contacts if query.lower() in c.lower()]")
      put("README.md", "# Contact Search Tool\nSearch contacts dynamically using string parsing.")
    }
    return ProjectEntity(
      id = "contact_search",
      title = "Contact Search Tool",
      difficulty = "INTERMEDIATE",
      description = "Search dynamic address rosters sequentially.",
      instructions = "Implement the contact search function to scan names.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 300,
      coinReward = 60
    )
  }

  private fun createDataProcessorProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def process_data(numbers):\n    return [n * n for n in numbers if n % 2 == 0]")
      put("README.md", "# Data Processing Tool\nTransform number sequences with list comprehensions.")
    }
    return ProjectEntity(
      id = "data_processing",
      title = "Data Processing Tool",
      difficulty = "ADVANCED",
      description = "Process, filter, and transform sequential data structures cleanly.",
      instructions = "Implement process_data using list comprehensions.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 400,
      coinReward = 80
    )
  }

  private fun createApiDashboardProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def parse_api_response(json_str):\n    import json\n    return json.loads(json_str)")
      put("README.md", "# API Data Dashboard\nMock parsing network client answers.")
    }
    return ProjectEntity(
      id = "api_dashboard",
      title = "API Data Dashboard",
      difficulty = "ADVANCED",
      description = "Simulate requesting and deserializing REST API payloads.",
      instructions = "Implement json parser for api response structures.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 450,
      coinReward = 90
    )
  }

  private fun createExpenseTrackerProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def track_expense(expenses, amount, category):\n    expenses.append({'amount': amount, 'category': category})\n    return sum(e['amount'] for e in expenses)")
      put("README.md", "# Personal Expense Tracker\nTrack expenses locally in a session.")
    }
    return ProjectEntity(
      id = "expense_tracker",
      title = "Personal Expense Tracker",
      difficulty = "ADVANCED",
      description = "Capstone: Formulate expense streams with dictionaries.",
      instructions = "Implement custom track_expense helper metrics.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 500,
      coinReward = 100
    )
  }

  private fun createQuizAppProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def score_quiz(answers, solutions):\n    return sum(1 for a, s in zip(answers, solutions) if a == s)")
      put("README.md", "# Interactive Quiz Application\nBuild an interactive quiz runner.")
    }
    return ProjectEntity(
      id = "quiz_app",
      title = "Interactive Quiz Application",
      difficulty = "ADVANCED",
      description = "Capstone: Multi-category coding quiz evaluator.",
      instructions = "Verify and tally correct answers.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 500,
      coinReward = 100
    )
  }

  private fun createStudentSystemProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "class Student:\n    def __init__(self, name, id):\n        self.name = name\n        self.id = id")
      put("README.md", "# Student Management System\nTrack complete student information with classes.")
    }
    return ProjectEntity(
      id = "student_system",
      title = "Student Management System",
      difficulty = "ADVANCED",
      description = "Capstone: Advanced OOP directory mapping.",
      instructions = "Write student details tracker classes.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 500,
      coinReward = 100
    )
  }

  private fun createAdventureGameProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def adventure_step(choice):\n    if choice == 'left': return 'Found a chest!'\n    return 'Met a dragon!'")
      put("README.md", "# Text-Based Adventure Game\nMap interactive branches based on custom user options.")
    }
    return ProjectEntity(
      id = "adventure_game",
      title = "Text-Based Adventure Game",
      difficulty = "ADVANCED",
      description = "Capstone: High branching state adventure game script.",
      instructions = "Implement choices and options state triggers.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 500,
      coinReward = 100
    )
  }

  private fun createFinalCapstoneProject(): ProjectEntity {
    val starterFiles = JSONObject().apply {
      put("main.py", "def run_capstone():\n    return 'Congratulations on graduating Python Mastery!'\nprint(run_capstone())")
      put("README.md", "# Final Python Capstone\nThe ultimate final Python consolidation.")
    }
    return ProjectEntity(
      id = "final_capstone",
      title = "Final Python Capstone",
      difficulty = "ADVANCED",
      description = "Capstone: Consolidation of entire Python curriculum.",
      instructions = "Consolidate loops, functions, lists, dictionaries, OOP, and data science modules into a complete terminal client dashboard.",
      starterFilesJson = starterFiles.toString(),
      xpReward = 600,
      coinReward = 150
    )
  }
}
