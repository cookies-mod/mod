package cm.tasks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class CreateRegions extends DefaultTask {

    @Inject
    public CreateRegions() {
        this.setGroup("generation");
        this.getOutputs().upToDateWhen(upToDateSpec -> false);
        this.getProject().getTasks().getByName("build").dependsOn(this);
    }

    @TaskAction
    public void createBuildInfo() {
        CompilationUnit compilationUnit = new CompilationUnit("dev.morazzer.mods.cookies.generated")
            .setStorage(
                this.getOutputDir().get().getAsFile().toPath()
                              .resolve("dev/morazzer/mods/cookies/generated/Regions.java")
                       );
        compilationUnit.addImport("dev.morazzer.cookies.mod.utils.skyblock.LocationUtils");
        final EnumDeclaration regions = compilationUnit.addEnum("Regions");

        JsonArray jsonArray;
        try (final InputStream resourceAsStream = CreateRegions.class.getClassLoader().getResourceAsStream("regions.json")) {
			if (resourceAsStream == null) {
				return;
			}
            jsonArray = JsonParser.parseString(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<String> set = new HashSet<>();
        jsonArray.forEach(jsonElement -> {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final String name = this.getName(jsonObject);
            if (set.contains(name)) {
                System.err.println("Duplicate region name: " + name);
            }
            set.add(name);

            final EnumConstantDeclaration declaration = regions.addEnumConstant(name.toUpperCase(Locale.GERMAN)
                                                                                    .replace(' ', '_')
                                                                                    .replace("'", ""));

            declaration.addArgument("LocationUtils.Island." + this.getIsland(jsonObject));
            declaration.addArgument(new StringLiteralExpr(this.getScoreboard(jsonObject)));
            declaration.addArgument(new BooleanLiteralExpr(
                jsonObject.has("regex") && jsonObject.get("regex").getAsJsonPrimitive().isBoolean()));
            if (this.hasIcon(jsonObject)) {
                declaration.addArgument(new StringLiteralExpr(this.getIcon(jsonObject)));
            }

        });

        regions.addField("LocationUtils.Island", "island", Modifier.Keyword.FINAL, Modifier.Keyword.PUBLIC);
        regions.addField("String", "icon", Modifier.Keyword.FINAL, Modifier.Keyword.PUBLIC);
        regions.addField("String", "scoreboard", Modifier.Keyword.FINAL, Modifier.Keyword.PUBLIC);
        regions.addField("boolean", "regex", Modifier.Keyword.FINAL, Modifier.Keyword.PUBLIC);

        this.addDefaultConstructor(regions);
        this.addAlternativeConstructor(regions);

        compilationUnit.getStorage().ifPresent(storage -> storage.save(unit -> new DefaultPrettyPrinter().print(unit)));
    }

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    private String getName(JsonObject jsonObject) {
        if (jsonObject.has("iname")) {
            return jsonObject.get("iname").getAsString();
        }
        return jsonObject.get("name").getAsString();
    }

    private String getIsland(JsonObject jsonObject) {
        if (jsonObject.has("island")) {
            return jsonObject.get("island").getAsString();
        }
        throw new RuntimeException("No island set for %s".formatted(this.getName(jsonObject)));
    }

    private String getScoreboard(JsonObject jsonObject) {
        return jsonObject.get("name").getAsString();
    }

    private boolean hasIcon(JsonObject jsonObject) {
        return jsonObject.has("icon");
    }

    private String getIcon(JsonObject jsonObject) {
        return jsonObject.get("icon").getAsString();
    }

    private void addDefaultConstructor(EnumDeclaration regions) {
        final ConstructorDeclaration constructor = regions.addConstructor();
        constructor.addParameter("LocationUtils.Island", "island");
        constructor.addParameter("String", "scoreboard");
        constructor.addParameter("boolean", "regex");
        constructor.addParameter("String", "icon");
        final BlockStmt body = constructor.createBody();
        body.addStatement("this.island = island;");
        body.addStatement("this.scoreboard = scoreboard;");
        body.addStatement("this.regex = regex;");
        body.addStatement("this.icon = icon;");
    }

    private void addAlternativeConstructor(EnumDeclaration regions) {
        final ConstructorDeclaration constructor = regions.addConstructor();
        constructor.addParameter("LocationUtils.Island", "island");
        constructor.addParameter("String", "scoreboard");
        constructor.addParameter("boolean", "regex");
        final BlockStmt body = constructor.createBody();
        body.addStatement("this(island, scoreboard, regex, null);");
    }
}
