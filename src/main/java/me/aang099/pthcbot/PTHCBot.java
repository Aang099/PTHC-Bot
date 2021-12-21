package me.aang099.pthcbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import me.aang099.pthcbot.configuration.GameConfiguration;
import me.aang099.pthcbot.listeners.CommandListener;
import me.aang099.pthcbot.listeners.ComponentListener;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PTHCBot {
    /** The JDA instance */
    public static JDA jda;

    public static final String PREFIX = "!";

    public static String tServerPath;
    public static int pthcCount;
    public static String pthcAnnouncementsChannel;
    public static String cmdChannel;

    public static Communicator communicator;
    public static boolean serverRunning = false;
    public static ServerManager serverManager;

    public static Process oldProcess;

    public static void main(String[] args) {
        try {
            File conFile = new File("config.properties");

            if(conFile.createNewFile())
                throw new IllegalArgumentException("No configuration provided");
            Properties config = new Properties();
            config.load(new FileInputStream(conFile));
            String token = config.getProperty("token");
            pthcCount = Integer.parseInt(config.getProperty("count"));
            tServerPath = config.getProperty("path");
            pthcAnnouncementsChannel = config.getProperty("announcements");
            cmdChannel = config.getProperty("commands");

            JDABuilder builder = JDABuilder.createDefault(token);
            builder.setChunkingFilter(ChunkingFilter.ALL); // enable member chunking for all guilds
            builder.setMemberCachePolicy(MemberCachePolicy.ALL); // ignored if chunking enabled
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);

            builder.setActivity(Activity.watching("for new PTHCs"));

            jda = builder.build();

            jda.addEventListener(new CommandListener());
            jda.addEventListener(new ComponentListener());

            jda.awaitReady();
        } catch (LoginException | IOException | InterruptedException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("ERROR INITIALIZING: ABORTING");
            System.exit(0);
        }

        communicator = new Communicator();
        communicator.start();
    }

    public static void sendMessage(MessageChannel channel, String response) {
        channel.sendMessage(response).queue();
    }

    public static void createPTHC(GameConfiguration configuration) {
        String startFilePath = tServerPath + "\\run.bat";
        File startFile = new File(startFilePath);

        try {
            FileWriter fw = new FileWriter(startFile, false);
            fw.write(configuration.getRunScript());
            fw.close();

            serverManager = new ServerManager(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
