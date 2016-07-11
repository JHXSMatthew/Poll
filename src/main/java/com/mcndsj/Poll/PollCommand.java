package com.mcndsj.Poll;

import com.mcndsj.Poll.round.Poll;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Matthew on 12/07/2016.
 */
public class PollCommand implements CommandExecutor {

    PollPlugin plugin;
    public PollCommand(PollPlugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("poll.admin")){
            commandSender.sendMessage("没有权限 poll.admin！");
            return true;
        }
        if(strings.length < 1){
            displayHelp(commandSender);
            return true;
        }

        if(strings[0].equals("new")){
            if(strings.length < 2){
                commandSender.sendMessage("/poll new <轮数>");
                return true;
            }
            try {
                int in = Integer.parseInt(strings[1]);
                Poll p = new Poll(plugin,in);
            }catch(Exception e){
                commandSender.sendMessage("/poll new <轮数>, 轮数必须为整数");
                return true;
            }


        }else{
            displayHelp(commandSender);
            return true;
        }

        return true;
    }

    private void displayHelp(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY + "======== Help ======== ");
        sender.sendMessage(ChatColor.GRAY + "/poll new <轮数>");
        sender.sendMessage(ChatColor.AQUA + "同时只能运行一轮,会给所有在线玩家发票!");

    }
}
