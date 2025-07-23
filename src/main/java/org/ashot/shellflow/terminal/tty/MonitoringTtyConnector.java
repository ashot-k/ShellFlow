package org.ashot.shellflow.terminal.tty;

import com.techsenger.jeditermfx.core.ProcessTtyConnector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MonitoringTtyConnector extends ProcessTtyConnector {
    private final ProcessTtyConnector delegate;
    private final AtomicReference<Consumer<String>> outputMonitorRef = new AtomicReference<>();

    public MonitoringTtyConnector(ProcessTtyConnector delegate, Consumer<String> onOutput) {
        super(delegate.getProcess(), StandardCharsets.UTF_8);
        this.delegate = delegate;
        this.outputMonitorRef.set(onOutput);
    }

    public void setOutputMonitor(Consumer<String> monitor) {
        outputMonitorRef.set(monitor);
    }

    @Override
    public String getName() {
        return "MonitoringTtyConnector";
    }

    //todo fix, this method prevents the terminal from expanding horizontally for some reason
    @Override
    public int read(char[] buf, int offset, int length) throws IOException {

        int n = delegate.read(buf, offset, length);
        if (n > 0) {
            String output = new String(buf, offset, n);
            Consumer<String> monitor = outputMonitorRef.get();
            if (monitor != null) {
                monitor.accept(output);
            }
        }
        return n;
    }
}
