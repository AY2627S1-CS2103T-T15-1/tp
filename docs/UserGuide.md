---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# Cindy User Guide

Cindy is a **desktop application that helps freelance gym trainers manage their clients, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, Cindy can help you manage your clients faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-T15-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for Cindy.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all clients in alphabetical order by name.

   * `add --name "John Doe" --phone "+65 9123 4567"` : Adds a client named `John Doe` with an international phone number.

   * `delete --id 3` : Deletes the client with ID 3. Each client's ID is shown as `#ID` on their card.

   * `clear` : Deletes all clients.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add --name "NAME"`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit --id ID [--to-name NEW_NAME] [--to-phone NEW_PHONE]` can be used as `edit --id 1 --to-name John Doe`.

* Values can be wrapped in double quotes, which is useful for values containing spaces.<br>
  For example, `--name "John Doe"` and `--name John Doe` mean the same.

* Commands that act on one client (`view`, `edit`, `delete` and `tag`) choose the client with either `--id ID` or `--name NAME`, but not both:
  * `--id ID` chooses the client with that ID. Every client's ID is shown as `#ID` on their card, e.g. `#3`.
  * `--name NAME` chooses the client whose name contains `NAME`, ignoring upper and lower case. For example, `--name john` matches `John`, `John Doe` and `Johnny Tan`.
  * If more than one client's name contains `NAME`, Cindy does not act on any of them. Instead, it lists those clients and asks you to run the command again with `--id`, using one of the IDs shown.
  * Both `--id` and `--name` search all your clients, not only the clients currently shown. For example, after `find --name Alice` shows only Alice, `delete --name Bob` still deletes Bob. Check the result message, which names the client acted on.

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

Cindy gives every new client a unique ID number, shown in the message after the client is added (e.g. `New client added: John Doe (ID: 7); Phone: +6591234567`). A client keeps the same ID for as long as they are in Cindy, and an ID is never given to another client, even after the client it belonged to is deleted. The only exception is `clear`: it deletes all clients and starts the numbering again, so the next client added after `clear` gets ID 1.

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
* If a previous command (such as `find`) showed only some clients, `list` shows all of them again.

### Editing a client: `edit`

Edits an existing client's name or phone number.

Format: `edit --id ID [--to-name NEW_NAME] [--to-phone NEW_PHONE]` or `edit --name NAME [--to-name NEW_NAME] [--to-phone NEW_PHONE]`

* Edits the client chosen by `--id` or `--name`, as described in [the notes about the command format](#features).
* At least one of `--to-name` and `--to-phone` must be provided.
* Existing values will be updated to the input values. New names and phone numbers follow the same rules as in `add`.
* Editing a client does not change their ID.

Examples:
*  `edit --id 1 --to-phone 91234567` Edits the phone number of the client with ID 1 to be `91234567`.
*  `edit --name Betsy --to-name "Betsy Crower"` Edits the name of the only client whose name contains `Betsy` to be `Betsy Crower`.

### Finding clients: `find`

Finds clients by name, phone number or ID, and shows only those clients in the list.

Format: `find [--name NAME] [--phone PHONE] [--id ID]`

* At least one of `--name`, `--phone` and `--id` must be given.
* `--name` finds clients whose names contain `NAME`, ignoring upper and lower case. For example, `--name han` finds `Hans` and `Johan Lee`.
* `--phone` finds clients whose phone numbers contain the digits in `PHONE`. Spaces, parentheses and hyphens in `PHONE` are ignored, so `--phone 9123 4567` finds `91234567`.
* `--id` finds the client with that ID.
* If you give more than one of them, only clients that match all of them are found.
* The clients found are still sorted by name. Use `list` to show all clients again.

Examples:
* `find --name alex` finds `Alex Yeoh` and `Alexis Tan`.
* `find --phone 9123` finds every client whose phone number contains `9123`.
* `find --name tan --phone 8` finds clients whose names contain `tan` and whose phone numbers contain `8`.

### Deleting a client: `delete`

Deletes the specified client record from Cindy.

Format: `delete --id ID` or `delete --name NAME`

* Deletes the client chosen by `--id` or `--name`, as described in [the notes about the command format](#features).
* This cannot be undone, so check the ID before you press Enter.
* The ID of a deleted client is not given to another client, unless you later run `clear`, which starts the numbering again from 1.

Examples:
* `delete --id 2` deletes the client with ID 2.
* `delete --name Betsy` deletes the only client whose name contains `Betsy`. If several clients' names contain `Betsy`, they are listed instead, and nothing is deleted.

### Viewing a client: `view`

Shows the name and phone number of the specified client.

Format: `view --id ID` or `view --name NAME`

* Shows the details of the client chosen by `--id` or `--name`, as described in [the notes about the command format](#features).
* The client's details open on their own page, in place of the client list. Running `list`, `find`, `add`, `edit`, `delete` or `clear` successfully returns you to the client list.

Examples:
* `view --id 2` shows the details of the client with ID 2.
* `view --name alex` shows the details of the only client whose name contains `alex`, such as `Alex Yeoh`. If several clients' names contain `alex`, they are listed instead, so that you can choose one with `view --id`.

### Labelling a client as a gym or personal client: `tag`

Labels a client as a client you train at a gym, or a client you train personally.

Format: `tag --id ID --type TYPE` or `tag --name NAME --type TYPE`, where `TYPE` is `gym` or `personal`

* Labels the client chosen by `--id` or `--name`, as described in [the notes about the command format](#features).
* `TYPE` ignores upper and lower case, so `--type Gym` is the same as `--type gym`.
* A client is either a gym client or a personal client, so labelling a gym client as `personal` replaces their `gym` label, and the other way round. Other tags of the client are kept.
* The label is shown as a tag on the client's card.

Examples:
* `tag --id 2 --type gym` labels the client with ID 2 as a gym client.
* `tag --name bernice --type personal` labels the only client whose name contains `bernice` as a personal client.

### Clearing all entries: `clear`

Deletes all clients from Cindy.

Format: `clear`

* Client IDs start again from 1 after `clear`, so the next client you add gets ID 1. IDs you noted down before clearing no longer refer to the same clients.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

Cindy automatically saves data after every successfully executed command. You do not need to save manually.

The **home folder** is the folder containing the Cindy JAR file. Cindy uses this folder even when you launch it from another directory. Keep the JAR in a folder where you have permission to write files.

The following files are stored in the home folder:

* `data/addressbook.json`: your clients, inside the `data` sub-folder.
* `preferences.json`: your window size and position.
* Rotating `addressbook.log` files and their lock file: application logs.

If you move the JAR to another folder, close Cindy first and move the `data` folder and `preferences.json` alongside it to retain your clients and window preferences. If the data file is missing, Cindy creates a new one with sample clients.

### Editing the data file

Cindy data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, Cindy starts with an empty client list at the next run. The invalid file remains on disk until you run a command (Cindy saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause Cindy to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Close Cindy, then copy the JAR and its `data` folder to a writable folder on the other computer, keeping them together. You can also copy `preferences.json` to retain your window preferences. If Cindy has already created a data file on the other computer, back it up before replacing it with your original `data/addressbook.json`.

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
**Delete** | `delete --id ID` or `delete --name NAME`<br> e.g., `delete --id 3`
**Edit**   | `edit --id ID [--to-name NEW_NAME] [--to-phone NEW_PHONE]` (or `--name NAME` instead of `--id ID`)<br> e.g., `edit --id 2 --to-name "James Lee"`
**Find**   | `find [--name NAME] [--phone PHONE] [--id ID]` (at least one)<br> e.g., `find --name james`
**List**   | `list`
**View**   | `view --id ID` or `view --name NAME`<br> e.g., `view --id 2`
**Tag**    | `tag --id ID --type gym` or `tag --id ID --type personal` (or `--name NAME` instead of `--id ID`)<br> e.g., `tag --id 2 --type gym`
**Help**   | `help`
