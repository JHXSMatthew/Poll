package com.mcndsj.Poll.round;

import com.mcndsj.Poll.Config;
import com.mcndsj.Poll.PollPlugin;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Matthew on 11/07/2016.
 */
public class Poll extends BukkitRunnable implements Listener {
    private int round;
    private int[] marks;

    private int current = 0;
    private int count = 0;
    private List<Player> playerList;
    private List<PlayerItemStorage> storages;
    private static List<VoteItem> voteItems;
    private  PollPlugin poll;


    static {
        voteItems = new ArrayList<VoteItem>();
        voteItems.add(new VoteItem(ChatColor.BOLD + "很好",2, DyeColor.ORANGE));
        voteItems.add(new VoteItem(ChatColor.BOLD + "好",1, DyeColor.YELLOW));
        voteItems.add(new VoteItem(ChatColor.BOLD + "一般",0, DyeColor.WHITE));
        voteItems.add(new VoteItem(ChatColor.BOLD + "差",-1, DyeColor.RED));
        voteItems.add(new VoteItem(ChatColor.BOLD + "很差",-2, DyeColor.BLACK));
    }


    public Poll(PollPlugin p, int round){
        marks = new int[round];
        this.round = round;
        playerList = new ArrayList<Player>();
        storages = new ArrayList<PlayerItemStorage>();

        for(Player player : Bukkit.getOnlinePlayers()){
            playerList.add(player);
            storages.add(new PlayerItemStorage(player));
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            if(player.getScoreboard() != null){
                Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
                if(obj == null)
                    continue;
                if(obj.getName().equals("sb")){
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }
        }


        this.poll = p;
        p.getServer().getPluginManager().registerEvents(this,p);
        Bukkit.broadcastMessage(Config.prefix + "新一次投票将在5秒内开始,请做好准备!");
        sendTitle(20,20*3,20,"投票即将开始","您可以通过右键手中物品投票!");
        runTaskTimer(p,20 * 5,20);
    }


    public void run() {
        if(count == 0){
            nextRound();
            return;
        }

        sendActionBar(ChatColor.RED  + ChatColor.BOLD.toString() + "当前投票剩余 " + count +" 秒");
        count --;
    }




    private void nextRound(){
        count = 10;
        current ++;
        if(current > round){
            Bukkit.broadcastMessage(ChatColor.AQUA + "==投票结果==");
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.registerNewObjective("sb","dummy");
            obj.setDisplayName("排名");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            for(int i = 0 ; i < marks.length ; i ++){
                Bukkit.broadcastMessage(ChatColor.YELLOW.toString() +  (i+1)+ " 号得分 " + marks[i] + " 分" );
                setScore(obj,(i+1)+ "号",marks[i]);

            }
            Bukkit.broadcastMessage(ChatColor.AQUA + "==========");

            for(Player p : playerList)
                p.setScoreboard(board);

            new BukkitRunnable(){
                public void run() {
                    for(Player p : Bukkit.getOnlinePlayers() ) {
                       if(p.getScoreboard() != null){
                           Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
                           if(obj == null)
                               continue;
                           if(obj.getName().equals("sb")){
                                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                           }
                       }
                    }
                }
            }.runTaskLater(poll,20 * 60);



            cancel();
        }else{
            for(Player p : playerList){
                if(p.isOnline()){
                    Inventory inv = p.getInventory();
                    inv.clear();
                    p.getInventory().setArmorContents(null);
                    p.sendMessage(Config.prefix + "共计 " + ChatColor.RED  + round +  ChatColor.GRAY + " 轮投票,当前是第 "+ ChatColor.RED + current + ChatColor.GRAY +" 轮.");
                    p.sendMessage(Config.prefix + "您有10秒钟时间进行投票,右击手中物品即可完成投票!");
                    sendTitle(0,20*5,0,current + "号",ChatColor.GRAY + "您可以通过右键手中物品投票!");

                    for(VoteItem item : voteItems)
                        p.getInventory().addItem(item.getItem());
                    p.playSound(p.getLocation(),Sound.ANVIL_BREAK,1F,1F);

                }
            }
        }

    }

    private void setScore(Objective obj,String name, int score){
        Score s = obj.getScore(name);
        s.setScore(score);
    }

    @Override
    public void cancel(){
        for(PlayerItemStorage pis :storages)
            pis.restore();

        dispose();
        super.cancel();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent evt){
        evt.setCancelled(true);
        if(evt.getAction() == Action.PHYSICAL){
            return;
        }

        if(!playerList.contains(evt.getPlayer())){
            return;
        }

        if(evt.getItem() != null){
            if(evt.getItem().getType() != Material.AIR){
                for(VoteItem item : voteItems){
                    if(item.isItem(evt.getItem())){
                        marks[current - 1] += item.getScore();
                        if(current > round) {
                            evt.getPlayer().sendMessage(Config.prefix + "您已经完成所有轮的投票,感谢支持!");
                        }else{
                            evt.getPlayer().sendMessage(Config.prefix + "投票成功,请等待下轮投票.");
                        }
                        evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.LEVEL_UP,1F,1F);
                        evt.getPlayer().getInventory().clear();
                        for(int i = 0 ; i < 9 ; i ++)
                            evt.getPlayer().getInventory().setItem(i,new ItemStack(Material.BARRIER));
                        sendTitle(0,40,0,"",ChatColor.GRAY + "玩家" + evt.getPlayer().getName() + "刚刚完成了本轮投票!");

                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent evt){
        if(playerList.contains((Player)evt.getWhoClicked())){
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent evt){
        if(playerList.contains(evt.getPlayer())){
            evt.setCancelled(true);
        }

    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent evt){
        if(playerList.contains(evt.getPlayer())){
            evt.setCancelled(true);
        }    }

    @EventHandler
    public void onDamaged(EntityDamageEvent evt){
        if(playerList.contains(evt.getEntity())){
            evt.setCancelled(true);
        }
        if(evt instanceof EntityDamageByEntityEvent){
            if(playerList.contains(((EntityDamageByEntityEvent) evt).getDamager())){
                evt.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent evt){
        if(playerList.contains(evt.getPlayer())){
            Iterator iterator = storages.iterator();
            while(iterator.hasNext()){
                PlayerItemStorage st = (PlayerItemStorage) iterator.next();
                if(st.isOwner(evt.getPlayer())){
                    st.restore();
                    iterator.remove();
                    playerList.remove(evt.getPlayer());
                    return;
                }
            }
        }
    }

    private void dispose(){
        HandlerList.unregisterAll(this);
    }


    public void sendTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        for(Player p: playerList) {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

            PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
            connection.sendPacket(packetPlayOutTimes);
            if (subtitle != null) {
                IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
                PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
                connection.sendPacket(packetPlayOutSubTitle);
            }
            if (title != null) {
                IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
                PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
                connection.sendPacket(packetPlayOutTitle);
            }
        }
    }

    public void sendActionBar(String message) {
        for(Player p: playerList){
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte) 2));
        }
    }

}
