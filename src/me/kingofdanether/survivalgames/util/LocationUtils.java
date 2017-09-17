package me.kingofdanether.survivalgames.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.SGChest;

public class LocationUtils {
	
	@Deprecated
	public static Location parseLocation(YamlConfiguration data, String path) {
		World w = Bukkit.getWorld(data.getString(path + ".world"));
		double x = data.getDouble(path + ".x");
		double y = data.getDouble(path + ".y");
		double z = data.getDouble(path + ".z");
		float yaw = (float)data.getDouble(path + ".yaw");
		float pitch = (float)data.getDouble(path + ".pitch");
		return new Location(w,x,y,z,yaw,pitch);
	}
	
	public static Location parseLocation(String location, String seperator) {
		String[] loc = location.split(seperator);
		World w = Bukkit.getWorld(loc[0]);
		double x = Double.valueOf(loc[1]);
		double y = Double.valueOf(loc[2]);
		double z = Double.valueOf(loc[3]);
		if (loc.length >= 6) {
			float yaw = Float.valueOf(loc[4]);
			float pitch = Float.valueOf(loc[5]);
			return new Location(w,x,y,z,yaw,pitch);
		}
		return new Location(w,x,y,z);
	}
	
	public static Location parseSimpleLocation(String location, String seperator) {
		String[] loc = location.split(seperator);
		World w = Bukkit.getWorld(loc[0]);
		double x = Double.valueOf(loc[1]);
		double y = Double.valueOf(loc[2]);
		double z = Double.valueOf(loc[3]);
		return new Location(w,x,y,z);
	}
	
	public static String locToSavableString(Location l, String seperator) {
		World w = l.getWorld();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		float yaw = l.getYaw();
		float pitch = l.getPitch();
		return String.valueOf(w.getName() + seperator + x + seperator + y + seperator + z + seperator + yaw + seperator + pitch);
	}
	
	public static String chestToString(SGChest chest, int tier, String seperator) {
		Location l = chest.getChest().getLocation();
		World w = l.getWorld();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		float yaw = l.getYaw();
		float pitch = l.getPitch();
		return String.valueOf(w.getName() + seperator + x + seperator + y + seperator + z + seperator + yaw + seperator + pitch + seperator + tier);
	}
	
	public static String locToString(Location l) {
		if (l == null) return StringUtils.colorize("&4Location corrupt or does not exist!");
		return StringUtils.colorize("&e" + l.getWorld().getName() + "&6, &e" + l.getBlockX() + "&6, &e" + l.getBlockY() + "&6, &e" + l.getBlockZ());
	}
	
	public static boolean locEqualsLoc(Location l1, Location l2) {
		if (l1.getWorld().getName().equals(l2.getWorld().getName()) && l1.getBlockX() == l2.getBlockX() && 
				l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ()) {
			return true;
		}
		return false;
	}
	
	public static boolean locIsSimilar(Location l1, Location l2) {
		if (l1.getWorld().getName().equals(l2.getWorld().getName()) && l1.getBlockX() == l2.getBlockX() && l1.getBlockZ() == l2.getBlockZ()) {
			return true;
		}
		return false;
	}
	
	public static List<Location> getSphere(Location centerBlock, int radius, boolean hollow) {  
        List<Location> circleBlocks = new ArrayList<Location>();
        
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                      
                        Location l = new Location(centerBlock.getWorld(), x, y, z);
                      
                        circleBlocks.add(l); 
                    }         
                }
            }
        }  
        return circleBlocks;
    }
	
	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<Block>();
        
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                for(int y = bottomBlockY; y <= topBlockY; y++) {
                    
                	Block block = loc1.getWorld().getBlockAt(x, y, z);
                    
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
	
	public static List<Block> chestsFromTwoPoints(final Location loc1, final Location loc2) {
		final List<Block> blocks = new ArrayList<Block>();

        
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                for(int y = bottomBlockY; y <= topBlockY; y++) {

                	Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if (block.getState() instanceof Chest) {
                    	blocks.add(block);
                    }
        		
                }
            }
        }
        return blocks;
    }
	
	public static Location randomLocation(Location min, Location max) {
		Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), min.getY(), Math.abs(max.getZ() - min.getZ()));
		return new Location(min.getWorld(), (Math.random() * range.getX()) + (min.getX() <= max.getX() ? min.getX() : max.getX()), range.getY(), (Math.random() * range.getZ()) + (min.getZ() <= max.getZ() ? min.getZ() : max.getZ()));
	}
	
	public static Player nearLocation(Location l) {
		for (Entity en : l.getWorld().getNearbyEntities(l, 1, 1, 1)) {
			if (en instanceof Player) {return (Player)en;}
		}
		return null;
	}

}
