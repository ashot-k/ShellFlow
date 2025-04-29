package org.ashot.microservice_starter.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OutputSearch {
    private boolean active;
    private int currentOccurrence = 1;
    private int currentOccurrencePos = 0;
    private String currentInput = "";
    private final CodeArea codeArea;
    private final Label results;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

    private void resetFindIndexToEnd(String input){
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
            stayOnOccurrence(true);
        }else{
            resetFind(true);
            stayOnOccurrence(false);
        }
    }

    private void findPreviousOccurrence(String input){
        if(findPreviousText(input)){
            moveToText(input);
            currentInput = input;
            stayOnOccurrence(true);
        }else{
            resetFind(false);
            stayOnOccurrence(false);
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
        } else {
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
        Platform.runLater(()->{
            codeArea.selectRange(currentOccurrencePos, currentOccurrencePos + input.length());
            codeArea.requestFollowCaret();
        });
    }

    private void stayOnOccurrence(boolean stay){
        if(stay){
            if(!executorService.isTerminated()){
                executorService.shutdownNow();
            }
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(()-> {
                Platform.runLater(()-> codeArea.selectRange(currentOccurrencePos, currentOccurrencePos + currentInput.length()));
                if(!isActive()){
                   executorService.shutdownNow();
                }
            }, 0, 2, TimeUnit.MILLISECONDS);
        }
        else{
            executorService.shutdown();
        }
    }

    public String getCurrentInput(){
        return currentInput;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
