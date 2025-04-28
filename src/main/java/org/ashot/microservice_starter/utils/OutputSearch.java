package org.ashot.microservice_starter.utils;

import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

public class OutputSearch {

    private int currentOccurrence = 1;
    private int currentOccurrencePos = 0;
    private String currentInput = "";
    private final CodeArea codeArea;
    private final Label results;

    public OutputSearch(Label results, CodeArea codeArea){
        this.results = results;
        this.codeArea = codeArea;
    }

    private void resetFind(){
        resetFindIndexes();
        resetFindResults();
        resetCaret();
    }

    public void resetFindIndexes(){
        currentOccurrencePos = 0;
        currentOccurrence = 1;
    }

    private void resetFindResults(){
        results.setText("0/0");
    }

    public void performSearch(String input){
        findNextOccurrence(input);
    }

    private void findNextOccurrence(String input){
        if(findText(input)) {
            moveToText(input);
            currentInput = input;
            currentOccurrence++;
            currentOccurrencePos += input.length();
        }else{
            resetFind();
        }
    }

    private boolean findText(String input){
        if(input.isBlank()) {
            return false;
        };
        if(currentOccurrencePos >= this.codeArea.getText().lastIndexOf(input)){
            resetFindIndexes();
        }
        currentOccurrencePos = this.codeArea.getText().indexOf(input, currentOccurrencePos);
        if(currentOccurrencePos == -1){
            return false;
        }
        results.setText(currentOccurrence + "/" + TextFindUtils.calculateOccurrences(input, codeArea));
        return true;
    }
    //todo add reverse

    private void resetCaret(){
        codeArea.deselect();
    }

    private void moveToText(String input){
        codeArea.selectRange(currentOccurrencePos, currentOccurrencePos + input.length());
        codeArea.requestFollowCaret();
    }

    public String getCurrentInput(){
        return currentInput;
    }

}
