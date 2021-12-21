package me.aang099.pthcbot;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.handlers.CommunicationHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A TCP Server that connects to the Terraria server to communicate with it
 */
public class Communicator extends Thread {
    private static ServerSocket serverSocket;

    private static PrintWriter writer;

    /** The port for the TCP Server to listen on, default is 8989 */
    private static final int port = 8989;

    public Communicator() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void run() {
        while(!interrupted()) {
            try {
                Socket socket = serverSocket.accept();

                System.out.println("Client connected!");

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)));

                sendGraceTime(PTHCBot.serverManager.getConfiguration().graceTime());

                String line;

                // If there is an incoming message, parse and handle it
                while((line = reader.readLine()) != null) {
                    parseAndHandleMessage(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // If the server closed the connection, end the server handler
                if(e.getMessage().toLowerCase().contains("connection reset")) PTHCBot.serverManager.end(false);
            }
        }
    }

    /**
     * Parses the incoming message into usable data and then passes it on to the handler
     * @param message The un-parsed, raw message
     */
    private static void parseAndHandleMessage(String message) {
        String[] temp = message.split(":");
        List<String> tempList = new ArrayList<>();
        tempList.add(temp[0]);
        tempList.addAll(List.of(temp[1].split(",")));
        String[] unParsedArray = tempList.toArray(new String[0]);

        String rawType = unParsedArray[0];

        InMessageTypes type = InMessageTypes.valueOf(rawType);

        String[] args = Arrays.copyOfRange(unParsedArray, 1, unParsedArray.length);

        switch(type) {
            case REQUESTUSERAPPROVAL -> CommunicationHandler.handleRequestUserApproval(args[0], Integer.parseInt(args[1]));
            case ANNOUNCEWINNER ->   CommunicationHandler.handleAnnounceWinner(args[0]);
        }
    }

    /**
     * This sends the grace time, it should be run whenever a client connects
     * @param minutes The grace length in minutes
     */
    public static void sendGraceTime(int minutes) {
        sendMessage(OutMessageTypes.SETGRACETIME, Integer.toString(minutes));
    }

    /**
     * The response to <code>InMessageTypes.RequestUserApproval</code>
     * @param discordId The discordId of the user who allowed or denied, this should be null if the user denied
     * @param playerIndex The player index so that the server can match the response to the appropriate player
     */
    public static void sendUserApprovalResponse(String discordId, int playerIndex) {
        sendMessage(OutMessageTypes.USERAPPROVALRESPONSE, discordId, Integer.toString(playerIndex));
    }

    /**
     * Converts the message type and arguments into a string readable by the client
     * @param type The message type (i.e <code>OutMessageTypes.SETGRACETIME</code>)
     * @param args The arguments for the specific message type, these must follow a format for that type
     */
    private static void sendMessage(OutMessageTypes type, String... args) {
        StringBuilder fullMessage = new StringBuilder(type.name() + ":");

        for(int i = 0; i < args.length; i++) {
            fullMessage.append(args[i]);
            if (i != args.length - 1) fullMessage.append(",");
        }

        writer.write(fullMessage.toString());
        writer.flush();
    }

    /** The incoming message types */
    public enum InMessageTypes {
        REQUESTUSERAPPROVAL,
        ANNOUNCEWINNER
    }

    /** The outgoing message types */
    public enum OutMessageTypes {
        SETGRACETIME,
        USERAPPROVALRESPONSE
    }
}
