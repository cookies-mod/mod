package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.render.WorldRender;
import codes.cookies.mod.render.types.BeaconBeam;
import codes.cookies.mod.render.types.BlockHighlight;
import codes.cookies.mod.render.types.Box;
import codes.cookies.mod.render.types.ComplexBlock;
import codes.cookies.mod.render.types.Line;
import codes.cookies.mod.render.types.Outlines;
import codes.cookies.mod.render.types.WorldText;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;


/**
 * Debug command to add all types of renderables.
 * usage: /dev debug addDebugRenders
 */
public class RenderDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("addDebugRenders")
				.requires(super::ensureDevEnvironment)
				.executes(super.run(this::addDebugRender));
	}

	/**
	 * Executor to add all renderables.
	 */
	private void addDebugRender() {
		super.sendInformation("Attempting to add debug renders");
		WorldRender.addRenderable(new BeaconBeam(new Vec3d(0, -60, 0), -59, 0xFFababab));
		WorldRender.addRenderable(new Box(new Vec3d(1, -60, 0), new Vec3d(2, -59, 1), 0xFF00FF00, true));
		WorldRender.addRenderable(new Outlines(new Vec3d(2, -60, 0), new Vec3d(3, -59, 1), 0xFFFF0000, 3, true));
		WorldRender.addRenderable(new ComplexBlock(Blocks.LEVER.getDefaultState(), new Vec3d(5, -60, 0), 0xFFFFFF00, true));
		WorldRender.addRenderable(new Line(new Vec3d(3, -60, 0), new Vec3d(4, -59, 1), 0xFFFF00FF));
		WorldRender.addRenderable(new WorldText(new Vec3d(4.5, -59.5, 0.5), Text.literal("test"), 0xFF00FFFF, true));
		WorldRender.addRenderable(new BlockHighlight(new BlockPos(6, -60, 0), 0xFFFFFF00));
		super.sendSuccessMessage("Successfully added all debug renderers!");
	}
}
