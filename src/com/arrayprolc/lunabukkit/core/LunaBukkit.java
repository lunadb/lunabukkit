package com.arrayprolc.lunabukkit.core;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.arrayprolc.lunadb.LunaStarter;
import com.arrayprolc.lunadb.command.CommandManager;

public class LunaBukkit extends JavaPlugin {

    private int port = 412;
    private String key = "key123";

    @SuppressWarnings("deprecation")
    public void onEnable() {
        setupConfig();
        CommandManager.initCommands();
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            public void run() {
                LunaStarter.main(new String[] { port + "", key, "no" });
            }
        }, 1);
    }

    @SuppressWarnings("deprecation")
    public void onDisable() {
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("static-access")
            public void run() {
                LunaStarter.database.getManager().shutdownServer();
            }
        }, 1);
    }

    public void setupConfig() {
        getConfig();
        reloadConfig();
        if (getConfig().get("port") == null) {
            getConfig().set("port", "412");
            saveConfig();
        }
        if (getConfig().get("accessKey") == null) {
            getConfig().set("accessKey", "key123");
            saveConfig();
        }
        key = getConfig().getString("accessKey");
        try {
            port = Integer.parseInt(getConfig().getString("port"));
        } catch (Exception ex) {
            System.out.println("[lunabukkit] Using default port, " + port);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§cYou do not have permission to use that.");
            return true;
        }
        String result = "";
        boolean first = true;
        for (String string : args) {
            if (first) {
                result += string;
                first = false;
            } else {
                result += " " + string;
            }
        }
        interpret(result);
        return false;
    }
    
    public void interpret(String s) {
        String commandName;
        String[] args;
        if (s.contains(" ")) {
            commandName = s.split(" ")[0];
            args = getArgs(s).split(" ");
        } else {
            commandName = s;
            args = new String[] {};
        }
        for (com.arrayprolc.lunadb.command.Command command : CommandManager.getInstance().commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                try {
                    System.out.println(command.runCommand("127.0.0.1", true, args, true));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
        for (com.arrayprolc.lunadb.command.Command command : CommandManager.getInstance().commands) {
            for (String subName : command.getSubNames()) {
                try {
                    if (subName.equalsIgnoreCase(commandName)) {
                        System.out.println(command.runCommand("127.0.0.1", true, args, true));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;

            }
        }
        System.out.println("[Luna] [ERROR] Unknown command. Use \"help\" command to get a list of commands.");
        return;
    }

    public String getArgs(String s) {
        String remove = s.split(" ")[0];
        s = s.replaceFirst(remove + " ", "");
        return s;
    }


}
