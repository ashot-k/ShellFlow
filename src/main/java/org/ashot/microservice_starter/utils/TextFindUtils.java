package org.ashot.microservice_starter.utils;

import org.fxmisc.richtext.CodeArea;

public class TextFindUtils {
    public static int calculateOccurrences(String input, CodeArea codeArea){
        if(input.isBlank()){
            return 0;
        }
        int count = 0;
        int idx = 0;
        while(idx <= codeArea.getText().lastIndexOf(input) && codeArea.getText().contains(input)){
            idx = codeArea.getText().indexOf(input, idx) + input.length();
            count++;
        }
        return count;
    }
}
