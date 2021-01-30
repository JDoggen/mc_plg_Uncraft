package com.backslide999.uncraft.commands.executors;

import com.backslide999.uncraft.UncraftPlugin;
import com.backslide999.uncraft.commands.Help;
import com.backslide999.uncraft.commands.Recipe;
import com.backslide999.uncraft.commands.Reload;
import com.backslide999.uncraft.commands.Uncraft;
import com.backslide999.uncraft.containers.Mode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UncraftListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if(sender instanceof Player){
            player = (Player) sender;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            new Reload(sender);
            return true;
        } else if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
            if (sender instanceof Player) {
                new Uncraft(player, Mode.ALL);
            } else {
                UncraftPlugin.getInstance().sendPlayerDefaultWarning(sender, "no_command_line");
            }
            return true;
        }else if(args.length == 1 && args[0].equalsIgnoreCase("recipe")){
            if(sender instanceof Player){
                new Recipe(player);
            } else{
                UncraftPlugin.getInstance().sendPlayerDefaultWarning(sender, "no_command_line");
            }
            return true;
        } else if (args.length == 0){
            if(sender instanceof Player){
                new Uncraft(player, Mode.ONCE);
            } else{
                UncraftPlugin.getInstance().sendPlayerDefaultWarning(sender, "no_command_line");
            }
            return true;
        }
        new Help(sender);
        return true;
    }
}
