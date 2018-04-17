package cofh.thermalexpansion.util.parsers;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class NuContentParser {

	private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private static final ObfMapping mapping = new ObfMapping(//
			"net/minecraftforge/common/crafting/JsonContext",//
			"loadConstants",//
			"([Lcom/google/gson/JsonObject;)V"//
	);

	private static final BiConsumer<JsonContext, JsonObject[]> callLoadContext =//
			(ctx, json) -> ReflectionManager.callMethod(mapping, null, ctx, new Object[] { json });

	private static void loadRecipes(ModContainer mod, String type, TriConsumer<JsonObject, JsonContext, ResourceLocation> loadRecipe) {

		JsonContext ctx = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/avaritia_recipes/" + type, root -> {
			Path fPath = root.resolve("_constants.json");
			if (fPath != null && Files.exists(fPath)) {
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(fPath);
					JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
					callLoadContext.accept(ctx, json);
				} catch (IOException e) {
					//Lumberjack.log(Level.ERROR, e, "Error loading _constants.json: ");
					return false;
				} finally {
					IOUtils.closeQuietly(reader);
				}
			}
			return true;
		}, (root, file) -> {
			Loader.instance().setActiveModContainer(mod);

			String relative = root.relativize(file).toString();
			if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) {
				return true;
			}

			String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
			ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file);
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				if (json.has("conditions") && !CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), ctx)) {
					return true;
				}
				loadRecipe.accept(json, ctx, key);
			} catch (JsonParseException e) {
				//Lumberjack.log(Level.ERROR, e, "Parsing error loading recipe %s", key);
				return false;
			} catch (IOException e) {
				//Lumberjack.log(Level.ERROR, e, "Couldn't read recipe %s from %s", key, file);
				return false;
			} finally {
				IOUtils.closeQuietly(reader);
			}
			return true;
		}, false, false);
	}
}
