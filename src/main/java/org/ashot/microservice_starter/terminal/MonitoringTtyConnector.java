package org.ashot.microservice_starter.terminal;

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

    public void setOutputMonitor(Consumer<String> monitor){
        outputMonitorRef.set(monitor);
    }

    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        int n = delegate.read(buf, offset, length);
        if (n > 0) {
            String output = new String(buf, offset, n);
            Consumer<String> monitor = outputMonitorRef.get();
            if(monitor != null) {
                monitor.accept(output);
            }
        }
        return n;
    }

    // Delegate all other methods to the original ProcessTtyConnector
    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        delegate.write(bytes);
    }

    @Override
    public void write(String s) throws IOException {
        delegate.write(s);
    }

    @Override
    public boolean isConnected() {
        return delegate.isConnected();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
