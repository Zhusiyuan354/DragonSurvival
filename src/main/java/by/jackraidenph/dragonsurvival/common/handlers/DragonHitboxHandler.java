package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class DragonHitboxHandler
{
	public static ConcurrentHashMap<Integer, Integer> dragonHitboxes = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent playerTickEvent){
		Player player = playerTickEvent.player;
		
		if(!player.level.isClientSide) {
			if (DragonUtils.isDragon(player)) {
				if (dragonHitboxes.containsKey(player.getId())) {
					int id = dragonHitboxes.get(player.getId());
					Entity ent = player.level.getEntity(id);
					
					if (ent == null || !(ent instanceof DragonHitBox) || !ent.isAlive() || ent.isRemoved()) {
						dragonHitboxes.remove(player.getId());
						addPlayerHitbox(player);
					}
				}else{
					dragonHitboxes.remove(player.getId());
					addPlayerHitbox(player);
				}
			}
		}
	}
	
	private static void addPlayerHitbox(Player player){
		if(player.level != null) {
			DragonHitBox hitbox = DSEntities.DRAGON_HITBOX.create(player.level);
			hitbox.copyPosition(player);
			hitbox.player = player;
			hitbox.setPlayerId(player.getId());
			player.level.addFreshEntity(hitbox);
			dragonHitboxes.put(player.getId(), hitbox.getId());
		}
	}
}
