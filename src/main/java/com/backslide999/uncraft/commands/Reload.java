package com.backslide999.uncraft.commands;

import com.backslide999.uncraft.UncraftPlugin;
import org.bukkit.command.CommandSender;

public class Reload {

    public Reload(CommandSender sender){
        if(!sender.hasPermission("uncraft.reload")){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(sender, "unauthorized");
            return;
        }

        try{
            UncraftPlugin.getInstance().reload(sender);
            UncraftPlugin.getInstance().sendPlayerDefaultInfo(sender, "reload_success");
        } catch(Exception e) {
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(sender, "reload_error");
        }
    }

}
