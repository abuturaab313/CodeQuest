package com.example.data.local

import com.example.data.models.CodingChallengeEntity
import org.json.JSONArray
import org.json.JSONObject

object InitialChallengeData {

  fun defaultChallenges(): List<CodingChallengeEntity> {
    return listOf(
      // 1. Print Hello World
      CodingChallengeEntity(
        id = "py_c_hello_world",
        lessonId = "py_w1_l1",
        title = "Print Hello World",
        description = "Welcome to Python! Your very first task is to write a program that outputs the classic programmer greeting.",
        difficulty = "EASY",
        languageId = "python",
        category = "BEGINNER",
        starterCode = "# Write your code below to print Hello, World!\n",
        solutionRequirements = "Use the print() function to display 'Hello, World!' exactly as specified.",
        inputDescription = "No input required.",
        outputDescription = "Print 'Hello, World!' to the console.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "")
            put("output", "Hello, World!")
            put("explanation", "The program directly prints the greeting string.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "")
            put("expectedOutput", "Hello, World!")
            put("isHidden", false)
            put("description", "Check standard Hello World output")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "")
            put("expectedOutput", "Hello, World!")
            put("isHidden", true)
            put("description", "Strict case and punctuation verification")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("In Python, we use the print() function to show output on screen.")
          put("Pass the greeting enclosed in quotation marks: print(\"...\")")
          put("Try: print(\"Hello, World!\")")
        }.toString(),
        xpReward = 40,
        coinReward = 10,
        orderIndex = 1,
        isUnlocked = true
      ),

      // 2. Add Two Numbers
      CodingChallengeEntity(
        id = "py_c_add_two",
        lessonId = "py_w1_l3",
        title = "Add Two Numbers",
        description = "Read two integers from standard input (one on each line), calculate their sum, and print the result.",
        difficulty = "EASY",
        languageId = "python",
        category = "MATH",
        starterCode = "# Read two integers and print their sum\na = int(input())\nb = int(input())\n\n# Calculate and print sum\n",
        solutionRequirements = "Calculate a + b and print the integer sum.",
        inputDescription = "Two lines containing integer values a and b.",
        outputDescription = "A single integer representing the sum of a and b.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "5\n7")
            put("output", "12")
            put("explanation", "5 + 7 = 12")
          })
          put(JSONObject().apply {
            put("input", "10\n-3")
            put("output", "7")
            put("explanation", "10 + (-3) = 7")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "5\n7")
            put("expectedOutput", "12")
            put("isHidden", false)
            put("description", "Basic positive integer addition")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "10\n-3")
            put("expectedOutput", "7")
            put("isHidden", false)
            put("description", "Positive and negative addition")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "0\n0")
            put("expectedOutput", "0")
            put("isHidden", true)
            put("description", "Zero values check")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "999\n1001")
            put("expectedOutput", "2000")
            put("isHidden", true)
            put("description", "Large number addition")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Variables a and b already hold the integer inputs.")
          put("Use the addition operator (+) to compute a + b.")
          put("Try: print(a + b)")
        }.toString(),
        xpReward = 50,
        coinReward = 15,
        orderIndex = 2,
        isUnlocked = true
      ),

      // 3. Calculate Square
      CodingChallengeEntity(
        id = "py_c_calc_square",
        lessonId = "py_w1_l3",
        title = "Calculate Square",
        description = "Read a single integer n from input and output its square (n * n).",
        difficulty = "EASY",
        languageId = "python",
        category = "MATH",
        starterCode = "# Read integer n\nn = int(input())\n\n# Compute square and print\n",
        solutionRequirements = "Compute n squared and print the result.",
        inputDescription = "A single integer n.",
        outputDescription = "The square of n.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "5")
            put("output", "25")
            put("explanation", "5 * 5 = 25")
          })
          put(JSONObject().apply {
            put("input", "10")
            put("output", "100")
            put("explanation", "10 * 10 = 100")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "5")
            put("expectedOutput", "25")
            put("isHidden", false)
            put("description", "Square of 5")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "10")
            put("expectedOutput", "100")
            put("isHidden", false)
            put("description", "Square of 10")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "-4")
            put("expectedOutput", "16")
            put("isHidden", true)
            put("description", "Negative number squaring")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "0")
            put("expectedOutput", "0")
            put("isHidden", true)
            put("description", "Zero check")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("You can multiply a number by itself to get its square.")
          put("In Python, you can use n * n or n ** 2.")
          put("Try: print(n * n)")
        }.toString(),
        xpReward = 50,
        coinReward = 15,
        orderIndex = 3,
        isUnlocked = true
      ),

      // 4. Convert Celsius to Fahrenheit
      CodingChallengeEntity(
        id = "py_c_celsius_to_fahr",
        lessonId = "py_w1_l4",
        title = "Convert Celsius to Fahrenheit",
        description = "Given temperature in Celsius as a float from input, convert it to Fahrenheit using the formula: F = (C * 9/5) + 32. Print the result as an integer or rounded float.",
        difficulty = "EASY",
        languageId = "python",
        category = "MATH",
        starterCode = "# Read Celsius temperature\ncelsius = float(input())\n\n# Convert to Fahrenheit and print\n",
        solutionRequirements = "Apply the formula: (celsius * 9/5) + 32 and print.",
        inputDescription = "A number representing degrees Celsius.",
        outputDescription = "Temperature in Fahrenheit.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "0")
            put("output", "32.0")
            put("explanation", "(0 * 9/5) + 32 = 32.0")
          })
          put(JSONObject().apply {
            put("input", "100")
            put("output", "212.0")
            put("explanation", "(100 * 9/5) + 32 = 212.0")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "0")
            put("expectedOutput", "32.0")
            put("isHidden", false)
            put("description", "Freezing point of water")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "100")
            put("expectedOutput", "212.0")
            put("isHidden", false)
            put("description", "Boiling point of water")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "25")
            put("expectedOutput", "77.0")
            put("isHidden", true)
            put("description", "Room temperature")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "-40")
            put("expectedOutput", "-40.0")
            put("isHidden", true)
            put("description", "Negative equal scale")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Multiply celsius by 9/5 (or 1.8), then add 32.")
          put("fah = (celsius * 9 / 5) + 32")
          put("Try: print((celsius * 9 / 5) + 32)")
        }.toString(),
        xpReward = 55,
        coinReward = 15,
        orderIndex = 4,
        isUnlocked = true
      ),

      // 5. Check Even or Odd
      CodingChallengeEntity(
        id = "py_c_even_odd",
        lessonId = "py_w2_l1",
        title = "Check Even or Odd",
        description = "Read an integer n. If n is divisible by 2, print 'Even'. Otherwise, print 'Odd'.",
        difficulty = "EASY",
        languageId = "python",
        category = "BEGINNER",
        starterCode = "# Read integer\nn = int(input())\n\n# Check if even or odd\n",
        solutionRequirements = "Use the modulo operator % to check if n % 2 == 0. Print 'Even' or 'Odd'.",
        inputDescription = "An integer n.",
        outputDescription = "'Even' or 'Odd'.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "4")
            put("output", "Even")
            put("explanation", "4 is divisible by 2 with no remainder.")
          })
          put(JSONObject().apply {
            put("input", "7")
            put("output", "Odd")
            put("explanation", "7 divided by 2 leaves remainder 1.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "4")
            put("expectedOutput", "Even")
            put("isHidden", false)
            put("description", "Even integer test")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "7")
            put("expectedOutput", "Odd")
            put("isHidden", false)
            put("description", "Odd integer test")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "0")
            put("expectedOutput", "Even")
            put("isHidden", true)
            put("description", "Zero check")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "101")
            put("expectedOutput", "Odd")
            put("isHidden", true)
            put("description", "Large odd number")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("The modulo operator % returns the remainder after division.")
          put("If n % 2 == 0, the number is Even, otherwise it is Odd.")
          put("if n % 2 == 0:\n    print(\"Even\")\nelse:\n    print(\"Odd\")")
        }.toString(),
        xpReward = 60,
        coinReward = 20,
        orderIndex = 5,
        isUnlocked = true
      ),

      // 6. Find Largest Number
      CodingChallengeEntity(
        id = "py_c_find_largest",
        lessonId = "py_w2_l2",
        title = "Find Largest Number",
        description = "Read three integers a, b, and c (one on each line). Determine and print the largest value among them.",
        difficulty = "MEDIUM",
        languageId = "python",
        category = "BEGINNER",
        starterCode = "# Read three numbers\na = int(input())\nb = int(input())\nc = int(input())\n\n# Find and print the maximum\n",
        solutionRequirements = "Compare the three variables or use max(a, b, c) and print the highest number.",
        inputDescription = "Three integer lines representing a, b, and c.",
        outputDescription = "The maximum of the three numbers.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "12\n45\n30")
            put("output", "45")
            put("explanation", "45 is the largest value.")
          })
          put(JSONObject().apply {
            put("input", "-5\n-1\n-10")
            put("output", "-1")
            put("explanation", "-1 is greater than -5 and -10.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "12\n45\n30")
            put("expectedOutput", "45")
            put("isHidden", false)
            put("description", "Mid-value highest")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "-5\n-1\n-10")
            put("expectedOutput", "-1")
            put("isHidden", false)
            put("description", "All negative numbers")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "100\n50\n25")
            put("expectedOutput", "100")
            put("isHidden", true)
            put("description", "First value highest")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "7\n7\n7")
            put("expectedOutput", "7")
            put("isHidden", true)
            put("description", "All equal numbers")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("You can use if-elif-else statements to compare values.")
          put("Alternatively, Python provides a built-in function max().")
          put("Try: print(max(a, b, c))")
        }.toString(),
        xpReward = 75,
        coinReward = 25,
        orderIndex = 6,
        isUnlocked = true
      ),

      // 7. Simple Calculator
      CodingChallengeEntity(
        id = "py_c_simple_calc",
        lessonId = "py_w2_l3",
        title = "Simple Calculator",
        description = "Read a number a, an operator op ('+', '-', '*', '/'), and a second number b. Perform the arithmetic operation and print the result (for division, print integer or standard result).",
        difficulty = "MEDIUM",
        languageId = "python",
        category = "MATH",
        starterCode = "# Read inputs\na = int(input())\nop = input().strip()\nb = int(input())\n\n# Compute result based on op and print\n",
        solutionRequirements = "Evaluate based on op ('+', '-', '*', '/'). Print the calculation output.",
        inputDescription = "First number a, operator string op, second number b.",
        outputDescription = "The calculated numeric result.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "8\n*\n3")
            put("output", "24")
            put("explanation", "8 * 3 = 24")
          })
          put(JSONObject().apply {
            put("input", "20\n-\n7")
            put("output", "13")
            put("explanation", "20 - 7 = 13")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "8\n*\n3")
            put("expectedOutput", "24")
            put("isHidden", false)
            put("description", "Multiplication test")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "20\n-\n7")
            put("expectedOutput", "13")
            put("isHidden", false)
            put("description", "Subtraction test")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "15\n+\n25")
            put("expectedOutput", "40")
            put("isHidden", true)
            put("description", "Addition test")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "30\n/\n5")
            put("expectedOutput", "6")
            put("isHidden", true)
            put("description", "Division test")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Check op using if op == '+':, elif op == '-':, etc.")
          put("Handle all 4 operators: +, -, *, /")
          put("if op == '+':\n    print(a + b)\nelif op == '-':\n    print(a - b)\nelif op == '*':\n    print(a * b)\nelif op == '/':\n    print(a // b if a % b == 0 else a / b)")
        }.toString(),
        xpReward = 80,
        coinReward = 25,
        orderIndex = 7,
        isUnlocked = true
      ),

      // 8. Count Characters
      CodingChallengeEntity(
        id = "py_c_count_chars",
        lessonId = "py_w1_l4",
        title = "Count Characters",
        description = "Read a text string on line 1, and a single character on line 2. Count how many times the character appears in the string and print the count.",
        difficulty = "EASY",
        languageId = "python",
        category = "STRINGS",
        starterCode = "# Read string and target character\ntext = input()\nchar = input()\n\n# Count occurrences and print\n",
        solutionRequirements = "Count occurrences of char in text and print the count.",
        inputDescription = "Line 1: text string. Line 2: target character.",
        outputDescription = "An integer representing the count.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "banana\na")
            put("output", "3")
            put("explanation", "The character 'a' appears 3 times in 'banana'.")
          })
          put(JSONObject().apply {
            put("input", "codequest\nz")
            put("output", "0")
            put("explanation", "'z' does not appear in 'codequest'.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "banana\na")
            put("expectedOutput", "3")
            put("isHidden", false)
            put("description", "Multiple occurrences")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "codequest\nz")
            put("expectedOutput", "0")
            put("isHidden", false)
            put("description", "Zero occurrences")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "Mississippi\ns")
            put("expectedOutput", "4")
            put("isHidden", true)
            put("description", "Case-sensitive letter count")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Python strings have a built-in method: text.count(char)")
          put("You can also loop over each character and increment a counter.")
          put("Try: print(text.count(char))")
        }.toString(),
        xpReward = 60,
        coinReward = 20,
        orderIndex = 8,
        isUnlocked = true
      ),

      // 9. Sum a List
      CodingChallengeEntity(
        id = "py_c_sum_list",
        lessonId = "py_w3_l1",
        title = "Sum a List of Numbers",
        description = "Read a space-separated sequence of numbers from input (e.g. '1 2 3 4 5'), compute their total sum, and print it.",
        difficulty = "MEDIUM",
        languageId = "python",
        category = "LISTS",
        starterCode = "# Read space-separated numbers\nline = input()\nnumbers = [int(x) for x in line.split()]\n\n# Compute sum and print\n",
        solutionRequirements = "Sum all numbers in the list and print the result.",
        inputDescription = "A single line of space-separated integers.",
        outputDescription = "The integer sum.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "1 2 3 4 5")
            put("output", "15")
            put("explanation", "1 + 2 + 3 + 4 + 5 = 15")
          })
          put(JSONObject().apply {
            put("input", "10 -5 20")
            put("output", "25")
            put("explanation", "10 + (-5) + 20 = 25")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "1 2 3 4 5")
            put("expectedOutput", "15")
            put("isHidden", false)
            put("description", "Five positive numbers")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "10 -5 20")
            put("expectedOutput", "25")
            put("isHidden", false)
            put("description", "Mixed positive and negative")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "42")
            put("expectedOutput", "42")
            put("isHidden", true)
            put("description", "Single element")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "0 0 0")
            put("expectedOutput", "0")
            put("isHidden", true)
            put("description", "All zeros")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Python has a built-in sum() function that works on lists.")
          put("You can also initialize total = 0 and loop for num in numbers: total += num")
          put("Try: print(sum(numbers))")
        }.toString(),
        xpReward = 75,
        coinReward = 25,
        orderIndex = 9,
        isUnlocked = true
      ),

      // 10. Find Maximum in a List
      CodingChallengeEntity(
        id = "py_c_find_max_list",
        lessonId = "py_w3_l2",
        title = "Find Maximum in a List",
        description = "Read a space-separated sequence of numbers from input. Find and print the maximum value in the list.",
        difficulty = "MEDIUM",
        languageId = "python",
        category = "LISTS",
        starterCode = "# Read space-separated numbers\nline = input()\nnumbers = [int(x) for x in line.split()]\n\n# Find maximum and print\n",
        solutionRequirements = "Find the maximum element in the numbers list and print it.",
        inputDescription = "A space-separated sequence of integers.",
        outputDescription = "The maximum integer in the list.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "3 8 2 15 6")
            put("output", "15")
            put("explanation", "15 is the greatest number in the list.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "3 8 2 15 6")
            put("expectedOutput", "15")
            put("isHidden", false)
            put("description", "Standard positive list")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "-20 -5 -100 -2")
            put("expectedOutput", "-2")
            put("isHidden", true)
            put("description", "All negative numbers list")
          })
          put(JSONObject().apply {
            put("id", "hid_2")
            put("input", "99")
            put("expectedOutput", "99")
            put("isHidden", true)
            put("description", "Single element list")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("Python provides the built-in max() function for lists.")
          put("Or write an algorithm: max_val = numbers[0], loop over numbers, update if num > max_val.")
          put("Try: print(max(numbers))")
        }.toString(),
        xpReward = 80,
        coinReward = 25,
        orderIndex = 10,
        isUnlocked = true
      ),

      // 11. Debug Challenge: Fix Off-by-One Loop Bug
      CodingChallengeEntity(
        id = "py_c_debug_off_by_one",
        lessonId = "py_w2_l4",
        title = "Debug: Fix Off-by-One Loop Error",
        description = "The following program is supposed to print numbers from 1 up to n inclusive, but it currently fails or prints the wrong range. Find and fix the bug!",
        difficulty = "MEDIUM",
        languageId = "python",
        category = "DEBUGGING",
        starterCode = "# BUGGY CODE: Fix the range to print 1 to n inclusive\nn = int(input())\n\nfor i in range(1, n):\n    print(i)\n",
        solutionRequirements = "Adjust the range parameters so it loops from 1 through n inclusive.",
        inputDescription = "A positive integer n.",
        outputDescription = "Numbers from 1 to n, each on its own line.",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "4")
            put("output", "1\n2\n3\n4")
            put("explanation", "Numbers 1, 2, 3, 4 printed on separate lines.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "4")
            put("expectedOutput", "1\n2\n3\n4")
            put("isHidden", false)
            put("description", "Range up to 4")
          })
          put(JSONObject().apply {
            put("id", "pub_2")
            put("input", "1")
            put("expectedOutput", "1")
            put("isHidden", false)
            put("description", "Range up to 1")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "5")
            put("expectedOutput", "1\n2\n3\n4\n5")
            put("isHidden", true)
            put("description", "Range up to 5")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("In Python, range(start, stop) stops BEFORE the stop value.")
          put("To include n, the stop value must be n + 1.")
          put("Change range(1, n) to range(1, n + 1)")
        }.toString(),
        xpReward = 85,
        coinReward = 30,
        orderIndex = 11,
        isUnlocked = true
      ),

      // 12. Debug Challenge: Fix String Concatenation Type Error
      CodingChallengeEntity(
        id = "py_c_debug_syntax",
        lessonId = "py_w1_l2",
        title = "Debug: Fix String & Type Error",
        description = "This program reads a user's name and age, and is supposed to print: 'User [name] is [age] years old'. But it crashes due to string concatenation type mismatch. Fix it!",
        difficulty = "EASY",
        languageId = "python",
        category = "DEBUGGING",
        starterCode = "# BUGGY CODE: Fix the type concatenation error\nname = input()\nage = int(input())\n\n# Fix this line:\nprint(\"User \" + name + \" is \" + age + \" years old\")\n",
        solutionRequirements = "Ensure age is converted to string or formatted cleanly.",
        inputDescription = "Line 1: name string. Line 2: age integer.",
        outputDescription = "Formatted string: 'User [name] is [age] years old'",
        examplesJson = JSONArray().apply {
          put(JSONObject().apply {
            put("input", "Alice\n20")
            put("output", "User Alice is 20 years old")
            put("explanation", "Concatenates name and stringified age.")
          })
        }.toString(),
        publicTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "pub_1")
            put("input", "Alice\n20")
            put("expectedOutput", "User Alice is 20 years old")
            put("isHidden", false)
            put("description", "Alice age 20")
          })
        }.toString(),
        hiddenTestsJson = JSONArray().apply {
          put(JSONObject().apply {
            put("id", "hid_1")
            put("input", "Zack\n15")
            put("expectedOutput", "User Zack is 15 years old")
            put("isHidden", true)
            put("description", "Zack age 15")
          })
        }.toString(),
        hintsJson = JSONArray().apply {
          put("You cannot concatenate an integer directly with a string using '+'.")
          put("Convert age to a string using str(age).")
          put("Try: print(\"User \" + name + \" is \" + str(age) + \" years old\")")
        }.toString(),
        xpReward = 70,
        coinReward = 20,
        orderIndex = 12,
        isUnlocked = true
      )
    )
  }
}
