package cm.tasks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.IOException;
import javax.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class CreateBuildInfo extends DefaultTask {
    @Inject
    public CreateBuildInfo() {
        this.setGroup("generation");
        this.getOutputs().upToDateWhen(upToDateSpec -> false);
        this.getProject().getTasks().getByName("build").dependsOn(this);
    }

    @TaskAction
    public void createBuildInfo() {
        CompilationUnit compilationUnit = new CompilationUnit("codes.cookies.mod.generated")
            .setStorage(
					this.getOutputDir().get().getAsFile().toPath()
                    .resolve("codes/cookies/mod/generated/BuildInfo.java")
            );
        compilationUnit.addImport("net.fabricmc.loader.api.SemanticVersion");
        compilationUnit.addImport("codes.cookies.mod.utils.exceptions.ExceptionHandler");

        final ClassOrInterfaceDeclaration buildInfoClass = compilationUnit.addClass("BuildInfo");

        final BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement(new ReturnStmt(new MethodCallExpr("SemanticVersion.parse",
            new StringLiteralExpr(this.getProject().getVersion().toString()))));

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

        try (Git git = GitUtils.findGit(this.getProject())) {
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
                new BooleanLiteralExpr(!this.getProject().getVersion().toString().contains("-")),
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
