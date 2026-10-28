---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Cindy Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

`UiManager` implements `Ui` and creates the `MainWindow`. The main window creates a `CommandBox`, `ResultDisplay`, `PersonListPanel`, `PersonDetailPanel`, and `StatusBarFooter`. It also creates one `HelpWindow`, reused by the `help` command, Help menu and F1 shortcut. These visible components and `PersonCard` inherit from the abstract `UiPart` class, which loads their FXML layouts and provides access to their JavaFX roots.

`PersonListPanel` observes the filtered client list supplied through `Logic` and creates `PersonCard` objects for its list cells. `PersonDetailPanel` also creates a `PersonCard` for the selected client's header. It displays the client's notes, workout plan and appointments when present, together with up to four session records ordered by date, newest first. When there are no sessions, it displays an empty-state message.

`CommandBox` submits commands through a `CommandExecutor` callback supplied by `MainWindow`. The main window calls `Logic.execute`, displays feedback in `ResultDisplay`, and handles the returned `CommandResult`. A result containing a client switches to the detail panel; other successful commands return to the list unless they request help or exit. Failed commands display their error message and preserve the current panel.

The UI also contains supporting classes that do not inherit from `UiPart`. Each `CommandBox` owns one in-memory `CommandHistory` for recalling input with the Up and Down keys. `HelpContent` holds an immutable catalog of `CommandSummary` objects, which `HelpWindow` reads to build its command cards. The diagram omits JavaFX controls, the nested command callback interface and list-cell class to keep the component relationships readable.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete --id 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete --id 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

`DeleteCommand`, like the other commands that act on one client (e.g. `view`, `edit`, `note`), extends `PersonTargetedCommand`. The parser turns `--id 1` (or `--name NAME`) into a `PersonSelector`, and the command uses it to find the matching clients when executed. If exactly one client matches, the command acts on that client in `executeOn`. If several clients match, which can only happen with `--name`, no client is changed: the list is filtered to the matching clients, and the user is asked to choose one with `--id`. If no client matches, a `CommandException` is thrown (not shown in the diagram).



How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores Cindy's client data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the custom groups (e.g. `morning`, `rehab`) as a list of `Tag` objects in `AddressBook`, in the order they were created. Every custom tag of a `Person` is also one of these groups, so a client can never be in a group that does not exist. `gym` and `personal` are built-in client types, not groups.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list, which is kept sorted alphabetically by name (case-insensitive). It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both Cindy's client data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

#### File locations

`AppPaths#getHomeDirectory()` in `seedu.address.commons.util` provides an absolute, normalized home folder. It resolves the location of its own code source through a URI so that spaces and other encoded characters in the path are handled correctly.

* When packaged in a JAR, the home folder is the folder containing that JAR, regardless of the working directory.
* When running compiled classes, such as through Gradle during development, the home folder is the working directory (normally the project folder).

`MainApp` resolves `data/cindy.json` and `preferences.json` against this home folder before constructing the storage objects. `LogsCenter` uses the same helper for its rotating `cindy.log` files and lock file. It escapes percent characters in the log path because `FileHandler` interprets them as pattern markers.

The application does not search another folder for existing data or fall back to another save location when writing fails. Moving a packaged application therefore requires moving its `data` folder and `preferences.json` alongside the JAR. These paths are anchored to the home folder; the helper does not enforce containment against symbolic links pointing outside it.

`AppPathsTest#getHomeDirectory_packagedJar_usesJarFolderInsteadOfWorkingDirectory()` packages the helper, logging class and a probe into a test JAR. It runs the probe in a separate JVM from a different working directory and verifies that the home folder and generated log are beside the JAR, with no files created in the launch directory. The JAR folder contains spaces and a percent character to cover URI decoding and log-pattern escaping. `MainAppTest#defaultFilePaths_useAbsoluteDevelopmentHome()` verifies the contact, preference and log paths for development runs.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Help window

`HelpContent` provides an immutable list of `CommandSummary` objects in display order. Each summary contains a category, command word, description, syntax, a complete example and an availability flag. The catalog is independent of JavaFX. An entry with `isAvailable` set to `false` is displayed as coming soon. The `list --today` command is implemented by the parser and command layer, but its catalog entry still has this flag set to `false`, so the help window continues to label it as coming soon.

`HelpWindow` renders each summary as a card, adding a heading whenever the category changes. `HelpWindow.fxml` keeps the User Guide controls outside the command reference's `ScrollPane`. The window is resizable, the scroll pane fits its content to the available width, and labels wrap without shrinking below their preferred height. `HelpWindow.css` uses Cindy's existing dark colors and typography. `MainWindow` retains one help window, shared by the `help` command, Help menu and F1 shortcut.

When adding or changing a command, update `HelpContent` and the User Guide's command summary together. Keep entries for each category contiguous and give each distinct command form its own entry with a unique syntax. Use a complete parsable command as the example for implemented entries. Planned entries must have `isAvailable` set to `false`; change it to `true` only when the behavior is implemented, not merely when the parser ignores an unsupported option. The short descriptions are maintained separately from command error messages so that the reference stays concise.

`HelpContentTest` checks command-family and parsed command-type coverage, unique formats, implemented examples, planned entries, category grouping and immutability. `HelpWindowTest` checks that the real FXML displays the catalog and coming-soon label, wraps and scrolls at a small size, keeps the guide controls fixed, copies the URL and reopens without duplicating entries. See the [manual help-window checks](#viewing-help) for entry-point and keyboard checks in the running application.

### Command history

Each `CommandBox` owns one `CommandHistory` in the `seedu.address.ui` package. History belongs to the UI because recalling a command changes the input text without executing it. It does not change the `Logic`, `Model` or `Storage` interfaces. History is held in memory for the lifetime of the command box and is not restored after restarting Cindy.

#### Recording and navigation

`CommandHistory` is independent of JavaFX and owns a private `List<String>`, a navigation cursor and a saved draft. Its operations are:

* `record(String commandText)`: preserves nonblank text verbatim, skips consecutive identical entries and evicts the oldest entry before exceeding 50 commands. It resets navigation even when the entry is skipped.
* `previous(String currentText)`: captures the current draft when browsing begins and returns progressively older commands, stopping at the oldest. An empty history returns the current text unchanged.
* `next(String currentText)`: returns progressively newer commands, then the saved draft. When not browsing, it returns the current text unchanged.
* `resetNavigation()`: ends browsing and discards the saved draft without changing recorded commands. The next `previous` call captures the current input as the new draft.

The cursor always lies between zero and the number of entries, inclusive. The position after the newest entry represents the draft. Bounds checks prevent navigation beyond the oldest entry or the draft, and callers never receive the mutable list. String arguments are checked with `requireNonNull` before state changes. Each instance is used on the JavaFX application thread.

#### Command box integration

On Enter, `CommandBox` ignores blank input and records other submissions before calling `CommandExecutor#execute`. Both parse failures and execution failures therefore remain available for recall. Successful execution clears the field; the existing `ParseException` and `CommandException` handling preserves failed input and shows its error style. The executor dependency is checked with `requireNonNull` during construction.

A `KEY_PRESSED` filter on the command text field handles plain Up and Down, replaces the text, positions the caret at the end and consumes the event. Modified arrow keys and other keys retain their normal behaviour, and other controls have no history filter. Recall never invokes the executor.

The text-change listener resets navigation when the user edits input. During recall, `isRecallingCommand` prevents programmatic text replacement from triggering that reset, so repeated Up/Down presses keep their position and saved draft. A `finally` block clears the flag after the update. The listener continues to clear error styling whenever text changes.

`CommandHistoryTest` covers the entry limit, ordering, duplicate handling, navigation boundaries, draft restoration, reset behaviour, null arguments and instance isolation. `CommandBoxTest` exercises real JavaFX controls and events for recall, Enter submission, failed commands, draft editing, blank input, modified keys and ordinary caret movement. See the [manual command-history checks](#recalling-commands) for checks in the running application.

### Rescheduling an appointment

`appointment --reschedule` moves one of a client's appointments to a new date and time. The sequence diagram below shows how `appointment --reschedule --id 2 --index 1 --date 2026-10-27 --time 18:00` is executed.

<puml src="diagrams/RescheduleSequenceDiagram.puml" alt="Interactions for the `appointment --reschedule` Command" />

Parsing works like it does for `delete` in the [Logic component](#logic-component) section, so the diagram shows it as a single `ref` frame. `AppointmentCommandParser` handles every form of the `appointment` command and picks the form from its flag (`--add`, `--cancel`, `--reschedule`, or `--date`/`--week` for listing). For `--reschedule`, it turns `--id 2` (or `--name NAME`) into a `PersonSelector` and creates a `RescheduleAppointmentCommand` with the selector, the appointment index, and the new date and time.

`RescheduleAppointmentCommand` extends `AppointmentIndexCommand`, which extends `PersonTargetedCommand`, so the client is found the same way as for `delete`. In `executeOn`, the command checks that the client has an appointment at the given index, and creates a new `Appointment` at the new date and time. Because `Person` and `Appointment` are immutable, the old appointment is not changed. Instead, `Person#withAppointments` returns a new `Person` whose list has the new appointment in place of the old one, and `Model#setPerson` replaces the client. The new appointment is never cancelled, so rescheduling a cancelled appointment makes it active again.

The command fails with a `CommandException` (not shown in the diagram) if the index is not valid for the client, or if another of the client's appointments is already at the new date and time.

### Adding clients and detecting duplicate phone numbers

`AddCommandParser` accepts only `--name` and `--phone`. It identifies unsupported long flags using the
quote-aware `ArgumentTokenizer` before validating field values, so `--note` after a valid phone is reported
as an unknown parameter. Text after a closing quote, or text after a valid unquoted phone, receives an
unexpected-text error with usage guidance. Quoted content remains subject to the field's normal validation.

`Phone` removes formatting separators for storage. Its equality and hash code use a comparison key that adds
`+65` to eight-digit local numbers; `81234567` and `+6581234567` therefore compare equally while retaining
their original country-code choice in `value`. Other country codes and number lengths remain distinct.
`Person#isSamePerson` uses normalized name and phone equality to reject duplicate clients; `AddressBook#hasPersonWithPhone`
uses the same phone equality for warnings when different clients share a number. These rules also apply when editing clients.
Phone keyword searches still match digits in the stored value; they do not use the equality comparison key.

`AddCommandParserTest` covers unsupported flags and trailing text. `PhoneTest` and `AddCommandIntegrationTest`
cover equality, hash codes, duplicate rejection and shared-phone warnings for both input orders.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear --confirm`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | new fitness trainer | see usage instructions | learn how to manage my clients in the app without having to ask for help |
| `* * *`  | fitness trainer | add a new client after a trial session | start keeping their contact and training details in one place from day one |
| `* * *`  | fitness trainer | view a client's details before a session | prepare a session that suits that client's needs |
| `* * *`  | fitness trainer | list all my clients | get an overview of everyone I am currently training |
| `* * *`  | fitness trainer | delete a client who has stopped training with me | keep my client list focused on the clients I am actively training |
| `* * *`  | fitness trainer | have my client data saved automatically on my computer | keep my client records between sessions without saving manually or relying on an internet connection |
| `* *`    | fitness trainer | edit a client's details | keep their contact and training information accurate when it changes |
| `* *`    | fitness trainer with many clients | find a client by name | pull up their details quickly without scrolling through my whole client list |
| `* *`    | fitness trainer | tag clients by fitness goal (e.g. weight loss, strength) | group clients with similar goals and reuse suitable training approaches |
| `* *`    | fitness trainer | record a client's fitness goals | design each session to work towards what the client wants to achieve |
| `* *`    | fitness trainer | record a client's injuries or health conditions | avoid exercises that could aggravate an injury or put the client at risk |
| `* *`    | fitness trainer | track how many sessions remain in a client's package | remind the client to renew before their package runs out |
| `* *`    | fitness trainer | view a deleted client's history | pick up where we left off if a former client returns |
| `* *`    | fitness trainer with many clients | sort my clients (e.g. by name) | find the client I need in a long list more easily |
| `* *`    | experienced user | use short commands | update client records quickly between back-to-back sessions |
| `*`      | fitness trainer | track a client's progress metrics over time (e.g. weight, personal bests) | show clients how far they have come and keep them motivated |
| `*`      | fitness trainer | store a workout plan for each client | run each session from a prepared plan instead of writing it from memory |
| `*`      | fitness trainer | log a client's session attendance | spot clients who are missing sessions and check in with them |
| `*`      | fitness trainer | set follow-up reminders for clients | check in with clients on time and keep them engaged between sessions |
| `*`      | fitness trainer | track client payments | see who has outstanding payments without keeping separate records |
| `*`      | fitness trainer | export my client list | back up my records or share them with another trainer if I need cover |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `Cindy` and the **Actor** is the `trainer`, unless specified otherwise.)

**Use case: Add a client**

**MSS**

1.  Trainer requests to view the client roster.
2.  Cindy shows the client roster.
3.  Trainer requests to add a client and provides the client's contact details.
4.  Cindy adds the client to the roster and confirms the addition.

    Use case ends.

**Extensions**

* 2a. The client roster is empty.

  Use case resumes at step 3.

* 3a. Trainer leaves out some required contact details.

    * 3a1. Cindy shows an error message stating which details are missing.

      Use case resumes at step 3.

* 3b. Some of the given contact details are in an invalid format.

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 3.

* 3c. The client is already in the roster.

    * 3c1. Cindy shows an error message stating that the client already exists.

      Use case ends.

**Use case: List all clients**

**MSS**

1.  Trainer requests to list all clients.
2.  Cindy shows all clients in the roster, sorted alphabetically by name, each with their client ID, name and phone number, along with the total number of clients.

    Use case ends.

**Extensions**

* 1a. Trainer includes `--today` in the request.

    * 1a1. Cindy replaces any previous filter and shows only clients with a non-cancelled appointment on the computer's local date at execution time, using the same alphabetical roster layout and client IDs.
    * 1a2. Each matching client appears once, including inactive clients and clients whose appointment time has already passed today. If no clients match, Cindy shows an empty list and reports `No clients have appointments today.`

      Use case ends.

* 1b. Trainer includes an unknown flag, repeats `--today`, or supplies extra arguments.

    * 1b1. Cindy shows usage feedback without changing the current list or detail view.

      Use case ends.

* 2a. The client roster is empty.

    * 2a1. Cindy shows that there are no clients yet and explains how to add one.

      Use case ends.

**Use case: Update client details**

**MSS**

1.  Trainer requests to view the client roster.
2.  Cindy shows the client roster.
3.  Trainer requests to update a specific client's contact details.
4.  Cindy shows the client's current contact details.
5.  Trainer provides the updated contact details.
6.  Cindy updates the client's record and confirms the update.

    Use case ends.

**Extensions**

* 2a. The client roster is empty.

  Use case ends.

* 3a. The requested client does not exist in the roster.

    * 3a1. Cindy shows an error message stating that the client does not exist.

      Use case resumes at step 2.

* 3b. The trainer chooses the client by name, and more than one client's name contains that name.

    * 3b1. Cindy lists those clients, with their client IDs, without updating any of them, and asks the trainer to choose one by ID.

      Use case resumes at step 3.

* 5a. Trainer does not provide any updated contact details.

    * 5a1. Cindy shows an error message stating that no changes were provided.

      Use case resumes at step 5.

* 5b. One or more updated contact details are in an invalid format.

    * 5b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 5.

**Use case: Record a training session**

**MSS**

1.  Trainer requests to view a client's profile.
2.  Cindy shows the client's details and recent session records.
3.  Trainer requests to add a session record using `session --add`, providing the client and date, with an optional note.
4.  Cindy records the session.
5.  Trainer requests to view the client's profile again.
6.  Cindy shows the updated session records.

    Use case ends.

**Extensions**

* 1a. The requested client does not exist in the roster.

    * 1a1. Cindy shows an error message.

      Use case ends.

* 2a. The client has no session records.

    * 2a1. Cindy shows that the client has no sessions yet.

      Use case resumes at step 3.

* 3a. Trainer leaves out the session date.

    * 3a1. Cindy shows an error message stating which details are missing.

      Use case resumes at step 3.

* 3b. The given date is invalid (e.g., in the wrong format, or a non-existent date such as 30 February).

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 3.

* 3c. A session record already exists for the client on the given date.

    * 3c1. Cindy shows an error message directing the trainer to use `session --edit`.

      Use case resumes at step 3.

* 3d. Trainer requests to correct an existing session note using `session --edit`, providing the client, date and note.

    * 3d1. Cindy replaces the note. An empty note clears the session note; the trainer can view the profile again to check it.
    * 3d2. If no session exists on that date, Cindy shows an error message.

      Use case ends.

Session records have a date and an optional note, with at most one record per client per date.
Scheduling uses the separate `appointment --add` command, which accepts a first date and time
and replaces the client's current appointments with four weekly appointments. It does not check for overlaps.

**Use case: Record client progress**

**MSS**

1.  Trainer requests to view a client's progress history.
2.  Cindy shows the client's existing progress records.
3.  Trainer submits a new progress entry with its date and results.
4.  Cindy records the entry and shows the updated progress history.

    Use case ends.

**Extensions**

* 1a. The requested client does not exist in the roster.

    * 1a1. Cindy shows an error message.

      Use case ends.

* 2a. The client has no progress records.

    * 2a1. Cindy shows that the client has no progress records yet.

      Use case resumes at step 3.

* 3a. Trainer leaves out the date or the results of the entry.

    * 3a1. Cindy shows an error message stating which details are missing.

      Use case resumes at step 3.

* 3b. The given date is invalid (e.g., in the wrong format, or a non-existent date such as 30 February).

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 3.

**Use case: View a client**

**MSS**

1.  Trainer requests to list all clients, or to find clients by name.
2.  Cindy shows the matching clients, each with their client ID.
3.  Trainer requests to view a client, giving either the client's ID or part of the client's name.
4.  Cindy shows the client's full details on a separate page, in place of the client list.

    Use case ends.

**Extensions**

* 2a. No clients match.

  Use case ends.

* 3a. No client has the given ID, or no client's name contains the given name.

    * 3a1. Cindy shows an error message.

      Use case resumes at step 2.

* 3b. The given ID is not a positive whole number, or the trainer gives both an ID and a name, or neither.

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 2.

* 3c. More than one client's name contains the given name.

    * 3c1. Cindy lists those clients, with their client IDs, and asks the trainer to choose one by ID.

      Use case resumes at step 3.

* 4a. Trainer runs another command that lists or changes clients.

    * 4a1. Cindy returns to the client list.

      Use case ends.

**Use case: Delete a client**

**MSS**

1.  Trainer requests to view the client roster.
2.  Cindy shows the client roster.
3.  Trainer requests to delete a specific client in the roster.
4.  Cindy deletes the client along with their details and confirms the deletion.

    Use case ends.

**Extensions**

* 2a. The client roster is empty.

  Use case ends.

* 3a. The requested client does not exist in the roster.

    * 3a1. Cindy shows an error message stating that the client does not exist.

      Use case resumes at step 2.

* 3b. The request is in an invalid format.

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 3.

* 3c. The trainer chooses the client by name, and more than one client's name contains that name.

    * 3c1. Cindy lists those clients, with their client IDs, without deleting any of them, and asks the trainer to choose one by ID.

      Use case resumes at step 3.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  All features are accessible using the keyboard.
5.  All commands should produce a result within 2 seconds.
6.  The user should not lose more than 1 minute of work after unexpected closure.
7.  The user should be able to understand the error message within 20 seconds of reading it.

### Glossary

* **Appointment**: A scheduled meeting with a client, identified by its date and time; managed using the `appointment` command
* **Client**: A person who receives fitness coaching from a trainer and whose information is managed in Cindy
* **Client ID**: A positive whole number that Cindy gives a client when they are added. It never changes, and it is never given to another client, even after the client it belonged to is deleted, unless all clients are cleared, which starts the numbering again from 1
* **Client progress**: Changes in a client's recorded fitness metrics or exercise performance over time
* **Client record**: The information Cindy stores about a client, including contact details, training sessions, and progress entries
* **Client roster**: The collection of client records managed by a trainer in Cindy
* **Fitness metric**: A measurable value used to assess a client's physical condition or exercise performance
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Progress entry**: A dated record of a client's fitness metrics, exercise results, or other progress observations
* **Progress history**: A client's progress entries arranged in chronological order
* **Trainer**: A freelance gym trainer who uses Cindy to manage clients
* **Training schedule**: The collection of appointments arranged with clients
* **Training session**: A dated session record for a client with an optional note, managed using `session --add` and `session --edit`; at most one record is stored per client per date

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Adding clients and checking duplicates

Use a disposable copy of Cindy with no clients named `Add Check` or `Shared Phone Check`.

1. Run `add --name "Add Check" --phone 81234567`.<br>
   Expected: A new client with an assigned ID and phone `81234567` is added.
1. Run `add --name "Add Check" --phone "+65 8123 4567"`.<br>
   Expected: A duplicate-client error; no new client or ID is added.
1. Run `add --name "Shared Phone Check" --phone "+65 8123 4567"`.<br>
   Expected: The client is added with a shared-phone warning; the stored phone is `+6581234567`.
1. Repeat with fresh client names, entering the `+65` form first and the local form second.<br>
   Expected: The same-name client is rejected and the different-name client receives the warning.
1. Run `add --name "Extra Check" --phone 91234567 --note "Knee injury"`.<br>
   Expected: `Unknown parameter for add: --note.` with usage guidance; no client is added.
1. Run `add --name "Extra Check" --phone 91234567 morning`, then repeat with the phone in quotes.<br>
   Expected: `Unexpected text after --phone: morning.` with usage guidance; no client is added.

### Viewing help

Use a disposable copy of Cindy and its data for these checks. Opening help should not change any client's details.

1. HELP-001: Opening help from each entry point

   1. Run `list`, note the displayed clients, then run `help`.<br>
      Expected: The result display says `Opened help window.` and one window titled `Cindy Help` opens. The client list is unchanged.
   1. Close the Help window using its window close button, then choose **Help > Help** in the main window.<br>
      Expected: The same command reference opens, and Cindy continues running.
   1. Close Help, focus the main command box and press F1. Repeat with the result display focused.<br>
      Expected: Both uses of F1 open the Help window, including when a text control has focus.

1. HELP-002: Reference content and scrolling

   1. Check the syntax and client-selector guidance above the command cards.<br>
      Expected: It explains placeholders, optional parts, alternatives, persistent client IDs and ambiguous names.
   1. Scroll through the reference.<br>
      Expected: There are 27 command cards in eight groups: Client details, Finding clients, Training information, Client status, Client types and groups, Appointments, Session records and Application. Each has a description, format and example. The reference covers group creation and moving, tagging and untagging, group deletion, appointment scheduling/day/week/cancellation/rescheduling, and session add/edit.
   1. Find the `list --today` card.<br>
      Expected: Its heading says `list (coming soon)` and its description says the option is not implemented yet. Do not treat it as a working filter: the current parser ignores arguments to `list`.
   1. Scroll to the bottom.<br>
      Expected: The `exit` card and keyboard shortcuts are reachable. The User Guide URL and Copy URL button remain visible at the top. Reading examples does not execute them.

1. HELP-003: Resizing and keyboard navigation

   1. Make Help as small as its minimum size, then enlarge it again.<br>
      Expected: Text wraps, full commands remain readable by scrolling vertically, and no horizontal scrolling is needed. The guide controls remain usable.
   1. Use Tab or Shift+Tab to focus Copy URL, then press Space.<br>
      Expected: The button has a visible focus indicator and copies the guide URL.
   1. Move keyboard focus to the command-reference scroll pane, then use Down or Page Down, followed by Up or Page Up.<br>
      Expected: The reference scrolls in the requested direction without executing commands.

1. HELP-004: Copying the guide URL and using help offline

   1. Click Copy URL and paste into an empty text editor.<br>
      Expected: The pasted text is exactly `https://ay2627s1-cs2103t-t15-1.github.io/tp/UserGuide.html`. Copying does not open a browser or modify client data.
   1. With the computer offline, close and reopen Help.<br>
      Expected: All command summaries and examples remain available. Copy URL still works; opening the full guide in a browser requires a connection.

1. HELP-005: Reopening help and preserving the client view

   1. Keep Help open, return to the main window and run `help` again.<br>
      Expected: The existing Help window is focused; no second Help window or duplicate command cards appear.
   1. Close Help, run `view --id ID` using an existing client's ID, then run `help` and close Help again.<br>
      Expected: The same client's details are still displayed with unchanged values. Repeat after filtering with `find --name NAME`; the filtered client list remains unchanged.
   1. Minimize Help and invoke `help` again.<br>
      Expected: The existing window remains minimized, as documented in the User Guide's known issues. Restore it manually; no duplicate window should appear.

### Deleting a client

1. Deleting a client while all clients are being shown

   1. Prerequisites: List all clients using the `list` command, with multiple clients in the list.

   1. Test case: `delete --id 1`<br>
      Expected: The client with ID 1 is deleted from the list. The status message shows the deleted client's details.

   1. Test case: `delete --id 0`<br>
      Expected: No client is deleted. The status message shows error details.

   1. Test case: `delete --name a` (where the names of several clients contain `a`)<br>
      Expected: No client is deleted. The list shows only the clients whose names contain `a`, and the status message asks you to choose one with `--id`.

   1. Other incorrect delete commands to try: `delete`, `delete 1`, `delete --id x` (where no client has the ID x), `delete --id 1 --name a`<br>
      Expected: No client is deleted. The status message shows error details.

1. _{ more test cases … }_

### Listing clients with appointments today

1. Filtering and restoring the full list

   1. Prerequisites: Prepare three clients with IDs 1, 2 and 3. Client 1 has a non-cancelled appointment today, client 2 has only a cancelled appointment today, and client 3 has only an appointment tomorrow. Use the computer's current local date for today.
   1. Run `find --id 3`, then `list --today`.<br>
      Expected: Only client 1 appears, using the same card layout as `list`, with their original ID. The feedback is `Listed 1 client with appointments today`. The earlier search does not restrict the results.
   1. Run `view --id 1`, then `list --today`.<br>
      Expected: The detail view is replaced by the same filtered client list. The command box clears and the menu and status bar remain visible.
   1. Run `list --today --today`, `list --todai`, `list --today tomorrow` and `list 3`.<br>
      Expected: Each command shows usage feedback and leaves the displayed clients unchanged. Invalid text stays in the command box.
   1. Run `list`.<br>
      Expected: All three clients reappear in alphabetical order with their original IDs.

1. Empty results and cancellation

   1. Using the same data, cancel client 1's appointment today with `appointment --cancel --id 1 --index INDEX`, replacing `INDEX` with that appointment's index from the detail view. Run `list --today`.<br>
      Expected: The client list is visible and empty. The feedback is `No clients have appointments today.` Client 2's cancelled appointment does not qualify either client for the list.
   1. Run `list`.<br>
      Expected: All three clients are restored; filtering has not deleted any client or appointment data.

### Recalling commands

1. Navigation, boundaries and draft restoration

   1. Prerequisite: Restart Cindy to begin with an empty history, then focus the command box.
   1. Type `find Bob` without pressing Enter, then press Up and Down.<br>
      Expected: The input stays `find Bob`; no command executes.
   1. Replace the input with `list` and press Enter, then run `find Alice`.
   1. Type `find Bob` without pressing Enter. Press Up twice, then Up once more.<br>
      Expected: The input changes to `find Alice`, then `list`, and stays at `list`. The displayed clients and result message do not change during recall.
   1. Press Down twice, then Down once more.<br>
      Expected: The input changes to `find Alice`, then restores `find Bob`, and stays at `find Bob`.
   1. Press Up twice to recall `list`, then append ` edited`. Press Down.<br>
      Expected: The input stays `list edited`, because editing ended browsing. Up recalls the newest entry, `find Alice`; Down restores `list edited`. The stored `list` entry is unchanged.
   1. Recall `list` and press Enter.<br>
      Expected: Only Enter executes the recalled command, the result shows the client list and the input clears. Up recalls `list`; Down restores the empty input.

1. Failed commands and shortcut scope

   1. Submit `not-a-command`.<br>
      Expected: An error appears and the failed text stays in the command box. Replace it with `draft`, press Up and then Down: the failed command is recalled and `draft` is restored.
   1. Hold Shift, Ctrl, Alt or Command/Windows while pressing Up or Down in the command box.<br>
      Expected: No history entry is recalled. Plain Up still recalls the newest command.
   1. Focus the result display and press Up or Down.<br>
      Expected: The command box text does not change.

1. Capacity, duplicate suppression and session lifetime

   1. Restart Cindy, then submit `find --phone 1`, `find --phone 2`, and so on through `find --phone 51`. These valid searches change only the displayed filter, and history preserves their full text.
   1. Submit `find --phone 51` again, then submit an empty input and an input containing only spaces.
   1. Press Up 50 times, then once more.<br>
      Expected: The first recall is `find --phone 51`; the fiftieth is `find --phone 2`, and further Up presses stay at `find --phone 2`. The oldest command was evicted, and the duplicate and blank submissions used no entries.
   1. Close and reopen Cindy, focus the command box and press Up.<br>
      Expected: The command box stays empty; the previous session's history is unavailable.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
