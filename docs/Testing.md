---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

`PersonListPanelTest`, `MainWindowTest`, `CommandBoxTest` and `HelpWindowTest` construct real JavaFX controls and run assertions on the JavaFX application thread.
They require a graphical desktop or a virtual display. On Linux without a desktop, install Xvfb and run
`xvfb-run --auto-servernum ./gradlew clean test`. Linux CI uses the same virtual-display setup.

`MainWindowTest` opens the main window and checks the default client list, `view --id 1`, an invalid
`view --id 99`, returning to the list with `list`, and `exit` through the real command box. It checks
that errors preserve the current detail card and that all FXML placeholders load successfully.
Storage uses a temporary directory. The test also checks that the menu bar and status footer
remain visible. The UI retains the existing command syntax, feedback, and input-clearing behavior.
It also checks that Up/Down can recall a failed command and restore a draft without changing
the result message or the displayed clients.

`CommandHistoryTest` checks command history without starting JavaFX. `CommandBoxTest` checks
Up/Down recall, draft restoration and editing, Enter-to-execute behaviour, failed submissions,
blank input, modifier keys (including Down while browsing), repeated failures and caret movement. Run these tests with
`gradlew test --tests seedu.address.ui.CommandHistoryTest --tests seedu.address.ui.CommandBoxTest`
(Mac/Linux: use `./gradlew`). The Developer Guide also provides
[manual command-history checks](DeveloperGuide.md#recalling-commands).

`HelpContentTest` checks the help catalog without starting JavaFX, including parsing every advertised example with the real command parser. `HelpWindowTest` loads the Help window's FXML and checks command rendering, wrapping and scrolling in a small window, fixed guide controls, URL copying and reopening. The clipboard test restores its previous contents afterwards. Run both classes with
`gradlew test --tests seedu.address.ui.HelpContentTest --tests seedu.address.ui.HelpWindowTest`
(Mac/Linux: use `./gradlew`). See the [manual help-window checks](DeveloperGuide.md#viewing-help)
for menu and F1 entry points, keyboard navigation, offline use and preserving the current client view.

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
