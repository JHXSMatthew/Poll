package com.mcndsj.Poll.round;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 12/07/2016.
 */
public class VoteItem {

    private ItemStack item;
    private int score;

    public VoteItem(String name,int score,DyeColor color){
        this.score =score;
        item = new ItemStack(Material.WOOL,1,color.getData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> str = new ArrayList<String>();
        str.add(ChatColor.GRAY + "手持右击投票!");
        str.add(ChatColor.GRAY + "价值 " + score + " 分");
        item.setItemMeta(meta);
    }

    public ItemStack getItem(){
        return item.clone();
    }

    public boolean isItem(ItemStack item){
        if(item == null){
            return false;
        }
        return item.hasItemMeta() && item.getType().equals(this.item.getType()) && item.getData().equals(this.item.getData()) && this.item.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName());
    }

    public int getScore(){
        return score;
    }
}
