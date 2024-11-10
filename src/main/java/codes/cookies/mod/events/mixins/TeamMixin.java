package codes.cookies.mod.events.mixins;

import codes.cookies.mod.events.ScoreboardUpdateEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Team.class)
public abstract class TeamMixin {

    @Shadow public abstract String getName();

    @Shadow public abstract Text getPrefix();

    @Shadow public abstract Text getSuffix();

    @Inject(method = "setPrefix", at = @At("RETURN"))
    public void setPrefix(Text prefix, CallbackInfo ci) {
        if (!this.getName().startsWith("team_")) {
            return;
        }
        ScoreboardUpdateEvent.EVENT.invoker().update(Integer.parseInt(this.getName().substring(5)), this.cookies$getText());
    }

    @Inject(method = "setSuffix", at = @At("RETURN"))
    public void setSuffix(Text suffix, CallbackInfo ci) {
        if (!this.getName().startsWith("team_")) {
            return;
        }
        ScoreboardUpdateEvent.EVENT.invoker().update(Integer.parseInt(this.getName().substring(5)), this.cookies$getText());
    }

    @Unique
    private String cookies$getText() {
        return (this.getPrefix().getString()) + (this.getSuffix().getString());
    }

}
