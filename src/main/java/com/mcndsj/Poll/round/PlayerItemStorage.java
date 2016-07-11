package com.mcndsj.Poll.round;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
/**
 * Created by Matthew on 11/07/2016.
 */
public class PlayerItemStorage {

    private String name;
    private ItemStack[] items;
    private ItemStack[] equipments;



    public PlayerItemStorage(Player player){
        name = player.getName();
        items = player.getInventory().getContents();
        equipments = player.getInventory().getArmorContents();
    }



    public void restore(){
        Bukkit.getPlayer(name).getInventory().setContents(items);
        Bukkit.getPlayer(name).getInventory().setArmorContents(equipments);
    }


    public boolean isOwner(Player p ){
        return p.getName().equals(name);
    }




}
