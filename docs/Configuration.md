---
  layout: default.md
  title: "Configuration guide"
---

# Configuration guide

When running the packaged JAR, the application uses the folder containing the JAR as its home folder,
regardless of the directory from which it is launched:

* Contacts are saved to `data/addressbook.json` inside the home folder.
* Window preferences are saved to `preferences.json` inside the home folder.
* Rotating `addressbook.log` files and their lock file are created inside the home folder.

The home folder must be writable. The application does not fall back to a different data folder.
To keep existing data when moving the JAR, move its `data` folder and `preferences.json` alongside it.

When running compiled classes through Gradle during development, the home folder is the working directory
(normally the project folder). File locations are fixed by the application; there is no `config.json` setting
to redirect them.
