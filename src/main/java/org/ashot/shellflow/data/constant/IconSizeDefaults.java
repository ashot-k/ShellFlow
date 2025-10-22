package org.ashot.shellflow.data.constant;

public enum IconSizeDefaults {
    DEFAULT_ICON_SIZE(16),
    EXECUTE_ICON_SIZE(26),
    CLOSE_ICON_SIZE(22),
    MENU_ITEM_SIZE(18),
    ENTRY_VALIDATION_MESSAGE_ICON(18),
    PATH_BROWSE_ICON_SIZE(DEFAULT_ICON_SIZE.getSize()),
    ;

    private final int size;

    IconSizeDefaults(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
