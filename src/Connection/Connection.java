package Connection;

/**
 * Created by Yahang Wu on 2017/4/5.
 * COMP90015 Distributed System Project1 EZServer
 * This file provide the methods to read the command line
 * and get instruction from the command line
 * Also, it read the command line and convert it to the json string
 * in order to sent to the server
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class Connection {

    //Debug Mode, true is on, false is off.
    public static boolean debugSwitch = false;
    public String host = "1.1.1.1";
    public int port = 3000;

    String channel = "";
    String description = "";
    String name = "";
    String owner = "";
    String secret = "";
    String ezserver = null;
    String servers = "";
    String tags = "";
    String uri = "";
    JsonArray tagsArray = new JsonArray();

    /**
     * @param args the command line arguments
     * @return commandObject.toString() the json string which contains
     * the command and attributes
     */
    public String clientCli(String[] args) {

        //To create the json object from the command line
        JsonObject commandObject = new JsonObject();

        Options options = new Options();

        options.addOption("channel", true, "channel");
        options.addOption("debug", false, "print debug information");
        options.addOption("description", true, "resource description");
        options.addOption("exchange", false,
                "exchange server list with server");
        options.addOption("fetch", false, "fetch resources from server");
        options.addOption("host", true,
                "server host, a domain name or IP address");
        options.addOption("name", true, "resource name");
        options.addOption("owner", true, "owner");
        options.addOption("port", true, "server port, an integer");
        options.addOption("publish", false, "publish resource on server");
        options.addOption("query", false, "query for resources from server");
        options.addOption("remove", false, "remove resource from server");
        options.addOption("secret", true, "secret");
        options.addOption("servers", true,
                "server list, host1:port1,host2:port2,...");
        options.addOption("share", false, "share resource on server");
        options.addOption("tags", true, "resource tags, tag1,tag2,tag3,...");
        options.addOption("uri", true, "resource URI");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            //help(options);
            System.out.println("I am here.");
        }

        assert cmd != null;

        if (cmd.hasOption("host")) {
            host = cmd.getOptionValue("host");
        }

        if (cmd.hasOption("port")) {
            port = Integer.parseInt(cmd.getOptionValue("port"));
        }

        if (cmd.hasOption("debug")) {
            debugSwitch = true;
        }

        if (cmd.hasOption("secret")) {
            secret = cmd.getOptionValue("secret");
        }

        if (cmd.hasOption("name")) {
            name = cmd.getOptionValue("name");
        }

        if (cmd.hasOption("channel")) {
            channel = cmd.getOptionValue("channel");
        }

        if (cmd.hasOption("description")) {
            description = cmd.getOptionValue("description");
        }

        if (cmd.hasOption("tags")) {
            tags = cmd.getOptionValue("tags");
            String[] array = tags.split(",");
            for (String str : array) {
                tagsArray.add(str);
            }
        }

        if (cmd.hasOption("uri")) {
            uri = cmd.getOptionValue("uri");
        }

        if (cmd.hasOption("owner")) {
            owner = cmd.getOptionValue("owner");
        }

        if (cmd.hasOption("servers")) {
            servers = cmd.getOptionValue("servers");
        }

        if (cmd.hasOption("publish") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "PUBLISH");
            JsonObject resource = new JsonObject();
            resourceGenerator(resource);
            commandObject.add("resource", resource);

            return commandObject.toString();
        }

        if (cmd.hasOption("query") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "QUERY");
            commandObject.addProperty("relay", true);
            JsonObject resorceTemplate = new JsonObject();
            resourceGenerator(resorceTemplate);
            commandObject.add("resourceTemplate", resorceTemplate);

            return commandObject.toString();
        }

        if (cmd.hasOption("remove") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "REMOVE");
            JsonObject resource = new JsonObject();
            resourceGenerator(resource);
            commandObject.add("resource", resource);

            return commandObject.toString();
        }

        if (cmd.hasOption("share") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "SHARE");
            commandObject.addProperty("secret", secret);
            JsonObject resource = new JsonObject();
            resourceGenerator(resource);
            commandObject.add("resource", resource);

            return commandObject.toString();
        }

        if (cmd.hasOption("exchange") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "EXCHANGE");

            JsonObject server1 = new JsonObject();
            JsonObject server2 = new JsonObject();

            String[] list = servers.split(",");
            String[] hostOne = list[0].split(":");
            String[] hostTwo = list[1].split(":");
            server1.addProperty("hostname", hostOne[0]);
            server1.addProperty("port", hostOne[1]);
            server2.addProperty("hostname", hostTwo[0]);
            server2.addProperty("port", hostTwo[1]);

            JsonArray serverList = new JsonArray();
            serverList.add(server1);
            serverList.add(server2);
            commandObject.add("serverList", serverList);

            System.out.println(commandObject.toString());
            return commandObject.toString();
        }

        if (cmd.hasOption("fetch") && commandObject.get("command") == null) {
            commandObject.addProperty("command", "FETCH");
            JsonObject resourceTemplate = new JsonObject();
            resourceGenerator(resourceTemplate);
            commandObject.add("resourceTemplate", resourceTemplate);

            return commandObject.toString();
        }

        return null;
    }

    /**
     * The common method to generate the JsonObject resource
     *
     * @param resource the resource JsonObject
     */
    private void resourceGenerator(JsonObject resource) {
        resource.addProperty("name", name);
        resource.add("tags", tagsArray);
        resource.addProperty("description", description);
        resource.addProperty("uri", uri);
        resource.addProperty("channel", channel);
        resource.addProperty("owner", owner);
        resource.addProperty("ezserver", ezserver);
    }

    /**
     * @param args the command line arguments
     */
    public static void serverCli(String[] args) {

        String advertisedHostname = "EZShare Server";
        String connectionIntervalLimit = "";
        int exchangeInterval = 600;
        int port = 0;
        String secret = "kfjdskfjaskldfjkalsjfk";

        Options options = new Options();

        options.addOption("advertisedhostname", true, "advertised hostname");
        options.addOption("connectionintervallimit", true,
                "connection interval limit in seconds");
        options.addOption("exchangeinterval", true,
                "exchange interval in seconds");
        options.addOption("port", true, "server port, an integer");
        options.addOption("secret", true, "secret");
        options.addOption("debug", false, "print debug information");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            //help(options);
            System.out.println("I am here.");
        }

        assert cmd != null;

        if (cmd.hasOption("advertisedhostname")) {
            advertisedHostname += cmd.getOptionValue("advertisedhostname");
            System.out.println("advertisedhostname succeed: " +
                    advertisedHostname);
        } else {
            System.out.println(advertisedHostname);
        }

        if (cmd.hasOption("connectionintervallimit")) {
            connectionIntervalLimit =
                    cmd.getOptionValue("connectionintervallimit");
            System.out.println("connectionintervallimit succeed: " +
                    connectionIntervalLimit);
        } else {
            connectionIntervalLimit =
                    "The user does not provide connection interval limit";
            System.out.println(connectionIntervalLimit);
        }

        if (cmd.hasOption("exchangeinterval")) {
            exchangeInterval +=
                    Integer.parseInt(cmd.getOptionValue("exchangeinterval"));
            System.out.println("Exchange interval succeed: " +
                    exchangeInterval);
        } else {
            System.out.println("default exchange interval: " +
                    exchangeInterval);
        }

        if (cmd.hasOption("port")) {
            port = Integer.parseInt(cmd.getOptionValue("port"));
            System.out.println("port succeed: " + port);
        } else {
            System.out.println("port failed, port remain: " + port);
        }

        if (cmd.hasOption("secret")) {
            secret += cmd.getOptionValue("secret");
            System.out.println("secret succeed: " + secret);
        } else {
            System.out.println(secret);
        }

        if (cmd.hasOption("debug")) {
            System.out.println("debug succeed");
        } else {
            System.out.println("debug failed");
        }
    }

}