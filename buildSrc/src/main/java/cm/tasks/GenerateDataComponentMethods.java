package cm.tasks;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Task to generate the {@link dev.morazzer.cookies.mod.generated.utils.ItemAccessor} which contains a method to access all different types of custom data.
 */

public abstract class GenerateDataComponentMethods extends DefaultTask {

    @Inject
    @SuppressWarnings("MissingJavadoc")
    public GenerateDataComponentMethods() {
        this.setGroup("generation");
        this.getOutputs().upToDateWhen(upToDateSpec -> false);
        this.getProject().getTasks().getByName("build").dependsOn(this);
    }

    @TaskAction
    private void generateDataComponent() throws IOException {
        final CompilationUnit compilationUnit =
            new CompilationUnit("dev.morazzer.cookies.mod.generated.utils").setStorage(
                this.getOutputDir().get().getAsFile().toPath()
                    .resolve("dev/morazzer/cookies/mod/generated/utils/ItemAccessor.java")
            );

        final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.addInterface("ItemAccessor");
        classOrInterfaceDeclaration.addAnnotation(new SingleMemberAnnotationExpr(new Name("SuppressWarnings"), new StringLiteralExpr("MissingJavadoc")));
        final ClassOrInterfaceDeclaration source = readSource();
        source.findAll(ImportDeclaration.class).forEach(importDeclaration -> {
            compilationUnit.addImport(importDeclaration.getNameAsString());
        });
        compilationUnit.addImport("dev.morazzer.cookies.mod.utils.*");
        compilationUnit.addImport("dev.morazzer.cookies.mod.utils.items.*");
        compilationUnit.addImport("net.minecraft.item.ItemStack");
        compilationUnit.addImport("java.util.*");
        compilationUnit.addImport("dev.morazzer.cookies.mod.repository.RepositoryItem");
        compilationUnit.addImport("java.time.Instant");

        source.getFields().forEach(fieldDeclaration -> {
            if (fieldDeclaration.findFirst(MarkerAnnotationExpr.class)
                    .map(NodeWithName::getNameAsString)
                    .orElse("").equalsIgnoreCase("GenerateAccessor")
                && fieldDeclaration.findFirst(VariableDeclarator.class).isPresent()) {
                createAccessor(fieldDeclaration, classOrInterfaceDeclaration);
            }
        });


        final MethodDeclaration methodDeclaration = classOrInterfaceDeclaration.addMethod("getItemStack");
        methodDeclaration.setType("ItemStack");
        methodDeclaration.removeBody();

        compilationUnit.getStorage().ifPresent(storage -> storage.save(unit -> new DefaultPrettyPrinter().print(unit)));
    }

    @OutputDirectory
    @SuppressWarnings("MissingJavadoc")
    public abstract DirectoryProperty getOutputDir();

    private ClassOrInterfaceDeclaration readSource() throws IOException {
        final ParseResult<CompilationUnit> parse = new JavaParser().parse(
            this.getProject().getLayout().getProjectDirectory().getAsFile().toPath()
                .resolve("src/main/java/dev/morazzer/cookies/mod/utils/items/SkyblockDataComponentTypes.java")
        );

        final CompilationUnit compilationUnit = parse.getResult().orElseThrow();
        final Optional<ClassOrInterfaceDeclaration> skyblockDataComponentTypes =
            compilationUnit.getClassByName("SkyblockDataComponentTypes");
        return skyblockDataComponentTypes.orElseThrow();
    }

    private void createAccessor(FieldDeclaration fieldDeclaration, ClassOrInterfaceDeclaration declaration) {
        final VariableDeclarator variableDeclarator =
            fieldDeclaration.findFirst(VariableDeclarator.class).orElseThrow();
        final Type type = variableDeclarator.getType();
        final ClassOrInterfaceType classOrInterfaceType =
            type.findFirst(ClassOrInterfaceType.class, c -> c != type)
                .orElseThrow();

        final String collect =
            lowerCaseFirst(Arrays.stream(variableDeclarator.getNameAsString().toLowerCase().split("_"))
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining("")));

        this.addDefaultAccessor(collect, classOrInterfaceType, variableDeclarator, declaration);
        this.addValueAccessor(collect, classOrInterfaceType, variableDeclarator, declaration);
        this.addInstanceMethod(collect, classOrInterfaceType, declaration);

    }

    private String lowerCaseFirst(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private void addDefaultAccessor(String name, ClassOrInterfaceType type, VariableDeclarator variableDeclarator,
                                    ClassOrInterfaceDeclaration declaration) {
        final MethodDeclaration methodDeclaration =
            declaration.addMethod(name + "OrNull", Modifier.Keyword.STATIC);
        methodDeclaration.setType(type);
        methodDeclaration.addParameter("ItemStack", "itemStack");
        final BlockStmt body = methodDeclaration.createBody();
        body.addStatement(new ReturnStmt("ItemUtils.getData(itemStack, SkyblockDataComponentTypes.%s)".formatted(
            variableDeclarator.getNameAsString()
        )));

    }

    private void addValueAccessor(String name, ClassOrInterfaceType type, VariableDeclarator variableDeclarator,
                                  ClassOrInterfaceDeclaration declaration) {
        final MethodDeclaration methodDeclaration =
            declaration.addMethod(name, Modifier.Keyword.STATIC);
        methodDeclaration.setType("Value<" + type.getNameAsString() + ">");
        methodDeclaration.addParameter("ItemStack", "itemStack");
        final BlockStmt body = methodDeclaration.createBody();
        body.addStatement(
            new ReturnStmt("() -> ItemUtils.getData(itemStack, SkyblockDataComponentTypes.%s)".formatted(
                variableDeclarator.getNameAsString()
            )));
    }

    private void addInstanceMethod(String name, ClassOrInterfaceType type,
                                   ClassOrInterfaceDeclaration declaration) {
        final MethodDeclaration methodDeclaration = declaration.addMethod(name, Modifier.Keyword.DEFAULT);
        methodDeclaration.setType("Value<" + type.getNameAsString() + ">");
        final BlockStmt body = methodDeclaration.createBody();
        body.addStatement(new ReturnStmt("%s(this.getItemStack())".formatted(name)));
    }

}
