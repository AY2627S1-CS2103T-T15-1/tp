package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_ADD = new Prefix("--add");
    public static final Prefix PREFIX_DATE = new Prefix("--date");
    public static final Prefix PREFIX_EDIT = new Prefix("--edit");
    public static final Prefix PREFIX_ID = new Prefix("--id");
    public static final Prefix PREFIX_NAME_LONG = new Prefix("--name");
    public static final Prefix PREFIX_NOTE = new Prefix("--note");
    public static final Prefix PREFIX_PHONE_LONG = new Prefix("--phone");
    public static final Prefix PREFIX_TO_NAME = new Prefix("--to-name");
    public static final Prefix PREFIX_TO_PHONE = new Prefix("--to-phone");
    public static final Prefix PREFIX_PLAN = new Prefix("--plan");
    public static final Prefix PREFIX_TYPE = new Prefix("--type");
    public static final Prefix PREFIX_GROUP = new Prefix("--group");
    public static final Prefix PREFIX_UNTAG = new Prefix("--untag");
    public static final Prefix PREFIX_DELETE = new Prefix("--delete");
    public static final Prefix PREFIX_MOVE = new Prefix("--move");
    public static final Prefix PREFIX_FROM = new Prefix("--from");
    public static final Prefix PREFIX_TO = new Prefix("--to");

}
