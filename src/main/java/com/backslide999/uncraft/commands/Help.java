package com.backslide999.uncraft.commands;

import com.backslide999.uncraft.UncraftPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help {

    public Help(CommandSender sender){
        List<String> help = UncraftPlugin.getInstance().fetchConfigStringList("messages.help");
        UncraftPlugin.getInstance().sendPlayerInfo(sender, help);
    }

}
