package codes.cookies.mod.datagen;

import codes.cookies.mod.translations.TranslationKeys;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

/**
 * Language provider to generate language files.
 */
public abstract class CookiesLanguageProvider extends FabricLanguageProvider implements TranslationKeys {
    protected CookiesLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(
        RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        this.generateLocals(registryLookup, translationBuilder::add);
    }

    protected abstract void generateLocals(
        RegistryWrapper.WrapperLookup registryLookup,
        CookiesTranslationBuilder translationBuilder);

	/**
	 * Expanded translation builder to allow for easy config value addition.
	 */
    public interface CookiesTranslationBuilder extends FabricLanguageProvider.TranslationBuilder {

        default void addConfig(String key, String name, String tooltip) {
            this.add(key + TranslationKeys.NAME_SUFFIX, name);
            this.add(key + TranslationKeys.TOOLTIP_SUFFIX, tooltip);
        }

    }

}
