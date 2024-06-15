package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@RegisterDragonAbility
public class BallLightningAbility extends ChargeCastAbility{
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "actives", "ball_lightning"}, key = "ballLightning", comment = "Whether the lightning ball ability should be enabled" )
	public static Boolean ballLightning = true;

	@ConfigRange( min = 0.05, max = 10000.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "actives", "ball_lightning"}, key = "ballLightningCooldown", comment = "The cooldown in seconds of the ball lightning ability" )
	public static Double ballLightningCooldown = 20.0;

	@ConfigRange( min = 0.05, max = 10000.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "actives", "ball_lightning"}, key = "ballLightningCasttime", comment = "The cast time in seconds of the ball lightning ability" )
	public static Double ballLightningCasttime = 2.0;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "actives", "ball_lightning"}, key = "ballLightningDamage", comment = "The amount of damage the lightning ball ability deals. This value is multiplied by the skill level." )
	public static Double ballLightningDamage = 4.0;

	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon", "actives", "ball_lightning"}, key = "ballLightningManaCost", comment = "The mana cost for using the lightning ball ability" )
	public static Integer ballLightningManaCost = 1;

	@Override
	public int getManaCost(){
		return ballLightningManaCost;
	}

	@Override
	public int getSortOrder(){
		return 2;
	}

	@Override
	public Integer[] getRequiredLevels(){
		return new Integer[]{0, 20, 45, 50};
	}

	@Override
	public int getSkillCooldown(){
		return Functions.secondsToTicks(ballLightningCooldown);
	}

	@Override
	public int getSkillCastingTime(){
		return Functions.secondsToTicks(ballLightningCasttime);
	}

	@Override
	public boolean requiresStationaryCasting(){
		return false;
	}

	@Override
	public void onCasting(Player player, int currentCastTime){

	}

	@Override
	public void castingComplete(Player player){
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

		float speed = 1;
		float yaw = (float) Math.toRadians(-player.getYRot());
		float pitch = (float) Math.toRadians(-player.getXRot());

		float xComp = (float)(Math.sin(yaw) * Math.cos(pitch));
		float yComp = (float)Math.sin(pitch);
		float zComp = (float)(Math.cos(yaw) * Math.cos(pitch));

		Vec3 eyePos = player.getEyePosition();
		Vec3 lookAngle = player.getLookAngle();

		double size = handler.getSize();

		Vec3 projPos;
		if (player.getAbilities().flying) {
			Vec3 forward = lookAngle.scale(2.0F);
			projPos = eyePos.add(forward).add(0F, -0.1-0.5F*(size / 30F), 0F);
		} else {
			Vec3 forward = lookAngle.scale(1.0F);
			projPos = eyePos.add(forward).add(0F, -0.1F-0.2F*(size / 30F), 0F);
		}
		Vec3 velocity = new Vec3(xComp * speed, yComp * speed, zComp * speed);

		BallLightningEntity entity = new BallLightningEntity(player.level(), player, velocity.x, velocity.y, velocity.z);
		entity.setPos(projPos.x + velocity.x, projPos.y + velocity.y, projPos.z + velocity.z);
		entity.setLevel(getLevel());
		entity.setDeltaMovement(velocity);
		player.level().addFreshEntity(entity);
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();
		components.add(Component.translatable("ds.skill.aoe", getRange() + "x" + getRange() + "x" + getRange()));
		components.add(Component.translatable("ds.skill.damage", getDamage()));

		if(!KeyInputHandler.ABILITY2.isUnbound()){
			String key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getString();
			}
			components.add(Component.translatable("ds.skill.keybind", key));
		}

		return components;
	}

	public int getRange(){
		return 4;
	}

	public float getDamage(){
		return getDamage(getLevel());
	}

	public static float getDamage(int level){
		return (float)(ballLightningDamage * level);
	}

	@Override

	public Component getDescription(){
		return Component.translatable("ds.skill.description." + getName(), getDamage());
	}

	@Override
	public String getName(){
		return "ball_lightning";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_0.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_1.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_2.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_3.png"),
		                              ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/ball_lightning_4.png"),};
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(Component.translatable("ds.skill.damage", "+" + ballLightningDamage));
		return list;
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}

	@Override
	public int getMinLevel(){
		return 0;
	}


	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ballLightning;
	}
}