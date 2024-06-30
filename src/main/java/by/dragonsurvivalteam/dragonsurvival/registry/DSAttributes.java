package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DSAttributes {
    public static final DeferredRegister<Attribute> DS_ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MODID);
    public static final Holder<Attribute> FLIGHT_STAMINA = DS_ATTRIBUTES.register("flight_stamina", () -> new RangedAttribute("attribute.mod.flight_stamina", 1.0D, 0.0D, 5.0D).setSyncable(true));
}
