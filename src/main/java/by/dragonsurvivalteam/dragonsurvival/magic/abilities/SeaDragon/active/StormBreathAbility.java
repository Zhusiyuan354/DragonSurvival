package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.SmallLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.sounds.StormBreathSound;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.ArrayList;
import java.util.List;

@RegisterDragonAbility
public class StormBreathAbility extends BreathAbility{
	public static StormBreathEntity EFFECT_ENTITY;
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreath", comment = "Whether the storm breath ability should be enabled" )
	public static Boolean stormBreath = true;
	@ConfigRange( min = 0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathDamage", comment = "The amount of damage the storm breath ability deals. This value is multiplied by the skill level." )
	public static Double stormBreathDamage = 1.0;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathInitialMana", comment = "The mana cost for starting the storm breath ability" )
	public static Integer stormBreathInitialMana = 2;
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathCooldown", comment = "The cooldown in ticks of the storm breath ability" )
	public static Integer stormBreathCooldown = Functions.secondsToTicks(10);
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathCasttime", comment = "The cast time in ticks of the storm breath ability" )
	public static Integer stormBreathCasttime = 20;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathOvertimeMana", comment = "The mana cost of sustaining the storm breath ability" )
	public static Integer stormBreathOvertimeMana = 1;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathManaTicks", comment = "How often in ticks, mana is consumed while using storm breath" )
	public static Integer stormBreathManaTicks = Functions.secondsToTicks(2);
	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathBlockBreaks", comment = "Blocks that have a chance to be broken by storm breath. Formatting: block/modid:id" )
	public static List<String> stormBreathBlockBreaks = List.of("minecraft:impermeable", "minecraft:snow", "minecraft:crops", "minecraft:flowers", "minecraft:banners", "minecraft:lush_plants_replaceable", "minecraft:azalea_log_replaceable", "minecraft:replaceable_plants", "minecraft:leaves");

	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "stormBreathChainCount", comment = "How many mobs stormbreath is able to chain to at once" )
	public static Integer stormBreathChainCount = 2;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedEffectChainCount", comment = "How many mobs the charged effect is able to chain to at once" )
	public static Integer chargedEffectChainCount = 2;
	@ConfigRange( min = -1, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedEffectMaxChain", comment = "How many times the charged effect is able to chain. -1 means it can chain infinitely" )
	public static Integer chargedEffectMaxChain = 10;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedChainRange", comment = "The max distance in blocks the storm breath and charged effect is able to chain to mobs" )
	public static Integer chargedChainRange = 10;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedEffectDamage", comment = "The amount of damage the charged effect deals each second" )
	public static Integer chargedEffectDamage = 1;
	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedSpreadBlacklist", comment = "List of entities that will not spread the charged effect. Format: modid:id" )
	public static List<String> chargedSpreadBlacklist =  List.of("minecraft:armor_stand");

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic",
	                                                     "abilities",
	                                                     "sea_dragon",
	                                                     "actives",
	                                                     "storm_breath"}, key = "chargedBlacklist", comment = "List of entities that will not receive the charged effect at all Format: modid:id" )
	public static List<String> chargedBlacklist =  List.of("minecraft:armor_stand", "minecraft:cat", "minecraft:cart", "minecraft:guardian", "minecraft:elder_guardian", "minecraft:enderman");

	@OnlyIn( Dist.CLIENT )
	private SoundInstance startingSound;

	@OnlyIn( Dist.CLIENT )
	private TickableSoundInstance loopingSound;

	@OnlyIn( Dist.CLIENT )
	private SoundInstance endSound;

	public static void onDamageChecks(LivingEntity entity){
		if(entity instanceof Creeper creeper){

			if(!creeper.isPowered())
				creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
		}
	}

	public static void spark(LivingEntity source, LivingEntity target){
		if(source.level.isClientSide){
			float eyeHeight = source instanceof Player ? 0f : source.getEyeHeight();
			Vec3 start = source.getPosition(eyeHeight);
			Vec3 end = target.getPosition(target.getEyeHeight());

			int parts = 20;

			double xDif = (end.x - start.x) / parts;
			double yDif = (end.y - start.y) / parts;
			double zDif = (end.z - start.z) / parts;

			if(end.x - start.x >= 64 || end.y - start.y >= 64 || end.z - start.z >= 64)
				return;

			for(int i = 0; i < parts; i++){
				double x = start.x + xDif * i;
				double y = start.y + yDif * i + eyeHeight;
				double z = start.z + zDif * i;
				source.level.addParticle(new DustParticleOptions(new Vector3f(0f, 1F, 1F), 1f), x, y, z, 0, 0, 0);
			}
		}
	}

	@Override
	public int getManaCost(){
		return stormBreathOvertimeMana;
	}

	@Override
	public Integer[] getRequiredLevels(){
		return new Integer[]{0, 10, 30, 50};
	}

	@Override
	public int getSkillCooldown(){
		return stormBreathCooldown;
	}

	public static void chargedEffectSparkle(Player player, LivingEntity source, int chainRange, int maxChainTargets, int damage){
		List<LivingEntity> secondaryTargets = getEntityLivingBaseNearby(source, chainRange);
		secondaryTargets.removeIf(e -> !isValidTarget(source, e));

		if(secondaryTargets.size() > maxChainTargets){
			secondaryTargets.sort((c1, c2) -> Boolean.compare(c1.hasEffect(DragonEffects.CHARGED), c2.hasEffect(DragonEffects.CHARGED)));
			secondaryTargets = secondaryTargets.subList(0, maxChainTargets);
		}

		secondaryTargets.add(source);

		for(LivingEntity target : secondaryTargets){
			if(player != null){
				target.hurt(DamageSource.indirectMobAttack(source, player), damage);
			}else{
				target.hurt(DamageSource.mobAttack(source), damage);
			}

			onDamageChecks(target);

			if(!chargedSpreadBlacklist.contains(source.getType()))
				if(target != source){
					GenericCapability capSource = GenericCapabilityProvider.getGenericCapability(source).orElse(null);
					GenericCapability cap = GenericCapabilityProvider.getGenericCapability(target).orElse(null);

					cap.chainCount = capSource.chainCount + 1;

					if(!target.level.isClientSide){
						if(target.level.random.nextInt(100) < 40){
							if(cap.chainCount < chargedEffectMaxChain || chargedEffectMaxChain == -1){
								cap.lastAfflicted = player != null ? player.getId() : -1;
								target.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
							}
						}
					}

					if(player != null){
						if(player.level.random.nextInt(100) < 50){
							if(!player.level.isClientSide){
								player.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(30)));
							}
						}
					}
					spark(source, target);
				}
		}
	}

	public static float getDamage(int level){
		return (float)(stormBreathDamage * level);
	}

	public static boolean isValidTarget(LivingEntity attacker, LivingEntity target){
		if(target == null || attacker == null)
			return false;
		if(chargedBlacklist.contains(target.getType()))
			return false;
		if(target == attacker)
			return false;
		if(target instanceof FakePlayer)
			return false;
		if(target instanceof DragonHitBox)
			return false;
		if(target instanceof TamableAnimal && ((TamableAnimal)target).getOwner() == attacker)
			return false;
		if(attacker instanceof TamableAnimal && !isValidTarget(((TamableAnimal)attacker).getOwner(), target))
			return false;
		if(target.getLastHurtByMob() == attacker && target.getLastHurtByMobTimestamp() + Functions.secondsToTicks(1) < target.tickCount)
			return false;
		return DragonStateProvider.getCap(target).map(DragonStateHandler::getType).orElse(null) != DragonType.SEA;
	}

//	@Override
//	public Entity getEffectEntity(){
//		return EFFECT_ENTITY;
//	}

	public void hurtTarget(LivingEntity entity){
		entity.hurt(new BreathDamage(player), getDamage());
		onDamage(entity);

		if(player.level.random.nextInt(100) < 50)
			if(!player.level.isClientSide){
				player.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(30)));
			}

		if(!entity.level.isClientSide)
			if(entity.level.random.nextInt(100) < 40){
				GenericCapability cap = GenericCapabilityProvider.getGenericCapability(entity).orElse(null);

				cap.lastAfflicted = player.getId();
				cap.chainCount = 1;

				entity.addEffect(new MobEffectInstance(DragonEffects.CHARGED, Functions.secondsToTicks(10), 0, false, true));
			}
	}

	@Override
	public String getName(){
		return "storm_breath";
	}

	@Override
	public DragonType getDragonType(){
		return DragonType.SEA;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/storm_breath_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/storm_breath_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/storm_breath_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/storm_breath_3.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/sea/storm_breath_4.png")};
	}


	@Override
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + stormBreathDamage));
		return list;
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}

	@Override
	public int getMinLevel(){
		return 1;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !stormBreath;
	}



	@OnlyIn( Dist.CLIENT )
	public   void sound(){
		if(startingSound == null)
			startingSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.stormBreathStart);

		Minecraft.getInstance().getSoundManager().playDelayed(startingSound, 0);
		loopingSound = new StormBreathSound(this);

		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "storm_breath_loop"), SoundSource.PLAYERS);
		Minecraft.getInstance().getSoundManager().queueTickingSound(loopingSound);
	}

	@Override
	public void onBlock(BlockPos pos, BlockState blockState, Direction direction){
		if(!player.level.isClientSide)
			if(player.tickCount % 40 == 0){
				if(player.level.isThundering()){
					if(player.level.random.nextInt(100) < 30){
						if(player.level.canSeeSky(pos)){
							LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
							lightningboltentity.moveTo(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
							lightningboltentity.setCause((ServerPlayer)player);
							player.level.addFreshEntity(lightningboltentity);
							player.level.playSound(player, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 5F, 1.0F);
						}
					}
				}
			}
	}


	@OnlyIn( Dist.CLIENT )
	public   void stopSound(){
		if(SoundRegistry.stormBreathEnd != null){
			if(endSound == null)
				endSound = SimpleSoundInstance.forAmbientAddition(SoundRegistry.stormBreathEnd);

			Minecraft.getInstance().getSoundManager().playDelayed(endSound, 0);
		}

		Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(DragonSurvivalMod.MODID, "storm_breath_loop"), SoundSource.PLAYERS);
	}


	@Override
	public void onChanneling(Player player, int castDuration){
		super.onChanneling(player, castDuration);

		if(EFFECT_ENTITY == null)
			EFFECT_ENTITY = DSEntities.STORM_BREATH_EFFECT.create(player.level);


		if(player.level.isClientSide && castDuration <= 1){
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> sound());
		}

		if(player.level.isClientSide){
			for(int i = 0; i < 6; i++){
				double xSpeed = speed * 1f * xComp;
				double ySpeed = speed * 1f * yComp;
				double zSpeed = speed * 1f * zComp;
				player.level.addParticle(new SmallLightningParticleData(37, true), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}

			for(int i = 0; i < 2; i++){
				double xSpeed = speed * xComp + spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * Math.sqrt(1 - xComp * xComp);
				double ySpeed = speed * yComp + spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * Math.sqrt(1 - yComp * yComp);
				double zSpeed = speed * zComp + spread * 0.7 * (player.level.random.nextFloat() * 2 - 1) * Math.sqrt(1 - zComp * zComp);
				player.level.addParticle(new LargeLightningParticleData(37, false), dx, dy, dz, xSpeed, ySpeed, zSpeed);
			}
		}

		hitEntities();

		if(player.tickCount % 10 == 0)
			hitBlocks();
	}

	@Override
	public boolean canHitEntity(LivingEntity entity){
		if(entity instanceof DragonHitBox)
			return false;
		return !(entity instanceof Player) || player.canHarmPlayer((Player)entity);
	}

	@Override
	public void onDamage(LivingEntity entity){
		onDamageChecks(entity);
	}

	@Override
	public void onEntityHit(LivingEntity entityHit){
		hurtTarget(entityHit);
		chargedEffectSparkle(player, entityHit, chargedChainRange, stormBreathChainCount, chargedEffectDamage);
	}


	@Override
	public float getDamage(){
		return getDamage(getLevel());
	}

	@Override
	public int getSkillChargeTime(){
		return stormBreathCasttime;
	}

	@Override
	public int getChargingManaCost(){
		return stormBreathInitialMana;
	}

	@Override
	public void castComplete(Player player){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)this::stopSound);
	}

	@Override
	public boolean requiresStationaryCasting(){
		return false;
	}
}