# MADE BY: MOUTAAHIBE imed eddine
This project, created in Java, is a compiler simulator that simumates lexical, syntaxtic, and semantic analysis, mimicking the functionality of a real compiler.
The simulator includes examples of pseudo code in the "Snake" language, demonstrating both correct usage and instances with syntaxtic and semantic errors.

## to use it download the content of the repository or fork it to your own repository and run the Main.java file

### Stages of Compilation
A compiler undergoes three primary phases: lexical analysis, syntaxical analysis, and semantic analysis.

#### Lexical Analysis
In this initial phase, the compiler dissects the code into tokens or keywords. For example:
printf("Hello, world!");
The tokens in this example are: printf, (, ", Hello, world!, ", ), ;.

#### Syntaxical Analysis
Syntaxical analysis ensures that the code adheres to the specified grammar rules. It verifies the correct arrangement of tokens in accordance with the language's syntax. For example:

if (a > b) {
    // Code
} else {
    // Code 
}
#### Syntaxical analysis checks if the structure follows the correct pattern for an if statement, including the proper placement of parentheses, braces, and the condition.
some famous syntaxical errors are: missing semi-colone, using undeclared variables and unclosed brackets and parantheses.

Semantic Analysis
Semantic analysis goes beyond syntax, examining the meaning of the code. It ensures that the code makes logical sense and adheres to the intended semantics of the language. For instance:

int x = 5;
int y = "Hello";
int result = x + y;
Semantic analysis would identify the second line as an error since it attempts to assign a string to an integer variable, violating the semantic rules of the language.
some other famous semantic errors are dividing by zero and assigning incompatible values to a variable like char to int or String to char
