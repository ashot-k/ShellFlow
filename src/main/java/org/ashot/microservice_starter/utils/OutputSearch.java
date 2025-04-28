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

    private void resetFind(boolean next){
        if(next) {
            resetFindIndexToStart();
        }else{
            resetFindIndexToEnd(currentInput);
        }
        resetFindResults();
        resetCaret();
    }

    public void resetFindIndexToStart(){
        currentOccurrencePos = 0;
        currentOccurrence = 1;
    }
    public void resetFindIndexToEnd(String input){
        currentOccurrencePos = codeArea.getText().lastIndexOf(input);
        currentOccurrence = TextFindUtils.calculateOccurrences(input, codeArea);
    }

    private void resetFindResults(){
        results.setText("0/0");
    }

    public void performForwardSearch(String input){
        findNextOccurrence(input);
    }

    public void performBackwardSearch(String input){
        findPreviousOccurrence(input);
    }

    private void findNextOccurrence(String input){
        if(findText(input)) {
            moveToText(input);
            currentInput = input;
        }else{
            resetFind(true);
        }
    }
    private void findPreviousOccurrence(String input){
        if(findPreviousText(input)){
            moveToText(input);
            currentInput = input;
        }else{
            resetFind(false);
        }
    }

    private boolean findText(String input){
        if(input.isBlank()) {
            return false;
        };
        if(currentOccurrencePos >= this.codeArea.getText().lastIndexOf(input)){
            resetFindIndexToStart();
        }
        if(currentOccurrence == 1){
            currentOccurrencePos = this.codeArea.getText().indexOf(input , currentOccurrencePos);
        }
        else {
            currentOccurrencePos = this.codeArea.getText().indexOf(input, currentOccurrencePos + input.length());
        }
        if(currentOccurrencePos == -1){
            return false;
        }
        results.setText(currentOccurrence + "/" + TextFindUtils.calculateOccurrences(input, codeArea));
        currentOccurrence++;
        return true;
    }

    private boolean findPreviousText(String input){
        if(input.isBlank()){
            return false;
        }
        if(currentOccurrencePos <= this.codeArea.getText().indexOf(input)){
            resetFindIndexToEnd(input);
        }else {
            currentOccurrencePos = this.codeArea.getText().substring(0, currentOccurrencePos).lastIndexOf(input);
        }
        if(currentOccurrencePos == -1){
            return false;
        }
        results.setText(currentOccurrence + "/" + TextFindUtils.calculateOccurrences(input, codeArea));
        currentOccurrence--;
        return true;
    }

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
