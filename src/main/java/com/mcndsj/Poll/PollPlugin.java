package com.mcndsj.Poll;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew on 11/07/2016.
 */
public class PollPlugin extends JavaPlugin {

    public void onEnable(){
        getCommand("poll").setExecutor(new PollCommand(this));
        getLogger().info("===========================================");
        getLogger().info("                加载完毕.");
        getLogger().info("       Source is open under GPL2.0");
        getLogger().info("     By MatthewYu - YourCraft MiniGame");
        getLogger().info("   中国最好玩的小游戏服务器 - YourCraft MiniGame");
        getLogger().info("===========================================");

    }


    public static void log(String info){
        Bukkit.broadcastMessage(ChatColor.AQUA + "投票 >> " + ChatColor.GRAY + info);
    }
}
