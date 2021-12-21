package me.aang099.pthcbot;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.configuration.GameConfiguration;

import java.io.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ServerManager {
    private static final String TSERVERPATH = PTHCBot.tServerPath;

    private Process process;

    private final GameConfiguration configuration;

    private Thread processThread;

    private int retryAttempts = 0;

    public ServerManager(GameConfiguration configuration) {
        this.configuration = configuration;

        startProcess();
    }

    private void startProcess() {
        processThread = new Thread(this::startProcessThreaded);
        processThread.start();
    }

    private void startProcessThreaded() {
        PTHCBot.serverRunning = true;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd", "/c", "run.bat");
        File dir = new File(TSERVERPATH);
        processBuilder.directory(dir);

        try {
            process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null && !processThread.isInterrupted()) {
                System.out.println("Log: " + line);
                writeLog(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(retryAttempts > 5) {
                System.out.println("Fucked up");
                end(false);
                System.exit(0);
            }
            // If old process is still running for some reason, destroy it
            PTHCBot.oldProcess.destroyForcibly();
            try {
                Thread.sleep(1000);
                startProcessThreaded();
                retryAttempts++;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                System.out.println("Well now we're really fucked chief");
                System.exit(0);
            }
        }
    }

    private void writeLog(String line) {
        File logDir = new File("./logs");

        logDir.mkdir();

        try {
            FileWriter fw = new FileWriter("./logs/tserverlogs.txt", true);
            fw.write(line + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end(boolean destroy) {
        if(destroy) process.destroyForcibly();
        System.out.println("PTHC ending");
        PTHCBot.pthcCount++;
        try {
            FileWriter writer = new FileWriter("./count.txt");
            writer.write(Integer.toString(PTHCBot.pthcCount));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PTHCBot.serverRunning = false;
        PTHCBot.oldProcess = process;
        processThread.interrupt();
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }
}
