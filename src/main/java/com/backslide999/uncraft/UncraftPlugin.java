package com.backslide999.uncraft;

import com.backslide999.library.BasePlugin;
import com.backslide999.uncraft.commands.executors.UncraftListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfigurationOptions;

import java.util.logging.Logger;

public class UncraftPlugin extends BasePlugin {

    private static UncraftPlugin instance;
    public static UncraftPlugin getInstance(){
        return instance;
    }
    public final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        instance = this;

        // Read config file
        this.logInfo("Reading Config file");
        this.reload();

        // Register Commands
        this.logInfo("Registering Commands");
        this.getCommand("Uncraft").setExecutor(new UncraftListener());

    }

    public void reload(){
        this.reload(null);
    }

    public void reload(CommandSender sender) {
        this.reloadConfig();
        FileConfigurationOptions config = getConfig().options().copyDefaults(true);
        saveConfig();
        Constants.reload(sender);
    }





}
