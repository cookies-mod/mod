package codes.cookies.mod.config.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import codes.cookies.mod.data.farming.squeakymousemat.SqueakyMousematData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Uuids;

public record SqueakyMousematOption(List<UUID> useProfileData, SqueakyMousematData data) {

	public static final Codec<SqueakyMousematOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Uuids.CODEC.listOf().fieldOf("use_profile").forGetter(SqueakyMousematOption::useProfileData),
			SqueakyMousematData.CODEC.fieldOf("data").forGetter(SqueakyMousematOption::data)
	).apply(instance, SqueakyMousematOption::create));

	public static SqueakyMousematOption create(List<UUID> uuids, SqueakyMousematData data) {
		return new SqueakyMousematOption(new ArrayList<>(uuids), data);
	}

	public static SqueakyMousematOption createDefault() {
		return new SqueakyMousematOption(new ArrayList<>(), SqueakyMousematData.getDefault());
	}
}
