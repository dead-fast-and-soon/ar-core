package org.hyperfresh.angelsreach;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Effect;
import org.bukkit.Sound;

public class AngelsReach extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	public boolean ensureUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if(meta.spigot().isUnbreakable()) return true;

		short maxDurability = 0;

		Material type = item.getType();
		switch(type) {
			case DIAMOND_SWORD:
				maxDurability = 1562; break;
			case IRON_SWORD:
				maxDurability = 251; break;
			case STONE_SWORD:
				maxDurability = 132; break;
			case WOOD_SWORD:
				maxDurability = 60; break;
			case GOLD_SWORD:
				maxDurability = 33; break;
			case FISHING_ROD:
				maxDurability = 65; break;
			case FLINT_AND_STEEL:
				maxDurability = 65; break;
			case CARROT_STICK:
				maxDurability = 26; break;
			case BOW:
				maxDurability = 385; break;
			case ELYTRA:
				maxDurability = 432; break;
		}

		short durability = (short)(maxDurability - item.getDurability());

		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		setDurability(item, durability, maxDurability);

		return false;
	}

	public short[] getDurability(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		short[] info = new short[2];
		try(Scanner s = new Scanner(meta.getDisplayName())) {
			s.useDelimiter("[^0-9\\-]+");
			int i = 0;
			while(s.hasNext()) {
				if(s.hasNextShort()) {
					info[i] = s.nextShort(); i++;
				} else {
					s.next();
				}
			}
		}
		return info;
	}

	public void setDurability(ItemStack item, short durability, short maxDurability) {
		ItemMeta meta = item.getItemMeta();
		//List<String> lore = Arrays.<String>asList(ChatColor.YELLOW + "Durability: (" + durability + "/" + maxDurability + ")");
		//meta.setLore(lore);
		meta.setDisplayName(ChatColor.YELLOW + item.getType().name() + " (" + durability + "/" + maxDurability + ")");
		item.setItemMeta(meta);
	}

	public void setDurability(ItemStack item, short durability) {
		setDurability(item, durability, getDurability(item)[1]);
	}

	public boolean decDurability(ItemStack item) {
		short[] array = getDurability(item);
		if(array[0] > 0) {
			setDurability(item, (short)(array[0] - 1), array[1]);
			return true;
		} else {
			return false;
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;

		Player player = (Player)e.getDamager();
		ItemStack item = player.getInventory().getItemInHand();
		ensureUnbreakable(item);
		if(!decDurability(item)) {
			World world = player.getWorld();
			Location loc = player.getLocation();
			player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			world.playEffect(loc, Effect.STEP_SOUND, item.getType());
			world.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(!(e.getEntity().getShooter() instanceof Player)) return;

		Player player = (Player)e.getEntity().getShooter();
		ItemStack item = player.getInventory().getItemInHand();
		ensureUnbreakable(item);
		if(!decDurability(item)) {
			World world = player.getWorld();
			Location loc = player.getLocation();
			player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			world.<Material>playEffect(loc, Effect.ITEM_BREAK, item.getType());
		}
	}
}
