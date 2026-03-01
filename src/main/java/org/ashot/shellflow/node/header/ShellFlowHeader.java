package org.ashot.shellflow.node.header;

import javafx.scene.Node;
import javafx.scene.layout.HeaderBar;


public class ShellFlowHeader extends HeaderBar {

    public ShellFlowHeader() {
    }

    public ShellFlowHeader(Node leading, Node center, Node trailing) {
        super(leading, center, trailing);
    }
}
