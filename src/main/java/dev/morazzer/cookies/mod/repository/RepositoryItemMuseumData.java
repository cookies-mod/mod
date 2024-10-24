package dev.morazzer.cookies.mod.repository;

import java.util.ArrayList;
import java.util.List;

import dev.morazzer.cookies.mod.repository.constants.MuseumData;

import java.util.Optional;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class RepositoryItemMuseumData {

	private List<MuseumData.ArmorItem> armorItems;
	private MuseumData.MuseumItem weapon = null;
	private MuseumData.MuseumItem rarity = null;
	private boolean canBeDonatedToSpecial = false;
	@Getter
	@NotNull
	private DonationType donationType = DonationType.NONE;

	public void addArmorItems(MuseumData.ArmorItem armorItem) {
		if (armorItems == null) {
			armorItems = new ArrayList<>();
		}
		armorItems.add(armorItem);
		donationType = DonationType.ARMOR;
	}

	public void addWeapon(MuseumData.MuseumItem weapon) {
		this.weapon = weapon;
		donationType = DonationType.WEAPON;
	}

	public void addRarity(MuseumData.MuseumItem rarity) {
		donationType = DonationType.RARITY;
		this.rarity = rarity;
	}
	public void setSpecial() {
		donationType = DonationType.SPECIAL;
		this.canBeDonatedToSpecial = true;
	}

	public Optional<List<MuseumData.ArmorItem>> getArmorItems() {
		return Optional.ofNullable(armorItems);
	}

	public Optional<MuseumData.MuseumItem> getWeapon() {
		return Optional.ofNullable(weapon);
	}

	public Optional<MuseumData.MuseumItem> getRarity() {
		return Optional.ofNullable(rarity);
	}

	public boolean canBeDonatedToSpecial() {
		return canBeDonatedToSpecial;
	}

	public enum DonationType {
		ARMOR, WEAPON, RARITY, SPECIAL, NONE
	}
}
