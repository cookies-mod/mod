package dev.morazzer.cookies.mod.data.server;

import dev.morazzer.cookies.mod.commands.system.CommandManager;
import dev.morazzer.cookies.mod.commands.WarpCommand;
import lombok.Getter;

import java.util.List;

@Getter
public class Warps {
    private static final List<String> warps = List.of("elizabeth", "museum", "dhub", "end");

    public static void load() {
        CommandManager.addCommands(warps.stream().map(WarpCommand::new).toArray(WarpCommand[]::new));
    }
}
