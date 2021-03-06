package org.terraform.structure.village.plains;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.terraform.coregen.PopulatorDataAbstract;
import org.terraform.data.SimpleBlock;
import org.terraform.data.Wall;
import org.terraform.schematic.SchematicParser;
import org.terraform.schematic.TerraSchematic;
import org.terraform.structure.room.CubeRoom;
import org.terraform.structure.room.RoomPopulatorAbstract;
import org.terraform.utils.BlockUtils;
import org.terraform.utils.GenUtils;
import org.terraform.utils.version.Version;

import java.io.FileNotFoundException;
import java.util.Random;

public class PlainsVillageWellPopulator extends RoomPopulatorAbstract {

	private static final String[] villageWellSchems = new String[] {
			"plainsvillage-well1",
			"plainsvillage-well2"
	};
    public PlainsVillageWellPopulator(Random rand, boolean forceSpawn, boolean unique) {
        super(rand, forceSpawn, unique);
    }

    @Override
    public void populate(PopulatorDataAbstract data, CubeRoom room) {
    	
    	int x = room.getX();
        int z = room.getZ();
        int y = GenUtils.getHighestGround(data, x, z);
        BlockFace roomDir = ((DirectionalCubeRoom) room).getDirection();
        
        try {
            SimpleBlock core = new SimpleBlock(data, x,y+1,z);
			TerraSchematic schem = TerraSchematic.load(villageWellSchems[rand.nextInt(villageWellSchems.length)], core.getRelative(0,-1,0));
			schem.parser = new PlainsVillageWellSchematicParser();
			schem.apply();
			
			int depth = GenUtils.randInt(rand, 5, 20);
			
			for(int i = 0; i < depth; i++) {
				if(i == 0)
					core.getRelative(0,-i,0).setType(Material.AIR);
				else
					core.getRelative(0,-i,0).setType(Material.WATER);
				for(BlockFace face:BlockUtils.xzPlaneBlockFaces) {
					if(i == 0)
						core.getRelative(0,-i,0).setType(Material.AIR);
					else
						core.getRelative(0,-i,0).getRelative(face).setType(Material.WATER);
				}
			}
			
			for(int nx = -3; nx <= 3; nx++) {
				for(int nz = -3; nz <= 3; nz++) {
					Wall target = new Wall(core.getRelative(nx,-1,nz));
					if(target.getType() == Material.COBBLESTONE
							||target.getType() == Material.MOSSY_COBBLESTONE) {
						target.getRelative(0,-1,0).downUntilSolid(rand, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE);
					}
				}
			}
			
			Wall w = new Wall(core.getRelative(roomDir,3), roomDir);
			
			int pathLength = room.getWidthX()/2;
			if(BlockUtils.getAxisFromBlockFace(roomDir) == Axis.Z)
				pathLength = room.getWidthZ()/2;
			
			for(int i = 0; i < pathLength-1; i++) {
				w.getGround().setType(Material.GRASS_PATH);
				w.getLeft().getGround().setType(Material.GRASS_PATH);
				w.getRight().getGround().setType(Material.GRASS_PATH);
				
				if(GenUtils.chance(rand, 1, 10)) {
					BlockFace lampFace = BlockUtils.getTurnBlockFace(rand, roomDir);
					SimpleBlock target = w.getRelative(lampFace,2).getGround().getRelative(0,1,0).get();
					if(target.getRelative(0,-1,0).getType() != Material.GRASS_PATH 
							&& PlainsVillagePathPopulator.canPlaceLamp(target)) {
						PlainsVillagePathPopulator.placeLamp(rand, target);
					}
				}
				
				w = w.getRelative(roomDir);
			}
			
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

    }

    @Override
    public boolean canPopulate(CubeRoom room) {
        return room.getWidthX() <= 10;
    }
    
    private class PlainsVillageWellSchematicParser extends SchematicParser{
        @Override
        public void applyData(SimpleBlock block, BlockData data) {
            if (data.getMaterial().toString().contains("COBBLESTONE")) {
                data = Bukkit.createBlockData(
                        data.getAsString().replaceAll(
                                "cobblestone",
                                GenUtils.randMaterial(rand, Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE)
                                        .toString().toLowerCase()
                        )
                );
                super.applyData(block, data);
            } else if(data.getMaterial() == Material.IRON_BARS){
            	if(Version.isAtLeast(16)) {
            		data = Bukkit.createBlockData(Material.CHAIN);
            	}
                super.applyData(block, data);
            }else {
                super.applyData(block, data);
            }
        }
    }
}
