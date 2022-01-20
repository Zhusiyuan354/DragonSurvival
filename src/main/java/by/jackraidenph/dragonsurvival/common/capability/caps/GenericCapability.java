package by.jackraidenph.dragonsurvival.common.capability.caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GenericCapability
{
    public Vec3 lastPos;
    public int lastAfflicted = -1;
    public int chainCount = 0;
    
    protected ListTag newDoubleList(double... p_70087_1_) {
        ListTag listnbt = new ListTag();
        
        for(double d0 : p_70087_1_) {
            listnbt.add(DoubleTag.valueOf(d0));
        }
        
        return listnbt;
    }
    
    @Nullable
    public CompoundTag writeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        if(lastPos != null) {
            compoundNBT.put("lastPos", newDoubleList(lastPos.x, lastPos.y, lastPos.z));
        }
        
        compoundNBT.putInt("lastAfflicted", lastAfflicted);
        return compoundNBT;
    }
    
    public void readNBT(CompoundTag nbt) {
        CompoundTag compoundNBT = nbt;
        
        if(compoundNBT.contains("lastPos")){
            ListTag listnbt = compoundNBT.getList("lastPos", 6);
            lastPos = new Vec3(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
        }
        
        lastAfflicted = compoundNBT.getInt("lastAfflicted");
    }
}
