package net.insomniacs.nucleus.impl;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.insomniacs.nucleus.api.NucleusDataGenerator;
import net.insomniacs.nucleus.api.annotations.Translate;
import net.insomniacs.nucleus.impl.utility.ProviderUtils;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;

import static net.insomniacs.nucleus.impl.utility.AnnotationUtils.getAnnotation;
import static net.insomniacs.nucleus.impl.utility.ProviderUtils.streamAllRegistries;
import static net.minecraft.util.Util.createTranslationKey;

public class NucleusLanguageProvider extends FabricLanguageProvider {

	private final NucleusDataGenerator generator;

	public NucleusLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup, NucleusDataGenerator generator) {
		super(dataOutput, registryLookup);

		this.generator = generator;
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
		ProviderUtils.streamAllRegistries(generator, (registry, entry) -> generateTranslation(builder, registry, entry));
	}

	public static void generateTranslation(TranslationBuilder builder, Registry<?> registry, RegistryEntry.Reference<?> entry) {
		var value = entry.value();
		var id = new Identifier(entry.getIdAsString());
		var parsedID = ProviderUtils.parseID(id);

		var entryClazz = value.getClass();
		var annotation = getAnnotation(entryClazz, Translate.class);

		var translationKey = Util.createTranslationKey(registry.getKey().getValue().getPath(), id);

		if (annotation != null)
			builder.add(translationKey, annotation.name());
		else
			builder.add(translationKey, parsedID);
	}

}
