package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class CommandOutputThread implements Runnable{
    private Tab tab;
    private Process process;
    public CommandOutputThread(Tab tab, Process process){
        this.tab = tab;
        this.process = process;
    }
    @Override
    public void run() {
        Text t = (Text) ((ScrollPane) tab.getContent()).getContent();
        try(InputStreamReader isr = new InputStreamReader(process.getInputStream())) {
            int c;
            while((c = isr.read()) >= 0) {
                System.out.print((char) c);
                System.out.flush();
                char finalC = (char) c;
                //add richtextfx instead
                Platform.runLater(()-> t.setText(t.getText() + finalC));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
