package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.AppPaths;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.DataFileLock;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.4";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = AppPaths.getHomeDirectory().resolve("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = AppPaths.getHomeDirectory().resolve("data/cindy.json");
    private static final String MESSAGE_ALREADY_OPEN = "Cindy is already open";
    private static final String MESSAGE_ALREADY_OPEN_DETAILS = "Another Cindy window is using the data file %1$s.\n"
            + "Use that window, or close it and open Cindy again.";

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private DataFileLock dataFileLock;
    private boolean isAlreadyOpen;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing Cindy ]===========================");
        super.init();

        dataFileLock = new DataFileLock(ADDRESS_BOOK_FILE_PATH);
        if (!canUseDataFile(dataFileLock)) {
            // Load and save nothing, so that the copy already open keeps its data
            isAlreadyOpen = true;
            return;
        }

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Returns true if this copy of Cindy may use the data file, taking {@code lock} if it can.
     * Returns false if another copy of Cindy holds the lock. If the lock cannot be taken for another reason,
     * such as a folder that cannot be written to, Cindy starts without it rather than not starting at all.
     */
    protected boolean canUseDataFile(DataFileLock lock) {
        try {
            if (lock.tryAcquire()) {
                return true;
            }
            logger.warning("Another copy of Cindy is using the data file, as " + lock.getLockFilePath()
                    + " is locked. Cindy will close.");
            return false;
        } catch (IOException e) {
            logger.warning("Could not lock " + lock.getLockFilePath() + ". Starting without the lock : "
                    + StringUtil.getDetails(e));
            return true;
        }
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used and saved if {@code storage}'s address book is not found.
     * If saving fails, the sample data will still be used. An empty address book will be used instead if errors occur
     * when reading {@code storage}'s address book.
     */
    protected Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with sample Cindy data.");
                initialData = SampleDataUtil.getSampleAddressBook();
                try {
                    storage.saveAddressBook(initialData);
                } catch (IOException e) {
                    logger.warning("Failed to save data file : " + StringUtil.getDetails(e));
                }
            } else {
                initialData = addressBookOptional.get();
            }
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting Cindy with no client data.");
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        if (isAlreadyOpen) {
            showAlreadyOpenAndExit();
            return;
        }
        logger.info("Starting Cindy " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    private void showAlreadyOpenAndExit() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MESSAGE_ALREADY_OPEN);
        alert.setHeaderText(MESSAGE_ALREADY_OPEN);
        alert.setContentText(String.format(MESSAGE_ALREADY_OPEN_DETAILS, ADDRESS_BOOK_FILE_PATH));
        alert.showAndWait();
        Platform.exit();
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Cindy ] =============================");
        if (isAlreadyOpen) {
            return;
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
        try {
            dataFileLock.release();
        } catch (IOException e) {
            logger.warning("Failed to release " + dataFileLock.getLockFilePath() + " : " + StringUtil.getDetails(e));
        }
    }
}
