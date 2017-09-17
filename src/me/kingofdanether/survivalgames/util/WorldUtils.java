package me.kingofdanether.survivalgames.util;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.PlayerDirection;
import me.kingofdanether.survivalgames.player.SGPlayer;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.TileEntityChest;
import net.minecraft.server.v1_12_R1.WorldServer;

public class WorldUtils {

	public static void playChestAction(Chest chest, boolean open) {
		Location loc = chest.getLocation();
		WorldServer world = ((CraftWorld)loc.getWorld()).getHandle();
		BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
		TileEntityChest tileChest = (TileEntityChest)world.getTileEntity(pos);
		world.playBlockAction(pos, tileChest.getBlock(), 1, open ? 1 : 0);
	}
	
	public static boolean isOutsideOfBorder(SGPlayer player) {
		Location loc = player.getBukkitPlayer().getLocation();
		WorldBorder border = loc.getWorld().getWorldBorder();
		double x = loc.getX();
		double z = loc.getZ();
		double size = border.getSize();
		return ((x > size || (-x) > size) || (z > size || (-z) > size));
	}
	
	public static Vector vectorPointingTo(Location to, Location from) {
		return to.toVector().subtract(from.toVector()).normalize();
	}
	
	public static Vector randVector(double x, double y, double z) {
		float x1 = (float)-x + (float) (Math.random() * ((x - -x) + 1));
		float y1 = (float)-y + (float) (Math.random() * ((y - -y) + 1));
		float z1 = (float)-z + (float) (Math.random() * ((z - -z) + 1));
		return new Vector(x1,y1,z1);
	}
	
	public static void spawnFirework(Location l, Type type, Color color, Color color1, int power) {
		Firework f = l.getWorld().spawn(l, Firework.class);
		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(type).withColor(color).withFade(color1).build());
		fm.setPower(power);
		f.setFireworkMeta(fm); 
	}
	
//	public void spawnCorpse(Player p, String overrideUsername, Location loc, int facing) {
//		int entityId = getNextEntityId();
//		GameProfile prof = cloneProfileWithRandomUUID(((CraftPlayer) p).getProfile(), "");
//
//		DataWatcher dw = clonePlayerDatawatcher(p, entityId);
//		//dw.watch(10, ((CraftPlayer) p).getHandle().getDataWatcher().getByte(10));
//		DataWatcherObject<Integer> obj = new DataWatcherObject<Integer>(10, DataWatcherRegistry.b);
//		//dw.register(obj, (byte)10);
//		dw.set(obj, (int)0);
//		DataWatcherObject<Byte> obj2 = new DataWatcherObject<Byte>(13, DataWatcherRegistry.a);
//		dw.set(obj2, (byte)0x7F);
//
//		Location locUnder = getNonClippableBlockUnderPlayer(loc, 1);
//		Location used = locUnder != null ? locUnder : loc;
//		used.setYaw(loc.getYaw());
//		used.setPitch(loc.getPitch());
//	}
	
	public static int getNextEntityId() {
		try {
			Field entityCount = Entity.class.getDeclaredField("entityCount");
			entityCount.setAccessible(true);
			int id = entityCount.getInt(null);
			entityCount.setInt(null, id + 1);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return (int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25);
		}
	}
	
	public static GameProfile cloneProfileWithRandomUUID(GameProfile oldProf,
			String name) {
		GameProfile newProf = new GameProfile(UUID.randomUUID(), name);
		newProf.getProperties().putAll(oldProf.getProperties());
		return newProf;
	}
	
	public static Location getNonClippableBlockUnderPlayer(Location loc, int addToYPos) {
		if (loc.getBlockY() < 0) {
			return null;
		}
		for (int y = loc.getBlockY(); y >= 0; y--) {
			Material m = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType();
			if (m.isSolid()) {
				return new Location(loc.getWorld(), loc.getX(), y + addToYPos,
						loc.getZ());
			}
		}
		return null;
	}
	
	public static DataWatcher clonePlayerDatawatcher(Player player, int currentEntId) {
		EntityHuman h = new EntityHuman(((CraftWorld) player.getWorld()).getHandle(),((CraftPlayer) player).getProfile()) {
			public void sendMessage(IChatBaseComponent arg0) {
				return;
			}

			public boolean a(int arg0, String arg1) {
				return false;
			}

			public BlockPosition getChunkCoordinates() {
				return null;
			}

			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean z() {
				return false;
			}
		};
		h.f(currentEntId);
		return h.getDataWatcher();
	}

	public static int getHighestY(Location loc) {
		if (loc.getBlock().getType() != Material.AIR) return loc.getBlockY();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		int locY = loc.getBlockY();
		for (int y = locY; y > 0; y--) {
			Block b = loc.getWorld().getBlockAt(x, y, z);
			if (b.getType() == Material.AIR) continue;
			if (b.getType() == Material.FIRE) continue;
			if (b.getType() == Material.LONG_GRASS) continue;
			return (y + 1);
		}
		return locY;
	}
	
	public static void spawnCorpse(Player p, Location loc, Arena a) {
		try {
			BlockPosition pos = new BlockPosition(p.getLocation().getBlockX(), 0, p.getLocation().getBlockZ());
			playFakeBed(p, pos, loc, a);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	 @SuppressWarnings("deprecation")
	 public static void playFakeBed(Player p, BlockPosition pos, Location loc, Arena a) throws Exception {
		 CraftPlayer p1 = (CraftPlayer) p;
		 loc.setY(getHighestY(loc));
		 MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		 WorldServer nmsWorld = ((CraftWorld) p1.getWorld()).getHandle();
		 GameProfile gameProfile = new GameProfile(p1.getUniqueId(), p1.getPlayerListName());
		 
		 EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
		 npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F), (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F));

		 npc.getBukkitEntity().teleport(new Location(loc.getWorld(), loc.getX(), 0, loc.getZ()));
		 
		 PacketPlayOutBed packetBed = new PacketPlayOutBed();

		 ReflectionUtils.setValue(packetBed, "a", npc.getId());
		 ReflectionUtils.setValue(packetBed, "b", pos);
		 
		 PacketPlayOutEntityTeleport packetTeleport = new PacketPlayOutEntityTeleport();
		 ReflectionUtils.setValue(packetTeleport, "a", npc.getId());
		 ReflectionUtils.setValue(packetTeleport, "b", loc.getX());
		 ReflectionUtils.setValue(packetTeleport, "c", loc.getY() + 0.01D);
		 ReflectionUtils.setValue(packetTeleport, "d", loc.getZ());
		 ReflectionUtils.setValue(packetTeleport, "e", (byte) ((int) (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F)));
		 ReflectionUtils.setValue(packetTeleport, "f", (byte) ((int) (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F)));
		 ReflectionUtils.setValue(packetTeleport, "g", true);
		 
		 for (SGPlayer player : a.getPlayers()) {
			 Location location = p.getLocation().clone();
			 player.getBukkitPlayer().sendBlockChange(location.subtract(0, location.getY(), 0), Material.BED_BLOCK, (byte) 0);

			 CraftPlayer pl = ((CraftPlayer) player.getBukkitPlayer());
			 pl.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			 pl.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			 pl.getHandle().playerConnection.sendPacket(packetBed);
			 //npc.setLocation(location.getX(), location.getY(), location.getZ(), (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F), (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F));
			 pl.getHandle().playerConnection.sendPacket(packetTeleport);
		 } 
		 a.getEntityIds().add(npc.getId());
	 }
	 
	    public static void createNPC(Player player, String npcName) {
	        Location location = player.getLocation();
	        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
	        WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
	        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "§a§l" + npcName);
	        //changeSkin(gameProfile);
	        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
	        npc.setLocation(location.getX(), location.getY(), location.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

	        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
	        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
	        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
	    }

//	    private void changeSkin(GameProfile profile){
//	        String texture = SurvivalGames.getInstance().getConfig().getString("Texture");
//	        String signature = SurvivalGames.getInstance().getConfig().getString("Signature");
//	        profile.getProperties().put("textures", new Property("textures",texture,signature));
//
//	    }
	    
	public static PlayerDirection getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return PlayerDirection.NORTH;
		} else if (22.5 <= rotation && rotation < 67.5) {
			return PlayerDirection.NORTH_EAST;
		} else if (67.5 <= rotation && rotation < 112.5) {
			return PlayerDirection.EAST;
		} else if (112.5 <= rotation && rotation < 157.5) {
			return PlayerDirection.SOUTH_EAST;
		} else if (157.5 <= rotation && rotation < 202.5) {
			return PlayerDirection.SOUTH;
		} else if (202.5 <= rotation && rotation < 247.5) {
			return PlayerDirection.SOUTH_WEST;
		} else if (247.5 <= rotation && rotation < 292.5) {
			return PlayerDirection.WEST;
		} else if (292.5 <= rotation && rotation < 337.5) {
			return PlayerDirection.NORTH_WEST;
		} else if (337.5 <= rotation && rotation < 360.0) {
			return PlayerDirection.NORTH;
		} else {
			return null;
		}
	}
	
	public static void createHelix(Location loc, Particle particle, double radius) {
		loc.add(0.5,0,0.5);
	    for(double y = 0; y <= 50; y+=0.05) {
	        double x = radius * Math.cos(y);
	        double z = radius * Math.sin(y);
	        loc.getWorld().spawnParticle(particle, (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z), 0, 0, 0, 0, 1);
	    }
	}
	
	public static void spawnParticle(Location loc, Particle particle, int amount) {
		loc.getWorld().spawnParticle(particle, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 0, 0, 0, amount);
	}
	
}
