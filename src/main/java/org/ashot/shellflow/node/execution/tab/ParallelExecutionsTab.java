package org.ashot.shellflow.node.execution.tab;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class ParallelExecutionsTab extends ExecutionTab {
    private final TabPane parallelExecutionTabPane;

    public ParallelExecutionsTab(String name, List<SingleExecutionTab> placeholders) {
        this(name);
        this.parallelExecutionTabPane.getTabs().addAll(placeholders);
    }

    public ParallelExecutionsTab(String name) {
        parallelExecutionTabPane = new TabPane();
        parallelExecutionTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        parallelExecutionTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        parallelExecutionTabPane.setTabMaxWidth(TAB_NAME_MAX_WIDTH);
        setContent(parallelExecutionTabPane);
        setText(name.isBlank() ? "Execution - Unknown" : name);
        parallelExecutionTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (c.next() && c.getList().isEmpty()) {
                getTabPane().getTabs().remove(this);
            }
        });
    }

    public List<SingleExecutionTab> getSingularExecutionTabs() {
        return parallelExecutionTabPane.getTabs().stream().filter(SingleExecutionTab.class::isInstance).map(o -> (SingleExecutionTab) o).toList();
    }

    public TabPane getParallelExecutionTabPane() {
        return parallelExecutionTabPane;
    }

    @Override
    public void triggerErrorMode(String errorMessage) {
        Text errorText = new Text(errorMessage);
        errorText.setTextAlignment(TextAlignment.CENTER);
        HBox hbox = new HBox(errorText);
        hbox.setAlignment(Pos.CENTER);
        hbox.setFillHeight(true);
        hbox.setPadding(new Insets(5));
        setContent(hbox);

        getTabPane().widthProperty().addListener((_, _, newValue) -> errorText.setWrappingWidth(newValue.doubleValue() - 50));
    }
}
