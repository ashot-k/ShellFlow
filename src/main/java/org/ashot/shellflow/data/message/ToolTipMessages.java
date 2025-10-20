package org.ashot.shellflow.data.message;

public class ToolTipMessages {
    private ToolTipMessages() {
    }

    public static final String NAME_FIELD = "Name of the tab in which the command will be ran";

    public static final String COMMAND_FIELD = "The command which will be executed";

    public static final String PATH_FIELD = "The system's path in which the command will be executed";

    public static final String PATH_BROWSE = "Browse";

    public static final String WSL_OPTION = "Will run inside WSL\nIn addition the path browser will attempt to translate windows paths to valid WSL paths";

    public static final String STOP_PROCESS_BUTTON = "Attempt a graceful termination by sending a SIGINT to the current process";

    public static final String DELAY_PER_COMMAND_SLIDER = "Delay per entry executed";


    public static final String FIND_BUTTON = "Open the find component";

    public static final String FONT_SELECTION_MODAL = "Edit terminal font";

    public static final String EXECUTE_BUTTON = "Run entry";

    public static final String CLEAR_OUTPUT_BUTTON = "Clear console";

    public static final String EXPAND_ALL_ENTRIES_BUTTON = "Expand all entries";

    public static final String COLLAPSE_ALL_ENTRIES_BUTTON = "Collapse all entries";
}
