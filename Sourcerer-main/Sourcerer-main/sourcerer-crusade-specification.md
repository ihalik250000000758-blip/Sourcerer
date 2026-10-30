# Universal Project Context & Execution Specification
## The Sourcerer: Crusade — Maven Refactoring, World Map Expansion, Python Boss Challenges, Potion Store, and Database Persistence

## 1. Role and Objective

Act as a Senior Java Software Engineer, Game Systems Architect, OOP Specialist, Database Engineer, UI/UX Designer, QA Engineer, and Technical Project Lead.

Inspect the existing Sourcerer: Crusade project and implement the requested improvements directly in the actual codebase. The uploaded project archive is the starting point and source of truth for existing features.

**The objective is to expand and improve the game without removing its existing features.** Refactoring must preserve existing behavior unless a specific defect requires a correction, and any necessary behavioral change must be documented.

Do not merely generate a plan or create placeholder files. Implement the features, update the SQL scripts, test the application, and provide step-by-step MySQL Workbench instructions.

## 2. Mandatory Existing Feature Preservation

**CRITICAL REQUIREMENT: DO NOT REMOVE, DISABLE, REPLACE, OR REGRESS EXISTING WORKING FEATURES.**

Before making changes, inspect the uploaded project and create an inventory of its existing functionality.

### Preservation rules

1. Identify existing gameplay systems, screens, buttons, menus, tutorials, Python challenges, player attributes, combat mechanics, enemies, rewards, progression rules, persistence logic, tests, resources, and configuration.
2. Record the current behavior of each relevant feature before refactoring.
3. Preserve existing classes, methods, business rules, and user workflows whenever possible.
4. Move classes into the required Maven packages without changing their intended behavior.
5. Extend existing systems to support new features rather than creating competing implementations.
6. Reuse existing models, services, repositories, challenge evaluators, and UI components where appropriate.
7. Do not remove existing levels, tutorials, challenges, enemies, rewards, or screens simply because the new specification introduces additional content.
8. Do not replace existing gameplay with simplified demonstrations, mock data, hardcoded UI values, or placeholder implementations.
9. Do not remove existing database tables, columns, constraints, records, or migration history without first proving that the change is necessary and obtaining approval for destructive changes.
10. Preserve existing save data and maintain backward compatibility where feasible.
11. Keep existing tests and add regression tests. Do not delete or weaken a test simply because it fails after a change.
12. If an existing feature conflicts with a new requirement, identify the conflict, explain the impact, and choose the smallest compatible modification. Ask for approval if the change is destructive or materially changes established gameplay.
13. If a feature is currently broken, repair it rather than silently deleting it.
14. If a feature cannot be preserved, document the exact reason, affected files, impact, and proposed alternative before proceeding.

### Required preservation workflow

Before implementation:

- Inventory existing features and relevant files.
- Establish a baseline build and test result.
- Identify dependencies between existing features.
- Map current classes to their intended destination packages.
- Create a feature-preservation and regression-test checklist.

During implementation:

- Make incremental, focused changes.
- Test affected existing functionality after each major phase.
- Avoid unrelated rewrites and unnecessary architectural changes.

After implementation:

- Compare the original feature inventory against the final application.
- Verify that every existing feature is preserved or has an explicitly approved correction.
- Report regressions, unresolved issues, and unverified behavior honestly.

**Adding the world map, 100-stage progression, boss battles, potion store, and database persistence must not come at the cost of existing Sourcerer: Crusade functionality.**

## 3. Source of Truth and Initial Inspection

Inspect the entire project before editing:

- Project structure and Java source files.
- `pom.xml` and the existing Java version.
- Application entry point and Swing/AWT screens.
- Existing gameplay, Python tutorial, challenge, battle, and reward logic.
- Player state, progression, and save/load functionality.
- Existing SQL files, schemas, migrations, repositories, and database configuration.
- Automated tests, resources, and documentation.
- Git status and any uncommitted changes.

Determine what already works, what is incomplete, and what is missing.

Do not assume a feature exists merely because a class or button is present.

If a build currently fails, identify whether the failure existed before your changes. Do not attribute pre-existing failures to the refactoring or conceal them.

## 4. Project Scope and Acceptance Requirements

| ID | Requirement | Acceptance condition |
|---|---|---|
| F-01 | Feature preservation | All existing working features remain available. |
| F-02 | Maven refactoring | The project uses standard Maven directories and valid package declarations. |
| F-03 | Package organization | `model`, `tutorial`, `ui`, and `util` are implemented. |
| F-04 | Database connection | `util.DatabaseConnection` connects to the configured local database. |
| F-05 | Expanded world map | Approximately 100 playable stages with visible progression. |
| F-06 | Player XP | Experience and character level progression work and persist. |
| F-07 | Player coins | Rewards, purchases, and coin balances are consistent and persistent. |
| F-08 | Potion store | Players can purchase affordable potions using earned coins. |
| F-09 | Boss stages | Every tenth world stage is a more difficult Python boss challenge. |
| F-10 | Boss statistics | Individual boss defeats and total boss count persist. |
| F-11 | Inventory | Potion quantities and consumption persist. |
| F-12 | SQL scripts | The schema matches the Java implementation. |
| F-13 | Workbench guide | Instructions explain database setup, script execution, and verification. |
| F-14 | Regression testing | Existing features continue working after the changes. |
| F-15 | Documentation | Setup, gameplay, testing, and troubleshooting are documented. |

## 5. Maven Package Structure

Refactor the project into Maven's standard layout while retaining its actual base package.

```text
project-root/
├── pom.xml
├── README.md
├── docs/
│   ├── implementation-plan.md
│   ├── database-setup.md
│   ├── gameplay-guide.md
│   └── verification-report.md
├── sql/
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   ├── 03_constraints_and_indexes.sql
│   ├── 04_seed_game_data.sql
│   ├── 05_verification_queries.sql
│   └── 06_reset_test_data.sql
├── src/
│   ├── main/
│   │   ├── java/[existing-base-package]/
│   │   │   ├── Main.java
│   │   │   ├── model/
│   │   │   ├── tutorial/
│   │   │   ├── ui/
│   │   │   └── util/
│   │   │       └── DatabaseConnection.java
│   │   └── resources/
│   └── test/
│       └── java/[existing-base-package]/
```

Replace `[existing-base-package]` with the correct package name identified in the project. Do not use the placeholder literally.

### Package responsibilities

- **`model`:** Player, XP, coins, inventory, potions, stages, enemies, bosses, rewards, and progression models.
- **`tutorial`:** Existing Python lessons, challenge content, answer evaluation, feedback, and boss challenge logic.
- **`ui`:** Existing and new game screens, world map, battles, store, inventory, player status, and navigation.
- **`util`:** `DatabaseConnection`, database configuration helpers, and justified shared utilities.

Introduce a `repository` or `service` package only when it provides a clear separation of responsibilities and is compatible with the existing design.

### Maven requirements

- Update package declarations, imports, tests, and resource paths consistently.
- Preserve existing functionality during class moves.
- Retain the current Java version unless inspection establishes a concrete compatibility reason to change it.
- Use compatible JDBC and JUnit 5 dependencies.
- Configure the actual application entry point correctly.
- Remove obsolete files only after confirming they are unused.
- Verify `mvn clean test` and `mvn package` using the documented environment.

Do not replace Swing/AWT with another UI framework or rewrite the application unnecessarily.

## 6. Object-Oriented Programming

Apply all four OOP principles meaningfully:

- **Encapsulation:** Private fields, controlled state changes, and validation.
- **Inheritance:** Appropriate shared abstractions for related enemies or encounters.
- **Polymorphism:** Different enemy, boss, or challenge behaviors through interfaces or overridden methods.
- **Abstraction:** Separate UI, gameplay rules, database access, and challenge evaluation.

Use KISS. Avoid unnecessary design patterns, duplicate models, and overengineering.

## 7. World Map and 100-Stage Progression

Expand the existing world map to support stages 1–100 without removing existing stages or content.

- Stage 1 is initially unlocked for a new player.
- Additional stages unlock according to the established progression rules.
- The map distinguishes locked, unlocked, completed, and boss stages.
- Completed stages can be replayed when consistent with the existing game.
- Stage completion and unlock state persist across restarts.
- Use regions, scrolling, pages, or another suitable navigation pattern to keep the map usable.

### Boss stages

Boss encounters occur at stages:

**10, 20, 30, 40, 50, 60, 70, 80, 90, and 100.**

Every boss stage must present a more demanding Python challenge than an ordinary stage. Increase difficulty progressively using concepts such as:

- Output tracing.
- Conditionals and nested conditions.
- Loops and nested loops.
- Strings and lists.
- Functions and return values.
- Debugging and error analysis.
- Multi-concept problems at higher stages.

Preserve existing tutorials and challenges. Extend the curriculum rather than deleting or replacing existing content.

Track both the total bosses defeated and the individual boss stages defeated.

## 8. Player XP, Character Level, Coins, and Rewards

Keep the world stage number separate from the player's character level.

- World progression contains approximately 100 stages.
- Character level is determined by accumulated XP using one documented formula.
- The current stage does not automatically determine the character level.
- XP, coins, and progression persist across application restarts.

Reuse existing XP, coin, reward, and level rules when available. Do not silently rebalance the game.

### Reward integrity

- Award rewards only when the applicable gameplay condition is satisfied.
- Prevent repeated submissions and retries from granting duplicate rewards.
- Preserve existing first-clear rules.
- Replaying a completed stage must not repeat first-clear rewards.
- Validate XP, coins, and inventory quantities.
- Reject negative or invalid balance changes.
- Use transactions when an operation changes multiple database records.

Do not grant rewards merely because a screen opens or a stage is constructed.

## 9. Potion Store and Inventory

Implement a functional store integrated with the existing game UI.

The store must:

- Display potion names, descriptions, prices, and owned quantities.
- Allow purchases using the player's coin balance.
- Reject purchases when funds are insufficient.
- Deduct coins and increase inventory atomically.
- Prevent duplicate purchases caused by repeated events or retries.
- Refresh the UI after a successful purchase.
- Display clear purchase success and failure messages.

Preserve existing potion types, prices, and effects when they are already defined.

Potion consumption must validate availability, decrement inventory exactly once, and apply the intended effect. A failed action must not consume a potion.

Do not implement a store that only changes UI labels without updating real gameplay and persistent state.

## 10. Database Architecture and `DatabaseConnection`

Use JDBC to connect to the local MySQL-compatible server.

The required class is:

`util.DatabaseConnection`

It must:

- Create JDBC connections using configuration rather than hardcoded credentials.
- Use the driver appropriate for the verified database server.
- Support connection validation and useful error reporting.
- Close resources safely.
- Avoid exposing passwords in logs.
- Never silently replace saved player data with defaults after a database failure.

XAMPP commonly uses MariaDB, so verify the actual server version and port before selecting the driver and SQL syntax.

### Recommended persistent data

Adapt existing tables instead of blindly creating duplicates.

Persist at least:

1. Player identity, XP, coins, and relevant statistics.
2. Potion inventory and quantities.
3. Stage definitions and stage types.
4. Player stage unlock and completion progress.
5. Individual boss defeat records or an equivalent normalized representation.
6. Challenge definitions or references where challenge content is stored in the database.
7. Reward and purchase operation records where required to prevent duplicate processing.

Use appropriate primary keys, foreign keys, unique constraints, indexes, and CHECK constraints supported by the actual server version.

### Data integrity requirements

- Use `PreparedStatement` for SQL values.
- Use transactions for multi-record purchases, rewards, and stage settlement.
- Roll back incomplete operations.
- Prevent negative balances and inventory quantities.
- Use unique operation identifiers or equivalent safeguards against duplicate rewards.
- Ensure boss records reference valid boss stages.
- Keep Java validation and database constraints consistent.
- Never execute destructive SQL automatically.
- Do not delete or replace existing schemas, migrations, or player data without approval.

Local persistence means the game can store data on the same computer and restore it after restart. It does not imply that the game is hosted online or synchronized between computers.

## 11. SQL Scripts and MySQL Workbench

Create complete SQL scripts aligned with the actual Java implementation.

### Required scripts

**`01_create_database.sql`**
- Create the application database.
- Define appropriate character encoding and collation.

**`02_create_tables.sql`**
- Create the required tables in dependency order.
- Define keys and data types.

**`03_constraints_and_indexes.sql`**
- Add any constraints or indexes not already defined.
- Avoid duplicating constraints.

**`04_seed_game_data.sql`**
- Populate the 100 stage definitions.
- Identify every tenth stage as a boss stage.
- Preserve existing challenge content and seed only necessary initial data.

**`05_verification_queries.sql`**
- Inspect player XP, character level, coins, inventory, unlocked stages, completed stages, boss defeats, and reward/purchase history.

**`06_reset_test_data.sql`**
- Clearly identify destructive operations.
- Restrict its documented use to disposable test data.
- Never run it automatically.

If the existing project uses database migrations, preserve that migration mechanism and do not create competing schema histories.

### MySQL Workbench guide

Provide beginner-friendly instructions to:

1. Start the local database service in XAMPP.
2. Verify whether the server is MySQL or MariaDB and identify the port.
3. Connect through MySQL Workbench using the verified credentials.
4. Open and execute `01_create_database.sql`.
5. Refresh the Schemas panel.
6. Execute the table, constraint, and seed scripts in order.
7. Inspect the tables and relationships.
8. Run the verification queries.
9. Configure the Java application's database connection.
10. Build and launch the Maven application.
11. Complete a stage, earn XP and coins, buy a potion, and defeat a boss.
12. Run the verification queries again to confirm the saved changes.
13. Restart the application and confirm that saved progress is restored.

Explain how to select and execute SQL statements, run scripts, refresh schemas, and recognize successful results.

Do not claim that the SQL was executed against the user's database unless it actually was. Request explicit approval before executing SQL against the user's local database.

## 12. UI/UX Integration

Integrate new features into the existing UI while preserving its established functionality.

The player should be able to access:

- Existing game screens and features.
- Expanded world map.
- Player status, character level, XP, and coins.
- Python challenges and boss encounters.
- Potion store and inventory.
- Stage completion and boss defeat status.

Maintain consistent navigation, readable layouts, and useful feedback for locked stages, purchases, insufficient coins, challenge results, and database errors.

Database operations must not block the Swing Event Dispatch Thread. Use background workers where appropriate and update Swing components safely.

Do not remove an existing screen or workflow merely to simplify the new design.

## 13. Specialist Roles and Parallel Execution

Use actual subagents only if the environment supports them. Otherwise, perform these roles sequentially without claiming parallel execution.

- **Coordinator:** Owns scope, integration, task order, and acceptance criteria.
- **Architecture Reviewer:** Reviews packages, OOP, and feature preservation.
- **Database Specialist:** Designs the schema, constraints, transactions, and Workbench scripts.
- **Gameplay Specialist:** Implements progression, rewards, Python challenges, bosses, and potions.
- **UI/UX Specialist:** Integrates new screens while preserving existing navigation.
- **QA/Debugging Specialist:** Runs tests, reproduces failures, and verifies persistence.
- **Final Reviewer:** Audits the final diff, feature inventory, tests, and documentation.

Parallelize only independent tasks with clear file ownership. Integrate and test all changes before reporting completion.

## 14. Loop Engineering

Apply this cycle to each major change:

1. **Specify** — Define behavior and acceptance criteria.
2. **Inspect** — Read affected code and dependencies.
3. **Implement** — Make the smallest complete change.
4. **Test** — Run relevant tests.
5. **Review** — Check correctness, architecture, and regressions.
6. **Fix** — Repair confirmed defects.
7. **Retest** — Verify the fix and affected existing features.
8. **Record** — Document evidence and remaining limitations.

Severity levels:

- **Critical:** Data loss, duplicated rewards, corrupted saves, or unsafe destructive SQL.
- **High:** Broken progression, purchases, boss tracking, or saved-game loading.
- **Medium:** Incorrect UI behavior or incomplete error handling.
- **Low:** Cosmetic or minor maintainability issues.

After three unsuccessful repair cycles for the same issue, stop, record the evidence and root cause, and propose the smallest viable correction. Do not weaken tests or disable database constraints to obtain a passing result.

## 15. Implementation Roadmap

### Phase 1 — Audit and preserve
- Inventory existing features and files.
- Record baseline build and test results.
- Map classes to the target packages.
- Establish the feature-preservation checklist.

### Phase 2 — Maven refactoring
- Update project structure and package declarations.
- Fix imports, dependencies, resources, and startup.
- Verify existing features still work.

### Phase 3 — Domain and gameplay
- Implement or extend player progression, stages, bosses, rewards, and inventory.
- Preserve established gameplay rules.
- Add unit and regression tests.

### Phase 4 — Database and SQL
- Implement `DatabaseConnection`.
- Align the schema with actual repository operations.
- Create SQL scripts and Workbench instructions.
- Test transactions and duplicate-operation safeguards.

### Phase 5 — World map and Python challenges
- Expand the progression system to 100 stages.
- Integrate the ten boss encounters.
- Preserve existing lessons and challenges.
- Persist stage and boss progress.

### Phase 6 — Store and inventory
- Integrate purchases and potion consumption.
- Verify coin deduction and inventory updates are atomic.
- Test insufficient funds and repeated requests.

### Phase 7 — UI integration
- Connect all new features to the existing UI.
- Preserve previous screens and workflows.
- Verify error handling and UI responsiveness.

### Phase 8 — Full verification
- Run regression tests, unit tests, and Maven packaging.
- Test the gameplay-to-database flow.
- Verify saved records through Workbench.
- Complete the final feature-preservation audit.

Adjust the sequence if dependencies require it, but do not omit acceptance criteria.

## 16. Testing and Acceptance Matrix

Every requirement must have a truthful status: `PASS`, `FAIL`, `BLOCKED`, or `NOT RUN`.

| Test | Expected result |
|---|---|
| Existing feature inventory | Original features are documented. |
| Regression tests | Existing supported functionality still works. |
| Maven build | Compiles with the documented Java version. |
| Package migration | All moved classes and imports resolve correctly. |
| Database connection | Connects using the verified configuration. |
| New player | Starts with the documented initial state. |
| Player reload | XP, coins, inventory, and progression persist. |
| XP and level | Rewards and level calculation are correct. |
| Coin rewards | Valid rewards are credited exactly once. |
| Store purchase | Coins decrease and inventory increases atomically. |
| Insufficient funds | Purchase fails without changing state. |
| Potion consumption | One potion is consumed and the intended effect is applied. |
| Stage progression | Unlocks and completion states persist correctly. |
| Replay | First-clear rewards are not duplicated. |
| Boss stages | Stages 10 through 100, every ten stages, are boss stages. |
| Boss tracking | Individual defeats and total count remain consistent. |
| Transaction rollback | Failed operations leave no partial updates. |
| Database failure | Errors are reported without silently corrupting data. |
| Workbench verification | SQL queries show the expected saved state. |
| Restart test | The player can resume saved progress. |
| UI responsiveness | Database operations do not freeze Swing. |

Use actual command output, test names, or query results as evidence. Never mark unexecuted tests as passed.

## 17. Safety and Change Control

- Preserve unrelated user changes.
- Do not remove existing features to simplify implementation.
- Do not reset Git or discard user modifications.
- Do not delete player data or execute destructive SQL automatically.
- Do not commit or push without explicit approval.
- Do not fabricate tests, query results, or feature completion.
- Document necessary behavioral changes and obtain approval for destructive or materially disruptive changes.
- Continue implementation when work can proceed safely; do not stop after writing a plan.

## 18. Required Deliverables

Produce the following in the actual project:

1. Maven refactoring with correct package declarations.
2. All preserved existing features.
3. `util.DatabaseConnection`.
4. Expanded 100-stage world map.
5. Player XP, character levels, coins, and rewards.
6. Ten increasingly challenging Python boss encounters.
7. Functional potion store and inventory.
8. Persistent player, stage, and boss records.
9. Complete SQL scripts aligned with the implementation.
10. Step-by-step MySQL Workbench instructions.
11. Automated gameplay, persistence, purchase, and regression tests.
12. Updated README and implementation documentation.
13. Final verification report and feature-preservation audit.

Do not create empty placeholder classes or documents simply to satisfy the list.

## 19. Final Report

Report:

### A. Summary
What was actually implemented.

### B. Existing Feature Preservation
List the original features inspected and confirm which were preserved, repaired, changed with justification, or remain unverified.

### C. Files Changed
Important created, modified, moved, and removed files.

### D. Architecture
Maven packages and the relationships between UI, gameplay, and database components.

### E. Database
Tables, constraints, configuration, SQL scripts, and Workbench instructions.

### F. Verification
Actual build commands, test results, failures, and limitations.

### G. Acceptance Matrix
Final status for every requirement.

### H. Manual Test Instructions
Steps for testing existing features, stage progression, bosses, rewards, store purchases, persistence, and SQL inspection.

### I. Remaining Work
Incomplete items, blockers, and decisions requiring approval.

### J. Git Status
Current branch, changed files, and uncommitted changes. Do not commit or push without approval.

## 20. Definition of Done

The project is complete only when:

- Existing working features remain available and their regression checks have been performed.
- Maven refactoring is integrated.
- The requested gameplay features are implemented.
- Java persistence and SQL constraints agree.
- Player progress survives application restarts.
- The database can be inspected through the documented Workbench workflow.
- Tests have been run and their results recorded.
- Remaining limitations are explicitly documented.

**Start by inspecting the uploaded Sourcerer: Crusade project. Preserve its existing functionality, then extend and refactor it to meet every requirement in this specification. Do not replace the original game with a simplified rewrite, and do not declare completion until both the new features and existing-feature preservation have been verified.**

---

## Multi-Agent and Sub-Agent Execution Protocol

### 1. Orchestrator Authority

The Project Manager / Orchestrator is responsible for coordinating the complete implementation.

Responsibilities:
- Inspect the repository and establish the existing-feature inventory.
- Break the project into clear, testable tasks.
- Assign tasks to specialist agents or sub-agents when supported.
- Define shared interfaces and file ownership before parallel work begins.
- Track dependencies, blockers, acceptance criteria, and test results.
- Resolve conflicting implementations and prevent duplicate work.
- Integrate completed work into the main project.
- Ensure all changes preserve existing functionality.
- Produce the final implementation and verification report.

The Orchestrator must not assume that delegated work is correct merely because an agent reports completion.

### 2. Specialist Agents

**Architecture and Maven Agent**
- Inspect the existing Java structure.
- Plan and implement Maven package migration.
- Preserve the existing base package and application entry point.
- Review OOP principles, dependency management, and class responsibilities.
- Coordinate shared interfaces with the other agents.

**Database and Persistence Agent**
- Inspect the existing database implementation and SQL scripts.
- Implement or improve `util.DatabaseConnection`.
- Design compatible tables, constraints, indexes, and migrations.
- Implement persistence for player statistics, progression, inventory, and boss records.
- Use transactions and idempotency safeguards for purchases and rewards.
- Produce MySQL Workbench instructions and verification queries.
- Never execute destructive SQL without explicit approval.

**Gameplay and Progression Agent**
- Extend existing gameplay rather than replacing it.
- Implement the 100-stage world progression.
- Integrate XP, character levels, coins, rewards, boss encounters, and potion usage.
- Preserve existing Python lessons, challenges, enemies, and gameplay rules.
- Prevent duplicate rewards when stages are replayed or operations are retried.

**Python Curriculum and Challenge Agent**
- Audit existing tutorials and challenge evaluation.
- Preserve existing challenge content.
- Implement or extend the difficulty progression.
- Create harder boss challenges at stages 10, 20, 30, 40, 50, 60, 70, 80, 90, and 100.
- Test answer validation and feedback against valid and invalid submissions.
- Avoid executing arbitrary user-submitted code unsafely.

**UI/UX Agent**
- Inspect existing Swing/AWT screens and navigation.
- Extend the world map, player status, store, inventory, battle, and boss screens.
- Preserve existing UI features and workflows.
- Connect UI actions to the established gameplay and persistence layers.
- Prevent duplicate submissions and keep database operations off the Swing Event Dispatch Thread.

**QA and Debugging Agent**
- Establish baseline build and test results.
- Add and run unit, integration, and regression tests.
- Reproduce defects before proposing fixes.
- Verify reward calculations, stage unlocking, purchases, inventory changes, and database persistence.
- Retest affected functionality after every correction.
- Report test evidence and unresolved defects accurately.

**Independent Code Reviewer**
- Review completed changes against the specification.
- Check feature preservation, OOP, package boundaries, SQL consistency, and error handling.
- Identify regressions, duplicate logic, unsafe operations, and unnecessary complexity.
- Verify that implementation claims are supported by tests or direct inspection.
- Request corrections when acceptance criteria are not met.

### 3. Sub-Agent Delegation Rules

Use sub-agents only when the actual execution environment supports them.

For each delegated task, provide:
1. Task objective.
2. Relevant repository files and context.
3. Requirements and constraints.
4. Files the agent may modify.
5. Files or behaviors that must remain unchanged.
6. Dependencies and interfaces.
7. Required tests and acceptance criteria.
8. Expected completion report.

Sub-agents must inspect existing code before editing. They must not independently redesign shared architecture, duplicate another agent's implementation, or overwrite unrelated changes.

If sub-agents are unavailable, execute the same specialist responsibilities sequentially. Never claim that parallel execution occurred when it did not.

### 4. Parallel Execution Strategy

Parallelize only tasks that can safely proceed independently.

Potentially parallel tasks after the initial audit:
- Database schema and persistence design.
- Python challenge content and difficulty design.
- UI layout and navigation design.
- Test planning and existing-feature inventory.

Tasks requiring coordination or sequential execution:
- Final package migration and shared interface decisions.
- Repository integration.
- Database-dependent gameplay implementation.
- End-to-end UI integration.
- Final build, regression testing, and release verification.

The Orchestrator must establish shared contracts before parallel implementation. Avoid simultaneous edits to the same files unless the environment provides a safe, explicit collaboration mechanism.

### 5. Integration and Review Gates

Every delegated task must pass these gates:

1. **Task review:** Confirm that the implementation matches its assigned requirements.
2. **Change review:** Inspect the actual diff and identify unrelated or destructive changes.
3. **Build and test:** Run relevant checks.
4. **Integration review:** Verify compatibility with the other components.
5. **Regression review:** Confirm that existing features remain available.
6. **Final acceptance:** The Orchestrator records evidence and updates the acceptance matrix.

A task is not complete until its changes are integrated and verified.

### 6. Conflict Resolution and Approval

When agents disagree:
- Compare their proposals against the repository, requirements, data integrity, and existing behavior.
- Prefer the smallest maintainable solution that satisfies the specification.
- Do not silently change approved gameplay rules or database contracts.
- Escalate destructive, irreversible, or materially behavior-changing decisions for user approval.

### 7. Agent Completion Report

Every agent must report:
- Files inspected.
- Files created, modified, or removed.
- Implemented behavior.
- Tests executed and actual results.
- Known defects or limitations.
- Dependencies or integration requirements.
- Any change that could affect existing features.

The Orchestrator must independently verify these reports before marking requirements as complete.
