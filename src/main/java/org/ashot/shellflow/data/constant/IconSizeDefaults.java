package org.ashot.shellflow.data.constant;

public enum IconSizeDefaults {
    DEFAULT_ICON_SIZE(18),
    EXECUTE_ICON_SIZE(22),
    CLOSE_ICON_SIZE(22),
    MENU_ITEM_SIZE(18),
    PATH_BROWSE_ICON_SIZE(DEFAULT_ICON_SIZE.getSize());

    private final int size;

    IconSizeDefaults(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
