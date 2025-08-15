package org.ashot.shellflow.node.modal;

import atlantafx.base.controls.Spacer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.config.Config;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.utils.NodeUtils;

import java.util.List;

public class FontSelectionDialog extends VBox {
    public static final List<Font> fontList = List.of(
            Font.font("Cascadia Mono"),
            Font.font("Consolas"),
            Font.font("Courier New")
    );
    private static Config config = Main.getConfig();

    public static SimpleObjectProperty<Font> selectedFont = new SimpleObjectProperty<>(config.getTerminalFontFamily());
    public static SimpleDoubleProperty selectedSize = new SimpleDoubleProperty(config.getTerminalFontSize());
    private final ComboBox<Double> fontSizeComboBox = new ComboBox<>(
    );
    private final ListView<Text> list = new ListView<>();

    public FontSelectionDialog(Runnable closeHandler){
        this((int) (Main.getPrimaryStage().getWidth() / 2), (int) (Main.getPrimaryStage().getHeight() / 3), closeHandler);
    }

    public FontSelectionDialog(int width, int height, Runnable closeHandler) {
        super();

        Text title = new Text("Terminal font settings");
        title.setFont(Fonts.title);

        Text fontFamilySectionTitle= new Text("Font family");
        fontFamilySectionTitle.setFont(Fonts.subTitle);
        List<Font> fonts = FXCollections.observableArrayList(fontList);
        for (Font f : fonts){
            list.getItems().add(new Text(f.getFamily()));
        }
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Text initialSelection = list.getItems().stream().filter(e -> e.getText().equals(selectedFont.get().getFamily())).toList().getFirst();
        list.getSelectionModel().select(initialSelection);
        list.getSelectionModel().selectedItemProperty().addListener((_, _, item) -> {
            if(item instanceof Text t){
                Font newSelection = fontList.stream().filter(font -> font.getFamily().equals(t.getText())).toList().getFirst();
                ThemedSettingsProvider.updateFont(newSelection.getFamily(), selectedSize.get());
                selectedFont.set(newSelection);
                config.saveProperty(ConfigProperty.TERMINAL_FONT_FAMILY, selectedFont.get().getFamily());
            }
        });
        list.setMinHeight(250);
        VBox.setVgrow(list, Priority.ALWAYS);

        Text fontSizeSectionTitle= new Text("Font size");
        fontSizeSectionTitle.setFont(Fonts.subTitle);
        List<Double> sizes = List.of(9.0, 11.0, 12.0, 14.0, 16.0, 20.0, 22.0, 24.0);
        double sizeOptionSelected = sizes.stream().filter(e -> e.equals(selectedSize.get())).toList().getFirst();
        fontSizeComboBox.getItems().addAll(sizes);
        fontSizeComboBox.valueProperty().addListener((_, _, newValue) -> {
            ThemedSettingsProvider.updateFont(selectedFont.get().getFamily(), newValue);
            selectedSize.set(newValue);
            config.saveProperty(ConfigProperty.TERMINAL_FONT_SIZE, String.valueOf(selectedSize.get()));
        });
        fontSizeComboBox.getSelectionModel().select(sizeOptionSelected);

        Button closeButton = new Button();
        closeButton.setOnAction(e -> closeHandler.run());
        NodeUtils.setupCloseIconButton(closeButton);
        HBox titleContainer = new HBox(title, new Spacer(), closeButton);
        titleContainer.setAlignment(Pos.TOP_CENTER);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPadding(Insets.EMPTY);

        VBox fontFamilySectionContainer= new VBox(5, fontFamilySectionTitle, list);
        VBox fontSizeSectionContainer = new VBox(5, fontSizeSectionTitle, fontSizeComboBox);
        VBox titleSectionContainer = new VBox(5, titleContainer, separator);

        getChildren().setAll(titleSectionContainer, fontFamilySectionContainer, fontSizeSectionContainer);
        setMinWidth(width);
        setMaxSize(width, height);
        setSpacing(10);
        getStyleClass().addAll("custom-dialog");
    }

    public ComboBox<Double> getFontSizeComboBox() {
        return fontSizeComboBox;
    }

    public ListView<Text> getList() {
        return list;
    }
}
