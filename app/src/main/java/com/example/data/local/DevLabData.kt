package com.example.data.local

import com.example.data.models.*
import org.json.JSONArray
import org.json.JSONObject

object DevLabData {

  // ----------------------------------------------------
  // 1. Bug Hunt Challenges
  // ----------------------------------------------------
  fun defaultBugHunts(): List<BugHuntEntity> = listOf(
    BugHuntEntity(
      id = "bug_hunt_1",
      title = "Empty Username Login Crash",
      scenario = "Users report that when logging into the portal with a blank or whitespace username, the authentication engine throws an uncaught IndexError and crashes the server session.",
      language = "python",
      difficulty = "BEGINNER",
      bugType = BugType.INPUT_VALIDATION,
      filesJson = JSONObject().apply {
        put("auth.py", """def validate_and_login(username, password):
    # BUG: Directly accesses index 0 without checking if username is non-empty!
    if username[0] == " " or len(username) == 0:
        return "Error: Invalid username"
    
    if len(password) < 6:
        return "Error: Password too short"
        
    return f"Welcome, {username.strip()}!"
""")
        put("main.py", """from auth import validate_and_login

def run_login():
    res = validate_and_login("", "secure123")
    print(res)

if __name__ == "__main__":
    run_login()
""")
      }.toString(),
      initialErrorOutput = "IndexError: string index out of range\n  File 'auth.py', line 3, in validate_and_login\n    if username[0] == ' ' or len(username) == 0:",
      errorExplainerWhat = "IndexError occurred when trying to read the first character of an empty string.",
      errorExplainerWhy = "The expression `username[0]` is evaluated before checking `len(username) == 0`. When username is empty, index 0 does not exist.",
      errorExplainerChecklistJson = JSONArray().apply {
        put("Check string emptiness (len == 0 or not username) BEFORE indexing")
        put("Use strip() to sanitize leading and trailing whitespace safely")
        put("Verify order of logical evaluation (left-to-right short-circuiting)")
      }.toString(),
      hypothesisOptionsJson = JSONArray().apply {
        put("The password check is causing a type mismatch")
        put("Accessing username[0] fails when username is empty string ''")
        put("The function needs a try-except block around password length")
        put("The string f-formatting syntax is invalid")
      }.toString(),
      correctHypothesisIndex = 1,
      hint1 = "Check the order in which conditions are evaluated on line 3.",
      hint2 = "Evaluate `len(username) == 0` or `not username.strip()` first so Python doesn't attempt `username[0]` on an empty string.",
      hint3 = "Change line 3 to `if not username or not username.strip(): return 'Error: Invalid username'`",
      solutionCode = """def validate_and_login(username, password):
    if not username or not username.strip():
        return "Error: Invalid username"
    
    if len(password) < 6:
        return "Error: Password too short"
        
    return f"Welcome, {username.strip()}!"
""",
      testsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_bug1_empty")
          put("title", "Empty Username Test")
          put("input", "")
          put("expectedOutput", "Error: Invalid username")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
        put(JSONObject().apply {
          put("id", "test_bug1_valid")
          put("title", "Valid Login Test")
          put("input", "")
          put("expectedOutput", "Welcome")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      xpReward = 120,
      coinReward = 30
    ),

    BugHuntEntity(
      id = "bug_hunt_2",
      title = "Negative Numbers Calculator Glitch",
      scenario = "A financial module produces positive values instead of preserving negative balances when applying tax offsets due to an incorrect absolute value logic check.",
      language = "python",
      difficulty = "BEGINNER",
      bugType = BugType.LOGIC_ERROR,
      filesJson = JSONObject().apply {
        put("finance.py", """def calculate_balance(income, expenses):
    # BUG: using abs() on expenses reverses negative adjustments
    total = income - abs(expenses)
    if total < 0:
        return total
    return total
""")
        put("main.py", """from finance import calculate_balance

def main():
    # User had negative correction of -500 and income 1000
    res = calculate_balance(1000, -500)
    print("Adjusted Balance:", res)

if __name__ == "__main__":
    main()
""")
      }.toString(),
      initialErrorOutput = "AssertionError: Expected 1500 (1000 - (-500) = 1500), but received 500.",
      errorExplainerWhat = "A logic error in subtracting expenses with `abs()` prevents proper handling of negative adjustments and refunds.",
      errorExplainerWhy = "Using `abs(expenses)` forces all expenses to be positive, converting a negative adjustment into a deduction.",
      errorExplainerChecklistJson = JSONArray().apply {
        put("Review the mathematical formula: balance = income - expenses")
        put("Verify whether absolute values should be avoided when signs carry semantic meaning")
        put("Test negative inputs: subtracting a negative number must add to the balance")
      }.toString(),
      hypothesisOptionsJson = JSONArray().apply {
        put("The subtraction sign is missing entirely")
        put("The abs() function improperly changes negative expenses into positive")
        put("The return statement needs to format floats")
      }.toString(),
      correctHypothesisIndex = 1,
      hint1 = "Look at how `expenses` is modified in `finance.py`.",
      hint2 = "Standard subtraction `income - expenses` naturally handles both positive debits and negative refunds.",
      hint3 = "Replace `income - abs(expenses)` with `income - expenses`.",
      solutionCode = """def calculate_balance(income, expenses):
    return income - expenses
""",
      testsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_bug2_neg")
          put("title", "Refund Adjustment Test (1000 - (-500))")
          put("input", "")
          put("expectedOutput", "1500")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      xpReward = 130,
      coinReward = 35
    ),

    BugHuntEntity(
      id = "bug_hunt_3",
      title = "Off-by-One Array Range in Report Generator",
      scenario = "The weekly report generator omits the final day of the month due to an off-by-one error in loop index range boundary.",
      language = "python",
      difficulty = "INTERMEDIATE",
      bugType = BugType.OFF_BY_ONE,
      filesJson = JSONObject().apply {
        put("report.py", """def get_weekly_totals(daily_data):
    totals = []
    # BUG: range(len(daily_data) - 1) skips the last day!
    for i in range(len(daily_data) - 1):
        totals.append(daily_data[i] * 2)
    return totals
""")
        put("main.py", """from report import get_weekly_totals

data = [10, 20, 30, 40, 50, 60, 70]
print("Computed Days:", len(get_weekly_totals(data)))
""")
      }.toString(),
      initialErrorOutput = "Warning: 6 days computed for 7-day dataset. Last day data omitted!",
      errorExplainerWhat = "The report produced 6 items instead of 7.",
      errorExplainerWhy = "In Python, `range(n)` already iterates up to `n - 1`. Passing `len(data) - 1` stops iteration one element too early.",
      errorExplainerChecklistJson = JSONArray().apply {
        put("Check upper limit in range(): range(len(items)) already stops before len(items)")
        put("Consider iterating directly over elements (`for val in daily_data:`)")
      }.toString(),
      hypothesisOptionsJson = JSONArray().apply {
        put("Range function needs step size parameter")
        put("range(len(daily_data) - 1) prematurely excludes the final element")
        put("List append fails on large numbers")
      }.toString(),
      correctHypothesisIndex = 1,
      hint1 = "Check the argument passed into `range()`.",
      hint2 = "Change `range(len(daily_data) - 1)` to `range(len(daily_data))` or `for val in daily_data:`",
      hint3 = "Use `for i in range(len(daily_data)):`",
      solutionCode = """def get_weekly_totals(daily_data):
    totals = []
    for i in range(len(daily_data)):
        totals.append(daily_data[i] * 2)
    return totals
""",
      testsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_bug3_len")
          put("title", "7-Day Completeness Test")
          put("input", "")
          put("expectedOutput", "Computed Days: 7")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      xpReward = 140,
      coinReward = 35
    )
  )

  // ----------------------------------------------------
  // 2. Test-First Challenges
  // ----------------------------------------------------
  fun defaultTestFirstChallenges(): List<TestFirstChallengeEntity> = listOf(
    TestFirstChallengeEntity(
      id = "test_first_1",
      title = "User Registration Validator",
      requirementDescription = "Implement `create_user(name, age)` satisfying validation criteria for user registration.",
      language = "python",
      difficulty = "BEGINNER",
      starterFilesJson = JSONObject().apply {
        put("user_service.py", """def create_user(name, age):
    # TODO: Implement user creation adhering to tests:
    # 1. name must be non-empty string, trimmed
    # 2. age must be integer between 1 and 120
    # 3. Return dict {"name": name, "age": age, "status": "active"} or raise ValueError with message
    pass
""")
        put("main.py", """from user_service import create_user

try:
    u = create_user("Sarah Connor", 28)
    print("User created:", u)
except ValueError as e:
    print("Validation Error:", e)
""")
      }.toString(),
      requirementsChecklistJson = JSONArray().apply {
        put("Accept valid names and integer ages (1..120)")
        put("Strip leading/trailing whitespace from name")
        put("Raise ValueError if name is empty or not a string")
        put("Raise ValueError if age is negative or > 120")
        put("Return dictionary with active status")
      }.toString(),
      acceptanceCriteria = "When invalid data is passed (empty name, negative age, invalid type), the service raises a descriptive ValueError instead of silently failing or crashing.",
      testsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_valid_user")
          put("title", "Valid User Creation")
          put("input", "")
          put("expectedOutput", "User created: {'name': 'Sarah Connor', 'age': 28, 'status': 'active'}")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      estimatedCoveragePercent = 100,
      xpReward = 150,
      coinReward = 35
    ),

    TestFirstChallengeEntity(
      id = "test_first_2",
      title = "E-Commerce Discount Engine",
      requirementDescription = "Implement `apply_coupon(cart_total, coupon_code)` according to business rules.",
      language = "python",
      difficulty = "INTERMEDIATE",
      starterFilesJson = JSONObject().apply {
        put("discounts.py", """def apply_coupon(cart_total, coupon_code):
    # Requirements:
    # - 'SAVE10': 10% off for carts >= 50
    # - 'FLAT20': $20 off for carts >= 100
    # - Unknown coupon or cart < threshold: return cart_total unmodified
    # - Negative or zero cart_total: return 0.0
    pass
""")
        put("main.py", """from discounts import apply_coupon

print("Discounted Total:", apply_coupon(100.0, "SAVE10"))
""")
      }.toString(),
      requirementsChecklistJson = JSONArray().apply {
        put("Apply 10% discount for 'SAVE10' when cart >= $50")
        put("Apply $20 discount for 'FLAT20' when cart >= $100")
        put("Ignore discount if cart is below threshold")
        put("Return 0.0 for negative or zero totals")
      }.toString(),
      acceptanceCriteria = "Discounts must calculate exact float values and reject invalid coupon codes safely.",
      testsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_save10")
          put("title", "SAVE10 Coupon on $100 Cart")
          put("input", "")
          put("expectedOutput", "Discounted Total: 90.0")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      estimatedCoveragePercent = 95,
      xpReward = 160,
      coinReward = 40
    )
  )

  // ----------------------------------------------------
  // 3. Git Curriculum & Interactive Simulator
  // ----------------------------------------------------
  fun defaultGitExercises(): List<GitExerciseEntity> = listOf(
    GitExerciseEntity(
      id = "git_ex_1",
      lessonNumber = 1,
      title = "What is Version Control & Git?",
      concept = "Version Control Basics",
      description = "Version control is a system that records changes to files over time so that you can recall specific versions later, collaborate safely, and track project history.",
      taskPrompt = "Inspect the current repository status using `git status` to see untracked files.",
      initialWorkingFilesJson = "[\"main.py\", \"README.md\"]",
      initialStagedFilesJson = "[]",
      initialBranch = "main",
      branchesJson = "[\"main\"]",
      expectedAction = "STATUS",
      commitMessageOptionsJson = "[\"Initial commit\", \"fix stuff\", \"wip\"]",
      bestCommitMessageIndex = 0,
      xpReward = 60,
      coinReward = 15
    ),

    GitExerciseEntity(
      id = "git_ex_2",
      lessonNumber = 2,
      title = "Staging Area & git add",
      concept = "Staging Area",
      description = "The Staging Area (or Index) is a preview of your next commit. Use `git add <file>` to stage modified files before committing.",
      taskPrompt = "Stage `main.py` to prepare it for your next commit snapshot.",
      initialWorkingFilesJson = "[\"main.py (modified)\", \"utils.py (untracked)\"]",
      initialStagedFilesJson = "[]",
      initialBranch = "main",
      branchesJson = "[\"main\"]",
      expectedAction = "STAGE",
      commitMessageOptionsJson = "[\"Add user authentication logic\", \"changes\", \"update\"]",
      bestCommitMessageIndex = 0,
      xpReward = 70,
      coinReward = 20
    ),

    GitExerciseEntity(
      id = "git_ex_3",
      lessonNumber = 3,
      title = "Commits & Descriptive Messages",
      concept = "Git Commits",
      description = "A commit is a permanent snapshot of staged changes with a descriptive message explaining *why* the change was made.",
      taskPrompt = "Select the most descriptive commit message and create the commit.",
      initialWorkingFilesJson = "[]",
      initialStagedFilesJson = "[\"auth.py\", \"tests.py\"]",
      initialBranch = "main",
      branchesJson = "[\"main\"]",
      expectedAction = "COMMIT",
      commitMessageOptionsJson = "[\"Fix empty username validation crash in login endpoint\", \"fixed a bug\", \"asdf commit\", \"done\"]",
      bestCommitMessageIndex = 0,
      xpReward = 80,
      coinReward = 25
    ),

    GitExerciseEntity(
      id = "git_ex_4",
      lessonNumber = 4,
      title = "Branching & Feature Isolation",
      concept = "Git Branches",
      description = "Branches let you develop features, fix bugs, or experiment without affecting the production `main` branch.",
      taskPrompt = "Create and switch to a new branch called `feature-login`.",
      initialWorkingFilesJson = "[]",
      initialStagedFilesJson = "[]",
      initialBranch = "main",
      branchesJson = "[\"main\", \"feature-login\"]",
      expectedAction = "BRANCH",
      commitMessageOptionsJson = "[\"Implement login form UI\", \"branch commit\"]",
      bestCommitMessageIndex = 0,
      xpReward = 90,
      coinReward = 25
    ),

    GitExerciseEntity(
      id = "git_ex_5",
      lessonNumber = 5,
      title = "Resolving Merge Conflicts",
      concept = "Merge Conflicts",
      description = "A merge conflict occurs when two branches edit the exact same lines of code. Git marks conflicts with `<<<<<<< HEAD`, `=======`, and `>>>>>>>` markers.",
      taskPrompt = "Resolve the conflict markers by keeping the new unified dashboard title and mark resolved.",
      initialWorkingFilesJson = "[\"app_config.py (conflict)\"]",
      initialStagedFilesJson = "[]",
      initialBranch = "main",
      branchesJson = "[\"main\", \"feature-ui\"]",
      expectedAction = "RESOLVE_CONFLICT",
      conflictFileContent = """<<<<<<< HEAD
APP_TITLE = "CodeQuest Student Portal"
=======
APP_TITLE = "CodeQuest Developer Studio"
>>>>>>> feature-ui""",
      resolvedFileContent = """APP_TITLE = "CodeQuest Developer Studio"""",
      commitMessageOptionsJson = "[\"Merge branch 'feature-ui' and resolve title conflict\", \"merge\", \"conflict fix\"]",
      bestCommitMessageIndex = 0,
      xpReward = 120,
      coinReward = 35
    ),

    GitExerciseEntity(
      id = "git_ex_6",
      lessonNumber = 6,
      title = "Version Control Boss Challenge",
      concept = "Full Git Workflow Boss",
      description = "Demonstrate complete proficiency: create a feature branch, stage changes, write a clean commit, merge into main, and verify the repository tree.",
      taskPrompt = "Execute the end-to-end Git workflow and claim the Version Control Boss badge!",
      initialWorkingFilesJson = "[\"calculator.py (modified)\"]",
      initialStagedFilesJson = "[]",
      initialBranch = "main",
      branchesJson = "[\"main\", \"feature-multiply\"]",
      expectedAction = "BOSS",
      commitMessageOptionsJson = "[\"Add multiply and safe divide operations to calculator engine\", \"update calc\", \"code\"]",
      bestCommitMessageIndex = 0,
      xpReward = 200,
      coinReward = 50
    )
  )

  // ----------------------------------------------------
  // 4. Code Review & Refactoring
  // ----------------------------------------------------
  fun defaultCodeReviews(): List<CodeReviewEntity> = listOf(
    CodeReviewEntity(
      id = "review_1",
      title = "Code Smell: Cryptic Variable Naming",
      language = "python",
      snippet = """def c(l):
    t = 0
    for x in l:
        if x > 50:
            t = t + x * 1.15
        else:
            t = t + x
    return t
""",
      isRefactorChallenge = false,
      description = "Analyze the code snippet above. Identify the primary clean-code violation and explain the maintenance tradeoff.",
      issuesOptionsJson = JSONArray().apply {
        put("The function uses single-letter variable names ('c', 'l', 't', 'x') that obscure the business meaning")
        put("The function syntax contains a missing colon")
        put("List iteration cannot use a for loop in Python")
      }.toString(),
      correctIssueIndex = 0,
      explanation = "Meaningful names (e.g. `calculate_total_with_tax`, `prices`, `total`, `price`) communicate intent immediately, reducing cognitive load during reviews.",
      xpReward = 100,
      coinReward = 25
    ),

    CodeReviewEntity(
      id = "review_2",
      title = "Refactoring: Single Responsibility Principle",
      language = "python",
      snippet = """def process_user_order(user, cart_items, payment_card):
    # 1. Validate user
    if not user.get("email") or "@" not in user["email"]:
        return "Invalid email"
    # 2. Compute total
    total = 0
    for item in cart_items:
        total += item["price"] * item["qty"]
    # 3. Apply discount
    if total > 100:
        total *= 0.9
    # 4. Charge card
    if len(payment_card) != 16:
        return "Invalid card"
    # 5. Format invoice
    return f"Order processed for {user['email']}. Total: ${'$'}{total:.2f}"
""",
      isRefactorChallenge = true,
      description = "Refactor this 20-line god-function into modular, focused helper functions (`validate_email`, `calculate_order_total`, `validate_card`). Existing tests must remain passing!",
      issuesOptionsJson = JSONArray().apply {
        put("God function handles validation, calculation, payment, and formatting in one place")
      }.toString(),
      correctIssueIndex = 0,
      explanation = "Breaking the function into single-responsibility subroutines makes testing, debugging, and future changes isolated and safe.",
      refactorStarterCode = """def validate_user(user):
    return user.get("email") and "@" in user["email"]

def calculate_order_total(cart_items):
    total = sum(item["price"] * item["qty"] for item in cart_items)
    if total > 100:
        total *= 0.9
    return total

def validate_card(card):
    return len(card) == 16

def process_user_order(user, cart_items, payment_card):
    if not validate_user(user):
        return "Invalid email"
    if not validate_card(payment_card):
        return "Invalid card"
    total = calculate_order_total(cart_items)
    return f"Order processed for {user['email']}. Total: ${'$'}{total:.2f}"
""",
      refactorTestsJson = JSONArray().apply {
        put(JSONObject().apply {
          put("id", "test_order_valid")
          put("title", "Valid Order Processing")
          put("input", "")
          put("expectedOutput", "Order processed")
          put("comparisonMode", "CONTAINS")
          put("isHidden", false)
        })
      }.toString(),
      xpReward = 150,
      coinReward = 35
    )
  )

  // ----------------------------------------------------
  // 5. Initial Project Issues for Issue-Based Workflows
  // ----------------------------------------------------
  fun defaultProjectIssues(): List<ProjectIssueEntity> = listOf(
    ProjectIssueEntity(
      id = "issue_calc_1",
      projectId = "py_project_calc",
      issueNumber = 1,
      title = "Calculator crashes on empty operation input",
      description = "When the user presses Enter without typing an operation (+, -, *, /), the CLI raises an UnboundLocalError.",
      difficulty = "BEGINNER",
      skillsJson = "[\"Input Validation\", \"Conditionals\"]",
      status = IssueStatus.TODO,
      affectedFile = "main.py",
      testId = "test_calc_add",
      xpReward = 40
    ),
    ProjectIssueEntity(
      id = "issue_calc_2",
      projectId = "py_project_calc",
      issueNumber = 2,
      title = "Support negative numbers in calculation",
      description = "Ensure inputs like '-15' and '-30' parse accurately as floating-point numbers without throwing ValueError.",
      difficulty = "BEGINNER",
      skillsJson = "[\"Floats\", \"Exception Handling\"]",
      status = IssueStatus.TODO,
      affectedFile = "calculator_ops.py",
      testId = "test_calc_sub",
      xpReward = 50
    ),
    ProjectIssueEntity(
      id = "issue_calc_3",
      projectId = "py_project_calc",
      issueNumber = 3,
      title = "Implement modulo (%) remainder operator",
      description = "Add support for `%` operator in calculator_ops.py and wire it to main.py.",
      difficulty = "INTERMEDIATE",
      skillsJson = "[\"Arithmetic\", \"Functions\"]",
      status = IssueStatus.TODO,
      affectedFile = "calculator_ops.py",
      testId = "test_calc_mul",
      xpReward = 60
    )
  )

  // ----------------------------------------------------
  // 6. Default Developer Stats
  // ----------------------------------------------------
  fun defaultDeveloperStats(): DeveloperStatsEntity = DeveloperStatsEntity(
    userId = 1L,
    bugsFixedCount = 0,
    testsPassedCount = 0,
    gitExercisesCompleted = 0,
    commitsCreatedCount = 0,
    branchesCreatedCount = 0,
    conflictsResolvedCount = 0,
    codeReviewsCompleted = 0,
    refactorsCompleted = 0,
    realWorldProjectsCompleted = 0,
    readmeScore = 85
  )
}
