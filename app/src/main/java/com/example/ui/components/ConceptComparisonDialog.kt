package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.QuestPrimary

data class ConceptComparisonItem(
  val id: String,
  val title: String,
  val description: String,
  val pythonSnippet: String,
  val jsSnippet: String,
  val javaSnippet: String,
  val cSnippet: String,
  val cppSnippet: String,
  val educationalExplanation: String
)

object ConceptComparisonData {
  val concepts = listOf(
    ConceptComparisonItem(
      id = "variables",
      title = "Variables & Types",
      description = "How variables are declared and typed across languages.",
      pythonSnippet = """name = "Alex"
age = 25
is_dev = True""",
      jsSnippet = """let name = "Alex";
const age = 25;
let isDev = true;""",
      javaSnippet = """String name = "Alex";
int age = 25;
boolean isDev = true;""",
      cSnippet = """char name[] = "Alex";
int age = 25;
int is_dev = 1;""",
      cppSnippet = """std::string name = "Alex";
int age = 25;
bool is_dev = true;""",
      educationalExplanation = "Python and JavaScript use dynamic or inferred typing where variables do not require explicit type signatures. Java, C, and C++ are statically typed and demand explicit type declarations (String, int, bool) at compile time. Notice const/let in modern JavaScript for immutability vs reassignability."
    ),
    ConceptComparisonItem(
      id = "conditionals",
      title = "Conditionals (If / Else)",
      description = "Branching logic and boolean evaluation.",
      pythonSnippet = """if age >= 18:
    print("Adult")
elif age >= 13:
    print("Teen")
else:
    print("Child")""",
      jsSnippet = """if (age >= 18) {
  console.log("Adult");
} else if (age >= 13) {
  console.log("Teen");
} else {
  console.log("Child");
}""",
      javaSnippet = """if (age >= 18) {
    System.out.println("Adult");
} else if (age >= 13) {
    System.out.println("Teen");
} else {
    System.out.println("Child");
}""",
      cSnippet = """if (age >= 18) {
    printf("Adult\n");
} else if (age >= 13) {
    printf("Teen\n");
} else {
    printf("Child\n");
}""",
      cppSnippet = """if (age >= 18) {
    std::cout << "Adult" << std::endl;
} else if (age >= 13) {
    std::cout << "Teen" << std::endl;
} else {
    std::cout << "Child" << std::endl;
}""",
      educationalExplanation = "Python uses indentation blocks and 'elif'. JavaScript, Java, C, and C++ all inherit C-style syntax using parentheses for condition checks 'if (...)' and curly braces '{...}' for scoping."
    ),
    ConceptComparisonItem(
      id = "loops",
      title = "Loops & Iteration",
      description = "Counting from 0 to 4 with loops.",
      pythonSnippet = """for i in range(5):
    print(i)""",
      jsSnippet = """for (let i = 0; i < 5; i++) {
  console.log(i);
}""",
      javaSnippet = """for (int i = 0; i < 5; i++) {
    System.out.println(i);
}""",
      cSnippet = """for (int i = 0; i < 5; i++) {
    printf("%d\n", i);
}""",
      cppSnippet = """for (int i = 0; i < 5; i++) {
    std::cout << i << std::endl;
}""",
      educationalExplanation = "Python's range(n) generates numbers seamlessly. JavaScript, Java, C, and C++ share the three-part for-loop structure: initialization (int i = 0), loop condition (i < 5), and step expression (i++)."
    ),
    ConceptComparisonItem(
      id = "functions",
      title = "Functions & Methods",
      description = "Defining a reusable addition function.",
      pythonSnippet = """def add(a, b):
    return a + b""",
      jsSnippet = """const add = (a, b) => a + b;
// or: function add(a, b) { return a + b; }""",
      javaSnippet = """public static int add(int a, int b) {
    return a + b;
}""",
      cSnippet = """int add(int a, int b) {
    return a + b;
}""",
      cppSnippet = """int add(int a, int b) {
    return a + b;
}""",
      educationalExplanation = "In Python ('def') and JavaScript ('function' or '=>'), return types are inferred. In Java, C, and C++, you must declare the return type ('int') and argument types ('int a, int b') beforehand."
    ),
    ConceptComparisonItem(
      id = "arrays",
      title = "Arrays & Collections",
      description = "Creating a collection of numbers.",
      pythonSnippet = """numbers = [1, 2, 3, 4]
numbers.append(5)""",
      jsSnippet = """const numbers = [1, 2, 3, 4];
numbers.push(5);""",
      javaSnippet = """List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4));
numbers.add(5);""",
      cSnippet = """int numbers[5] = {1, 2, 3, 4, 5};
// Fixed stack buffer""",
      cppSnippet = """std::vector<int> numbers = {1, 2, 3, 4};
numbers.push_back(5);""",
      educationalExplanation = "Python lists and JS arrays are dynamic arrays with .append() / .push(). C uses fixed-size memory buffers, while Java uses ArrayList and C++ uses std::vector for resizeable heap collections."
    ),
    ConceptComparisonItem(
      id = "oop",
      title = "Object-Oriented Design",
      description = "Creating a User class with a name attribute.",
      pythonSnippet = """class User:
    def __init__(self, name):
        self.name = name""",
      jsSnippet = """class User {
  constructor(name) {
    this.name = name;
  }
}""",
      javaSnippet = """public class User {
    private String name;
    public User(String name) {
        this.name = name;
    }
    public String getName() { return name; }
}""",
      cSnippet = """typedef struct {
    char name[50];
} User;""",
      cppSnippet = """class User {
private:
    std::string name;
public:
    User(std::string n) : name(n) {}
    std::string getName() const { return name; }
};""",
      educationalExplanation = "Python uses __init__(self, ...). JS uses class / constructor. Java and C++ enforce access specifiers (private, public) and encapsulation. C uses structs without built-in class member methods."
    )
  )
}

@Composable
fun ConceptComparisonDialog(
  onDismiss: () -> Unit
) {
  var selectedConceptId by remember { mutableStateOf("variables") }
  val concept = remember(selectedConceptId) {
    ConceptComparisonData.concepts.firstOrNull { it.id == selectedConceptId } ?: ConceptComparisonData.concepts.first()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CompareArrows, contentDescription = null, tint = QuestPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Compare Languages", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxSize()) {
        // Concept Selection Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
          items(ConceptComparisonData.concepts) { c ->
            FilterChip(
              selected = selectedConceptId == c.id,
              onClick = { selectedConceptId = c.id },
              label = { Text(c.title) }
            )
          }
        }

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            // Educational explanation box
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = QuestPrimary.copy(alpha = 0.1f),
              border = androidx.compose.foundation.BorderStroke(1.dp, QuestPrimary.copy(alpha = 0.3f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "UNDER THE HOOD",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = QuestPrimary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = concept.educationalExplanation,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          item { LanguageSnippetCard("🐍 Python", concept.pythonSnippet, Color(0xFF3572A5)) }
          item { LanguageSnippetCard("🌐 JavaScript (ES6)", concept.jsSnippet, Color(0xFFF7DF1E)) }
          item { LanguageSnippetCard("☕ Java", concept.javaSnippet, Color(0xFFB07219)) }
          item { LanguageSnippetCard("⚡ C", concept.cSnippet, Color(0xFF555555)) }
          item { LanguageSnippetCard("⚙️ C++", concept.cppSnippet, Color(0xFFF34B7D)) }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Got It")
      }
    }
  )
}

@Composable
private fun LanguageSnippetCard(
  languageTitle: String,
  code: String,
  accentColor: Color
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFF1E1E2E),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF313244)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = languageTitle,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = accentColor)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF181825),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = code,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          color = Color(0xFFCDD6F4),
          modifier = Modifier.padding(8.dp)
        )
      }
    }
  }
}
