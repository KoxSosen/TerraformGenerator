package org.terraform.coregen;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.terraform.main.TerraformGeneratorPlugin;

public class PopulatorDataPostGen extends PopulatorDataAbstract {
	private World w;
	private Chunk c;
	public PopulatorDataPostGen(Chunk c){
		this.w = c.getWorld();
		this.c = c;
	}

	@Override
	public Material getType(int x, int y, int z) {
		return w.getBlockAt(x,y,z).getType();
	}

	@Override
	public BlockData getBlockData(int x, int y, int z) {
		return w.getBlockAt(x,y,z).getBlockData();
	}

	@Override
	public void setType(int x, int y, int z, Material type) {
		//if(type == Material.DIRT) Bukkit.getLogger().info("Set " + x + "," + y + "," + z + " to dirt.");
	
		w.getBlockAt(x, y, z).setType(type,false);
	}

	@Override
	public void setBlockData(int x, int y, int z, BlockData data) {
		w.getBlockAt(x,y,z).setBlockData(data,false);
	}

	@Override
	public Biome getBiome(int rawX, int rawY, int rawZ) {
		return w.getBlockAt(rawX,rawY,rawZ).getBiome();
	}

	@Override
	public int getChunkX() {
		return c.getX();
	}

	@Override
	public int getChunkZ() {
		return c.getZ();
	}

	@Override
	public void addEntity(int rawX, int rawY, int rawZ, EntityType type) {
		c.getWorld().spawnEntity(new Location(c.getWorld(),rawX,rawY,rawZ), type);
	}

	@Override
	public void setSpawner(int rawX, int rawY, int rawZ, EntityType type) {

		w.getBlockAt(rawX, rawY, rawZ).setType(Material.SPAWNER,false);
	}

	@Override
	public void lootTableChest(int x, int y, int z, TerraLootTable table) {
		//Can't do that for ya, sorry
		if(!w.getBlockAt(x,y,z).getType().toString().contains("CHEST")){
			TerraformGeneratorPlugin.logger.error("Attempted to set loot table to a non chest @ " + x +"," + y + "," + z);
		}
	}

}