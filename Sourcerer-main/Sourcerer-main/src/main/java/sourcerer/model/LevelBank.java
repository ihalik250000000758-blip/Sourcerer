package sourcerer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** All Python lessons. Edit or extend this list to change the curriculum. */
public final class LevelBank {

    private static final List<Level> LEVELS = new ArrayList<>();
    private static final Level TUTORIAL;

    static {
        LEVELS.add(new Level(1, "Variables",
                "A Grey Bat swoops in! Learn variables to cast your first spell. A variable is a labelled box that stores a value.",
                "hp = 100", "hp now holds 100",
                Arrays.asList(
                        new Puzzle("Make a variable x that holds 5.",
                                new String[]{"x", "=", "5"},
                                new String[]{"y", "==", "7", "+", "print", "\"Hi\"", "(", ")"},
                                "x = 5 stores 5 in the variable x."),
                        new Puzzle("Store the text \"Hero\" in a variable called name.",
                                new String[]{"name", "=", "\"Hero\""},
                                new String[]{"x", "\"Villain\"", "3", "+", "==", "(", ")"},
                                "Text goes inside quotes. name now holds Hero."),
                        new Puzzle("Set a variable hp to 100.",
                                new String[]{"hp", "=", "100"},
                                new String[]{"mp", "50", "==", "+", "\"hp\"", ":"},
                                "hp = 100 creates hp and puts 100 inside."),
                        new Puzzle("Add 1 to score: set score equal to score + 1.",
                                new String[]{"score", "=", "score", "+", "1"},
                                new String[]{"-", "*", "0", "==", "x", "\"1\""},
                                "Python works out the right side first, then stores it back in score."))));

        LEVELS.add(new Level(2, "Data Types",
                "A Blobfish flops into your path! Match the data types to weaken it. Values have types: int (whole), float (decimal), str (text), bool (True/False).",
                "speed = 2.5", "speed is a float",
                Arrays.asList(
                        new Puzzle("Make a float variable speed that holds 2.5.",
                                new String[]{"speed", "=", "2.5"},
                                new String[]{"int", "\"2.5\"", "3.5", "==", "x", "+"},
                                "Numbers with a decimal point are floats."),
                        new Puzzle("Make a bool called alive and set it to True.",
                                new String[]{"alive", "=", "True"},
                                new String[]{"true", "\"True\"", "False", "1", "==", "x"},
                                "True and False start with a capital letter and have no quotes."),
                        new Puzzle("Turn the text \"7\" into a number using int().",
                                new String[]{"num", "=", "int", "(", "\"7\"", ")"},
                                new String[]{"str", "7", "print", "==", "+", "\"num\""},
                                "int(\"7\") converts the text \"7\" into the whole number 7."),
                        new Puzzle("Turn the number 5 into text using str().",
                                new String[]{"word", "=", "str", "(", "5", ")"},
                                new String[]{"int", "\"5\"", "+", "print", "==", "x"},
                                "str(5) converts the number 5 into the text \"5\"."))));

        LEVELS.add(new Level(3, "Input and Output",
                "A Shadow Fox blocks the crossroads! Use input() and print() to outsmart it. print() shows things on screen. input() asks the player to type something.",
                "print(\"Hi\")", "Hi",
                Arrays.asList(
                        new Puzzle("Print the text \"Hi\".",
                                new String[]{"print", "(", "\"Hi\"", ")"},
                                new String[]{"input", "=", "x", "+", "\"Bye\"", ":", "5"},
                                "print(\"Hi\") shows Hi on the screen."),
                        new Puzzle("Print the value of the variable score.",
                                new String[]{"print", "(", "score", ")"},
                                new String[]{"input", "\"score\"", "=", "==", "+", ":"},
                                "Without quotes, print shows what the variable holds."),
                        new Puzzle("Ask for a name and store it in name.",
                                new String[]{"name", "=", "input", "(", ")"},
                                new String[]{"print", "\"name\"", "+", "x", "==", "5"},
                                "input() waits for the player to type, then gives back the text."),
                        new Puzzle("Ask for an age as a number and store it in age.",
                                new String[]{"age", "=", "int", "(", "input", "(", ")", ")"},
                                new String[]{"str", "print", "==", "\"age\""},
                                "input() gives text, so int() turns it into a number."),
                        new Puzzle("Print \"Hello \" joined with name.",
                                new String[]{"print", "(", "\"Hello \"", "+", "name", ")"},
                                new String[]{"input", "=", "\"name\"", "5", "==", ":"},
                                "+ joins two pieces of text together."))));

        LEVELS.add(new Level(4, "Conditionals",
                "A Leaf Spider drops from the canopy! Make the right choices with if statements. if lets code make decisions. The line must end with a colon.",
                "if hp > 0: print(\"alive\")", "alive",
                Arrays.asList(
                        new Puzzle("If x is greater than 5, print \"big\".",
                                new String[]{"if", "x", ">", "5", ":", "print", "(", "\"big\"", ")"},
                                new String[]{"for", "<", "while"},
                                "The code after the colon only runs when x > 5 is True."),
                        new Puzzle("If hp equals 0, print \"dead\".",
                                new String[]{"if", "hp", "==", "0", ":", "print", "(", "\"dead\"", ")"},
                                new String[]{"=", "while", ">"},
                                "== compares two values. A single = would store a value instead."),
                        new Puzzle("If age is 18 or more, print \"adult\".",
                                new String[]{"if", "age", ">=", "18", ":", "print", "(", "\"adult\"", ")"},
                                new String[]{"<=", "else", "="},
                                ">= means greater than or equal to."),
                        new Puzzle("If ok is not true, print \"no\".",
                                new String[]{"if", "not", "ok", ":", "print", "(", "\"no\"", ")"},
                                new String[]{"and", "==", "or", "x"},
                                "not flips True to False and False to True."))));

        LEVELS.add(new Level(5, "Loops",
                "A Top-Hat Tiger challenges you! Loop your spells to break through. A loop repeats code so you don't have to write it again and again.",
                "for i in range(3): print(i)", "0  1  2",
                Arrays.asList(
                        new Puzzle("Print i for every number from 0 to 2.",
                                new String[]{"for", "i", "in", "range", "(", "3", ")", ":", "print", "(", "i", ")"},
                                new String[]{},
                                "range(3) gives 0, 1, 2. The loop runs the print three times."),
                        new Puzzle("Keep adding 1 to x while x is less than 5.",
                                new String[]{"while", "x", "<", "5", ":", "x", "+=", "1"},
                                new String[]{"for", "in", "range", "=="},
                                "while repeats as long as its condition stays True. += adds to a variable."),
                        new Puzzle("Print every letter c in \"Hi\".",
                                new String[]{"for", "c", "in", "\"Hi\"", ":", "print", "(", "c", ")"},
                                new String[]{"range", "while", "="},
                                "A for loop can walk through the letters of a string."),
                        new Puzzle("Print \"Hi\" two times with a loop.",
                                new String[]{"for", "n", "in", "range", "(", "2", ")", ":", "print", "(", "\"Hi\"", ")"},
                                new String[]{},
                                "range(2) repeats the loop body two times."))));

        LEVELS.add(new Level(6, "Lists",
                "A Purple Bat swarms you! Use lists to organize your inventory and fight back. A list stores many values in one variable. Counting starts at 0.",
                "nums = [1, 2, 3]", "nums holds three numbers",
                Arrays.asList(
                        new Puzzle("Make a list called nums holding 1, 2 and 3.",
                                new String[]{"nums", "=", "[", "1", ",", "2", ",", "3", "]"},
                                new String[]{"(", ")", ":"},
                                "Square brackets make a list. Items are separated by commas."),
                        new Puzzle("Print how many items are in nums.",
                                new String[]{"print", "(", "len", "(", "nums", ")", ")"},
                                new String[]{"range", "input", "=", "+", "x"},
                                "len() counts the items in a list."),
                        new Puzzle("Add 4 to the end of nums.",
                                new String[]{"nums", ".", "append", "(", "4", ")"},
                                new String[]{"len", "=", "+", "[", "]", "print"},
                                ".append() puts a new item at the end of the list."),
                        new Puzzle("Print the first item of nums.",
                                new String[]{"print", "(", "nums", "[", "0", "]", ")"},
                                new String[]{"1", "len", "=", ".", ":"},
                                "Index 0 is the first item - lists start counting at 0."))));

        LEVELS.add(new Level(7, "Functions",
                "The Demon Spider Boss awaits! Define your ultimate functions to defeat it. A function is a named spell you write once and cast many times.",
                "def hi(): print(\"Hi\")", "hi() prints Hi",
                Arrays.asList(
                        new Puzzle("Define a function hello that prints \"Hi\".",
                                new String[]{"def", "hello", "(", ")", ":", "print", "(", "\"Hi\"", ")"},
                                new String[]{"return", "if", "="},
                                "def creates the function. The code after the colon is its body."),
                        new Puzzle("Define add(a, b) that returns a + b.",
                                new String[]{"def", "add", "(", "a", ",", "b", ")", ":", "return", "a", "+", "b"},
                                new String[]{},
                                "return sends a result back to whoever called the function."),
                        new Puzzle("Call the function hello.",
                                new String[]{"hello", "(", ")"},
                                new String[]{"def", "print", ":", "=", "x", "\"hello\""},
                                "Writing the name followed by () runs the function."),
                        new Puzzle("Print the result of add with 2 and 3.",
                                new String[]{"print", "(", "add", "(", "2", ",", "3", ")", ")"},
                                new String[]{"def", "return", "="},
                                "add(2, 3) returns 5, and print shows it."))));


        TUTORIAL = new Level(0, "Training",
                "Learn the controls with one tiny Python spell.",
                "x = 5", "x holds 5",
                Arrays.asList(
                        new Puzzle("Make a variable x that holds 5.",
                                new String[]{"x", "=", "5"},
                                new String[]{"y", "==", "7", "+", "print", "\"Hi\"", "(", ")"},
                                "x = 5 stores 5 in the variable x.")));
    }

    private LevelBank() {
    }

    public static List<Level> all() {
        return LEVELS;
    }

    public static int count() {
        return LEVELS.size();
    }

    public static Level get(int id) {
        return LEVELS.get(id - 1);
    }

    /** A one-puzzle practice level used by the guided tutorial. */
    public static Level tutorial() {
        return TUTORIAL;
    }
}