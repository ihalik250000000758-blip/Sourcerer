-- Seed exactly the seven campaign levels and six tutorial minigames in the Java source.
-- These rows are definitions, not player progress. Re-running refreshes their labels only.
USE sourcerer_crusade;

INSERT INTO stages (stage_id, stage_name, description) VALUES
    (1, 'Variables', 'A variable is a labelled box that stores a value.'),
    (2, 'Data Types', 'Values have types: int (whole), float (decimal), str (text), bool (True/False).'),
    (3, 'Input and Output', 'print() shows things on screen. input() asks the player to type something.'),
    (4, 'Conditionals', 'if lets code make decisions. The line must end with a colon.'),
    (5, 'Loops', 'A loop repeats code so you do not have to write it again and again.'),
    (6, 'Lists', 'A list stores many values in one variable. Counting starts at 0.'),
    (7, 'Functions', 'A function is a named spell you write once and cast many times.')
ON DUPLICATE KEY UPDATE stage_name = VALUES(stage_name), description = VALUES(description);

INSERT INTO tutorial_stages (tutorial_stage_id, title, mechanic) VALUES
    (1, 'Variables', 'Match the boxes'),
    (2, 'Data Types', 'Sort the values'),
    (3, 'Input & Output', 'Connect the blocks'),
    (4, 'Conditionals', 'Pick the path'),
    (5, 'Loops', 'Complete the loop'),
    (6, 'Functions', 'Build the machine')
ON DUPLICATE KEY UPDATE title = VALUES(title), mechanic = VALUES(mechanic);
