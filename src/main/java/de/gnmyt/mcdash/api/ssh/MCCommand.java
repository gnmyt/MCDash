package de.gnmyt.mcdash.api.ssh;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.apache.commons.io.FileUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MCCommand implements Command {

    private final StringBuilder commandBuffer = new StringBuilder();
    private OutputStream out;
    private InputStream in;
    private ExitCallback callback;
    private int currentLine = 0;

    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.callback = exitCallback;
    }

    @Override
    public void setErrorStream(OutputStream outputStream) {
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    /**
     * Starts a new ssh session
     * @param channelSession The current {@link ChannelSession}
     * @param environment The current {@link Environment}
     * @throws IOException Will be thrown if the session could not be started
     */
    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        Thread commandExecutionThread = new Thread(this::executeCommands);
        Thread logReadingThread = new Thread(this::sendLogLines);
        commandExecutionThread.start();
        logReadingThread.start();

        channelSession.addCloseFutureListener(future -> {
            commandExecutionThread.interrupt();
            logReadingThread.interrupt();
            callback.onExit(0);
        });
    }

    /**
     * Executes the given command, append the command to the command buffer and send the command to the bukkit server
     */
    private void executeCommands() {
        try {
            while (true) {
                while (in.available() > 0) {
                    int c = in.read();

                    if (c == '\r' || c == '\n') {
                        out.write('\r');
                        String commandString = commandBuffer.toString();
                        executeCommand(commandString);
                        commandBuffer.setLength(0);
                    } else if (c == '\b' || c == 127) {
                        handleBackspace();
                    } else if (c == 3) {
                        callback.onExit(0);
                        break;
                    } else {
                        appendCommandBuffer(c);
                    }
                }

                if (Thread.interrupted()) break;

                Thread.sleep(100);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Sends the log lines to the ssh client
     */
    private void sendLogLines() {
        try {
            while (true) {
                String[] lines = FileUtils.readFileToString(new File("logs/latest.log"), StandardCharsets.UTF_8).split("\n");
                if (lines.length > currentLine) {
                    for (int i = currentLine; i < lines.length; i++) out.write((lines[i] + "\r\n").getBytes());
                    out.flush();
                    currentLine = lines.length;
                }

                if (Thread.interrupted()) break;

                Thread.sleep(1000);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Executes the given command
     * @param commandString The command that should be executed
     */
    private void executeCommand(String commandString) {
        Bukkit.getScheduler().runTask(MinecraftDashboard.getInstance(),
                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString));
    }

    /**
     * Handles the backspace
     * @throws IOException Will be thrown if the backspace could not be handled
     */
    private void handleBackspace() throws IOException {
        if (commandBuffer.length() > 0) {
            commandBuffer.setLength(commandBuffer.length() - 1);
            out.write("\b \b".getBytes());
            out.flush();
        }
    }

    /**
     * Appends the given command to the command buffer
     * @param c The character that should be appended
     * @throws IOException Will be thrown if the command could not be appended
     */
    private void appendCommandBuffer(int c) throws IOException {
        if (c >= 32 && c <= 126) {
            commandBuffer.append((char) c);
            out.write(c);
            out.flush();
        }
    }

    @Override
    public void destroy(ChannelSession channelSession) throws Exception {

    }
}
