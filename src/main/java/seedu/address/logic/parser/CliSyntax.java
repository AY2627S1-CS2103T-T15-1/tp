package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_ID = new Prefix("--id");
    public static final Prefix PREFIX_NAME_LONG = new Prefix("--name");
    public static final Prefix PREFIX_NOTE = new Prefix("--note");
    public static final Prefix PREFIX_PHONE_LONG = new Prefix("--phone");
    public static final Prefix PREFIX_TO_NAME = new Prefix("--to-name");
    public static final Prefix PREFIX_TO_PHONE = new Prefix("--to-phone");

}
