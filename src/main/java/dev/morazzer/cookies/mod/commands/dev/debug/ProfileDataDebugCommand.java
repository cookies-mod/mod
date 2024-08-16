package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.sub.RancherSpeeds;
import java.util.Optional;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

public class ProfileDataDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("profileData")
            .then(literal("load").executes(run(this::load)))
            .then(literal("save").executes(run(this::save)))
            .then(literal("storage")
                .then(literal("clear").executes(run(this::clearStorage))))
            .then(literal("sacks")
                .then(literal("clear").executes(run(this::clearSacks))))
            .then(literal("chests")
                .then(literal("clear").executes(run(this::clearChests))))
            .then(literal("rancher_speeds")
                .then(literal("reset").executes(run(this::resetRancherSpeeds))));
    }

    private void resetRancherSpeeds() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        currentProfile.ifPresent(profileData -> profileData.setRancherSpeeds(new RancherSpeeds(profileData)));
        sendSuccessMessage("Reset rancher speeds for the current profile");
        save();
    }

    private void clearChests() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        currentProfile.ifPresent(profileData -> profileData.getGlobalProfileData().getIslandStorage().clear());
        sendSuccessMessage("Deleted all chest data for the current profile");
        save();
    }

    private void clearStorage() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        currentProfile.ifPresent(profileData -> profileData.getStorageData().clear());
        sendSuccessMessage("Deleted all storage data for the current profile");
        save();
    }

    private void clearSacks() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        currentProfile.ifPresent(profileData -> profileData.getSackTracker().clear());
        sendSuccessMessage("Deleted all sack data for the current profile");
        save();
    }

    private void save() {
        ProfileStorage.saveCurrentProfile();
        sendSuccessMessage("Saved the current profile");
    }

    private void load() {
        sendMessage("Loading the current profile");
        ProfileStorage.loadCurrentProfile(true);
    }
}
