package org.ashot.shellflow.node.tab.setup;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.mapper.EntryToCommandMapper;
import org.ashot.shellflow.node.entry.EntryBox;

import java.util.ArrayList;
import java.util.List;

public class EntrySetupTab extends Tab {
    private final FlowPane entryContainer;

    public EntrySetupTab(){
        entryContainer = new FlowPane();
        entryContainer.setAlignment(Pos.TOP_CENTER);
        entryContainer.setVgap(15);
        entryContainer.setPadding(new Insets(20, 0, 0, 15));
        entryContainer.getStyleClass().add("entryContainer");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(entryContainer);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().add(scrollPane);
        HBox.setHgrow(scrollPane,  Priority.ALWAYS);

        setContent(hBox);
        setClosable(false);
        setText("Entry Setup");
    }

    public void addEntryBox(){
        addEntryBox(new Entry());
    }

    public void addEntryBox(Entry entry){
        EntryBox entryBox = new EntryBox(entry);
        entryBox.setOnDeleteButtonAction(_ -> removeEntryBox(entryBox));
        entryBox.setOnExecuteButtonAction(_ ->{
            CommandExecutor.execute(EntryToCommandMapper.entryToCommand(entryBox, false));
        });
        entryContainer.getChildren().add(entryBox);
    }

    public void removeEntryBox(EntryBox entryBox){
        entryContainer.getChildren().remove(entryBox);
    }

    public void loadEntries(){

    }

    public void clearEntryBoxes(){
        entryContainer.getChildren().clear();
    }

    public List<EntryBox> getEntryBoxes() {
        List<EntryBox> entryBoxes = new ArrayList<>();
        for(Node node : entryContainer.getChildren()){
            if(node instanceof EntryBox entryBox){
                entryBoxes.add(entryBox);
            }
        }
        return entryBoxes;
    }

    public List<Entry> getEntries(){
        List<Entry> entries = new ArrayList<>();
        for (Node node : entryContainer.getChildren()){
            if(node instanceof  EntryBox entryBox){
                entries.add(new Entry(entryBox.getNameProperty(), entryBox.getPathProperty(), entryBox.getCommandProperty(), entryBox.isWslProperty()));
            }
        }
        return entries;
    }



    public void addEntryListChangeListener(ListChangeListener<Node> changeListener){
        entryContainer.getChildren().addListener(changeListener);
    }
}
