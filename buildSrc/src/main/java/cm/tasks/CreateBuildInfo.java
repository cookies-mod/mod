package cm.tasks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.units.qual.A;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gradle.api.tasks.options.Option;
import java.io.IOException;
import javax.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class CreateBuildInfo extends DefaultTask {
    private boolean isStable;

    @Inject
    public CreateBuildInfo() {
        this.setGroup("generation");
        this.getOutputs().upToDateWhen(upToDateSpec -> false);
        this.getProject().getTasks().getByName("build").dependsOn(this);
    }

    @Option(option = "isStable", description = "Indicates that this is a release instead of a beta release")
    public void setIsStable(boolean flag) {
        this.isStable = flag;
    }

    @Input
    public boolean isStable() {
        return isStable;
    }

    @TaskAction
    public void createBuildInfo() {
        CompilationUnit compilationUnit = new CompilationUnit("dev.morazzer.mods.cookies.generated")
            .setStorage(
                getOutputDir().get().getAsFile().toPath()
                    .resolve("dev/morazzer/mods/cookies/generated/BuildInfo.java")
            );
        compilationUnit.addImport("net.fabricmc.loader.api.SemanticVersion");
        compilationUnit.addImport("dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler");

        final ClassOrInterfaceDeclaration buildInfoClass = compilationUnit.addClass("BuildInfo");

        final BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement(new ReturnStmt(new MethodCallExpr("SemanticVersion.parse",
            new StringLiteralExpr(getProject().getVersion().toString()))));

        buildInfoClass.addFieldWithInitializer(
            "SemanticVersion",
            "version",
            new MethodCallExpr("ExceptionHandler.removeThrows", new LambdaExpr(NodeList.nodeList(), blockStmt)),
            Modifier.Keyword.FINAL,
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.STATIC
        );

        buildInfoClass.addFieldWithInitializer(
            new PrimitiveType(PrimitiveType.Primitive.LONG),
            "buildTime",
            new LongLiteralExpr("%sL".formatted(System.currentTimeMillis())),
            Modifier.Keyword.FINAL,
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.STATIC
        );

        try (Git git = GitUtils.findGit(getProject())) {
            buildInfoClass.addFieldWithInitializer(
                "String",
                "branch",
                new StringLiteralExpr(git.getRepository().getBranch()),
                Modifier.Keyword.FINAL,
                Modifier.Keyword.PUBLIC,
                Modifier.Keyword.STATIC
            );

            buildInfoClass.addFieldWithInitializer(
                new PrimitiveType(PrimitiveType.Primitive.BOOLEAN),
                "isStable",
                new BooleanLiteralExpr(isStable),
                Modifier.Keyword.FINAL,
                Modifier.Keyword.PUBLIC,
                Modifier.Keyword.STATIC
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        compilationUnit.getStorage().ifPresent(storage -> storage.save(unit -> new DefaultPrettyPrinter().print(unit)));
    }

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

}
