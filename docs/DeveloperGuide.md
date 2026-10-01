---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


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
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

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
2.  Cindy shows all clients in the roster, each with an index number, name and phone number, along with the total number of clients.

    Use case ends.

**Extensions**

* 1a. Trainer includes extra parameters in the request.

    * 1a1. Cindy shows an error message stating that the request does not take any parameters.

      Use case resumes at step 1.

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

* 5a. Trainer does not provide any updated contact details.

    * 5a1. Cindy shows an error message stating that no changes were provided.

      Use case resumes at step 5.

* 5b. One or more updated contact details are in an invalid format.

    * 5b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 5.

**Use case: Schedule a training session**

**MSS**

1.  Trainer requests to view a client's training schedule.
2.  Cindy shows the client's scheduled sessions.
3.  Trainer requests to schedule a session and provides its date, time, and details.
4.  Cindy records the session and shows the updated schedule.

    Use case ends.

**Extensions**

* 1a. The requested client does not exist in the roster.

    * 1a1. Cindy shows an error message.

      Use case ends.

* 2a. The client has no scheduled sessions.

    * 2a1. Cindy shows that the client has no sessions yet.

      Use case resumes at step 3.

* 3a. Trainer leaves out the date or time of the session.

    * 3a1. Cindy shows an error message stating which details are missing.

      Use case resumes at step 3.

* 3b. The given date or time is invalid (e.g., in the wrong format, or a non-existent date such as 30 February).

    * 3b1. Cindy shows an error message describing the expected format.

      Use case resumes at step 3.

* 3c. The new session overlaps with a session the trainer has already scheduled.

    * 3c1. Cindy shows an error message identifying the conflicting session.

      Use case resumes at step 3.

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


### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  All features are accessible using the keyboard.
5.  All commands should produce a result within 2 seconds.
6.  The user should not lose more than 1 minute of work after unexpected closure.
7.  The user should be able to understand the error message within 20 seconds of reading it.

### Glossary

* **Client**: A person who receives fitness coaching from a trainer and whose information is managed in Cindy
* **Client progress**: Changes in a client's recorded fitness metrics or exercise performance over time
* **Client record**: The information Cindy stores about a client, including contact details, training sessions, and progress entries
* **Client roster**: The collection of client records managed by a trainer in Cindy
* **Fitness metric**: A measurable value used to assess a client's physical condition or exercise performance
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Progress entry**: A dated record of a client's fitness metrics, exercise results, or other progress observations
* **Progress history**: A client's progress entries arranged in chronological order
* **Trainer**: A freelance gym trainer who uses Cindy to manage clients
* **Training schedule**: The collection of training sessions arranged for a client
* **Training session**: A scheduled appointment between a trainer and a client, identified by its date, time, and session details

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

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
