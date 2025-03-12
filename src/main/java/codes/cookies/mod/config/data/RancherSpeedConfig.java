package codes.cookies.mod.config.data;

import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.data.farming.RancherSpeeds;
import codes.cookies.mod.utils.IntReference;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.util.dynamic.Codecs;

import org.slf4j.Logger;

/**
 * Profile specific rancher boot speeds.
 */
@SuppressWarnings("MissingJavadoc")
@Getter
@Setter
@ConfigObject
@AllArgsConstructor
public class RancherSpeedConfig implements CodecJsonSerializable<RancherSpeedConfig> {
	public static RancherSpeeds DEFAULT = new RancherSpeedConfig().asData();

	public static final Codec<RancherSpeedConfig> CODEC =  RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("uuids").forGetter(RancherSpeedConfig::getUseProfileSettings),
					Codecs.rangedInt(0, 1000).fieldOf("wheat").forGetter(RancherSpeedConfig::getWheat),
					Codecs.rangedInt(0, 1000).fieldOf("carrot").forGetter(RancherSpeedConfig::getCarrot),
					Codecs.rangedInt(0, 1000).fieldOf("potato").forGetter(RancherSpeedConfig::getPotato),
					Codecs.rangedInt(0, 1000).fieldOf("nether_wart").forGetter(RancherSpeedConfig::getNetherWart),
					Codecs.rangedInt(0, 1000).fieldOf("pumpkin").forGetter(RancherSpeedConfig::getPumpkin),
					Codecs.rangedInt(0, 1000).fieldOf("melon").forGetter(RancherSpeedConfig::getMelon),
					Codecs.rangedInt(0, 1000).fieldOf("cocoa_beans").forGetter(RancherSpeedConfig::getCocoaBeans),
					Codecs.rangedInt(0, 1000).fieldOf("sugar_cane").forGetter(RancherSpeedConfig::getSugarCane),
					Codecs.rangedInt(0, 1000).fieldOf("cactus").forGetter(RancherSpeedConfig::getCactus),
					Codecs.rangedInt(0, 1000).fieldOf("mushroom").forGetter(RancherSpeedConfig::getMushroom))
			.apply(instance, RancherSpeedConfig::of));

	private static RancherSpeedConfig of(
			List<String> uuids,
			Integer wheat,
			Integer carrot,
			Integer potato,
			Integer netherWart,
			Integer pumpkin,
			Integer melon,
			Integer cocoaBeans,
			Integer sugarCane,
			Integer cactus,
			Integer mushroom
	) {
		return new RancherSpeedConfig(uuids, wheat, carrot, potato, netherWart, pumpkin, melon, cocoaBeans, sugarCane, cactus, mushroom);
	}

	public RancherSpeedConfig() {}

	public List<String> useProfileSettings = new ArrayList<>();

	public int wheat = 93;
	public int carrot = 93;
	public int potato = 93;
	public int netherWart = 93;
	public int pumpkin = 258;
	public int melon = 258;
	public int cocoaBeans = 155;
	public int sugarCane = 327;
	public int cactus = 400;
	public int mushroom = 200;

	public RancherSpeeds asData() {
		return new RancherSpeeds(
				new IntReference(this::getWheat, this::setWheat),
				new IntReference(this::getCarrot, this::setCarrot),
				new IntReference(this::getPotato, this::setPotato),
				new IntReference(this::getNetherWart, this::setNetherWart),
				new IntReference(this::getPumpkin, this::setPumpkin),
				new IntReference(this::getMelon, this::setMelon),
				new IntReference(this::getCocoaBeans, this::setCocoaBeans),
				new IntReference(this::getSugarCane, this::setSugarCane),
				new IntReference(this::getCactus, this::setCactus),
				new IntReference(this::getMushroom, this::setMushroom)
		);
	}


	@Override
	public Codec<RancherSpeedConfig> getCodec() {
		return CODEC;
	}

	@Override
	public void load(RancherSpeedConfig value) {

	}

	@Override
	public RancherSpeedConfig getValue() {
		return this;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
