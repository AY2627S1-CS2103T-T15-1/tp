---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-T15-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all clients in alphabetical order by name.

   * `add --name "John Doe" --phone "+65 9123 4567"` : Adds a client named `John Doe` with an international phone number.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add --name "NAME"`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit INDEX [n/NAME]` can be used as `edit 1 n/John Doe`.

* Parameters can be in any order.<br>
  For example, if the command specifies `--name "NAME" --phone "PHONE_NUMBER"`, the two parameters can be reversed.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a client: `add`

Adds a client using their name and mandatory phone number.

Format: `add --name "NAME" --phone "PHONE_NUMBER"`

Name requirements:

* Contains 1–100 characters after trimming.
* Allows Unicode letters, spaces, apostrophes, hyphens, and periods.
* Must contain at least one letter.
* Consecutive spaces are collapsed when saved.
* Letter case is preserved but ignored when comparing client names.

Phone requirements:

* Contains 7–15 digits after separators are removed.
* Allows digits, spaces, parentheses, and hyphens, with one optional leading `+`.
* Formatting separators are removed when saved; a leading `+` is preserved.

Duplicate handling:

* A client is considered a duplicate only when both their normalized name and phone number match an existing client.
* Clients with the same name but different phone numbers can be added.
* A phone number can be shared by different clients, but the application displays a warning when it is reused.

Invalid values produce these messages:

* Name: `Invalid name: enter 1–100 characters using letters, spaces, apostrophes, hyphens or periods.`
* Phone: `Invalid phone: enter 7–15 digits, with an optional leading +.`

Examples:
* `add --name "John Doe" --phone "+65 9123 4567"`
* `add --name "John Doe" --phone "6123 4567"`

### Listing all clients: `list`

Shows all your clients, sorted alphabetically by name, together with the total number of clients.

Format: `list`

* Clients are sorted by name, ignoring upper and lower case. For example, `alice Tan` is listed before `Bob Lim`.
* The client list is always kept in this order, including after you `add` or `edit` a client and in the results of `find`.
* The index numbers used by `view`, `edit` and `delete` follow this order.
* If a previous command (such as `find`) showed only some clients, `list` shows all of them again.

### Editing a client: `edit`

Edits an existing client's name or phone number.

Format: `edit INDEX [n/NAME] [p/PHONE]`

* Edits the client at the specified `INDEX`. The index refers to the index number shown in the displayed client list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.

Examples:
*  `edit 1 p/91234567` Edits the phone number of the 1st client to be `91234567`.
*  `edit 2 n/Betsy Crower` Edits the name of the 2nd client to be `Betsy Crower`.

### Locating clients by name: `find`

Finds clients whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Clients matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a client: `delete`

Deletes the specified client record from the address book.

Format: `delete INDEX`

* Deletes the client at the specified `INDEX`.
* This cannot be undone, so check the index before you press Enter.
* The index refers to the index number shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd client in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st client in the results of the `find` command.

### Viewing a client: `view`

Shows the name and phone number of the specified client.

Format: `view INDEX`

* Shows the details of the client at the specified `INDEX`.
* The index refers to the index number shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, ...
* The client's details open on their own page, in place of the client list. Running `list`, `find`, `add`, `edit`, `delete` or `clear` successfully returns you to the client list.

Examples:
* `list` followed by `view 2` shows the details of the 2nd client in the address book.
* `find Betsy` followed by `view 1` shows the details of the 1st client in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every successfully executed command. You do not need to save manually.

The **home folder** is the folder containing the AddressBook JAR file. AddressBook uses this folder even when you launch it from another directory. Keep the JAR in a folder where you have permission to write files.

The following files are stored in the home folder:

* `data/addressbook.json`: your contacts, inside the `data` sub-folder.
* `preferences.json`: your window size and position.
* Rotating `addressbook.log` files and their lock file: application logs.

If you move the JAR to another folder, close AddressBook first and move the `data` folder and `preferences.json` alongside it to retain your contacts and window preferences. If the data file is missing, AddressBook creates a new one with sample contacts.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Close AddressBook, then copy the JAR and its `data` folder to a writable folder on the other computer, keeping them together. You can also copy `preferences.json` to retain your window preferences. If AddressBook has already created a data file on the other computer, back it up before replacing it with your original `data/addressbook.json`.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete `preferences.json` from the folder containing the JAR before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add --name "NAME" --phone "PHONE_NUMBER"` <br> e.g., `add --name "James Ho" --phone "+65 9123 4567"`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER]`<br> e.g., `edit 2 n/James Lee`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**View**   | `view INDEX`<br> e.g., `view 2`
**Help**   | `help`
