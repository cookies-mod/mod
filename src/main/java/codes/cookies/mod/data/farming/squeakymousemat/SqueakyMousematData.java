package codes.cookies.mod.data.farming.squeakymousemat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SqueakyMousematData(SqueakyMousematEntry wheat, SqueakyMousematEntry carrot, SqueakyMousematEntry potato,
								  SqueakyMousematEntry netherWart, SqueakyMousematEntry pumpkin,
								  SqueakyMousematEntry melon, SqueakyMousematEntry cocoaBeans,
								  SqueakyMousematEntry sugarCane, SqueakyMousematEntry cactus,
								  SqueakyMousematEntry mushroom) {

	public static final Codec<SqueakyMousematData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					SqueakyMousematEntry.CODEC.fieldOf("wheat").forGetter(SqueakyMousematData::wheat),
					SqueakyMousematEntry.CODEC.fieldOf("carrot").forGetter(SqueakyMousematData::carrot),
					SqueakyMousematEntry.CODEC.fieldOf("potato").forGetter(SqueakyMousematData::potato),
					SqueakyMousematEntry.CODEC.fieldOf("nether_wart").forGetter(SqueakyMousematData::netherWart),
					SqueakyMousematEntry.CODEC.fieldOf("pumpkin").forGetter(SqueakyMousematData::pumpkin),
					SqueakyMousematEntry.CODEC.fieldOf("melon").forGetter(SqueakyMousematData::melon),
					SqueakyMousematEntry.CODEC.fieldOf("cocoa_beans").forGetter(SqueakyMousematData::cocoaBeans),
					SqueakyMousematEntry.CODEC.fieldOf("sugar_cane").forGetter(SqueakyMousematData::sugarCane),
					SqueakyMousematEntry.CODEC.fieldOf("cactus").forGetter(SqueakyMousematData::cactus),
					SqueakyMousematEntry.CODEC.fieldOf("mushroom").forGetter(SqueakyMousematData::mushroom))
			.apply(instance, SqueakyMousematData::new));

	public static SqueakyMousematData getDefault() {
		return new SqueakyMousematData(
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(0, 45),
				new SqueakyMousematEntry(45, 0),
				new SqueakyMousematEntry(90, 0),
				new SqueakyMousematEntry(0, 0));
	}

	public void loadFrom(SqueakyMousematData other) {
		this.wheat.loadFrom(other.wheat);
		this.carrot.loadFrom(other.carrot);
		this.potato.loadFrom(other.potato);
		this.netherWart.loadFrom(other.netherWart);
		this.pumpkin.loadFrom(other.pumpkin);
		this.melon.loadFrom(other.melon);
		this.cocoaBeans.loadFrom(other.cocoaBeans);
		this.sugarCane.loadFrom(other.sugarCane);
		this.cactus.loadFrom(other.cactus);
		this.mushroom.loadFrom(other.mushroom);
	}
}
