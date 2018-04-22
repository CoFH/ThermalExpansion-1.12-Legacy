package cofh.thermalexpansion.util.parsers;

import cofh.core.init.CoreProps;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.parsers.dynamo.*;
import cofh.thermalexpansion.util.parsers.machine.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gnu.trove.map.hash.THashMap;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class ContentParser {

	private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static File contentFolder;
	private static THashMap<String, IContentParser> contentParsers = new THashMap<>();

	private ContentParser() {

	}

	public static void initialize() {

		contentFolder = new File(CoreProps.configDir, "/cofh/thermalexpansion/content/");
		if (!contentFolder.exists()) {
			try {
				contentFolder.mkdir();
			} catch (Throwable t) {
				// pokemon!
			}
		}
		contentParsers.put("oredict", new OreDictParser());
		contentParsers.put("constants", new ConstantParser());

		contentParsers.put("furnace", new FurnaceParser());
		contentParsers.put("pulverizer", new PulverizerParser());
		contentParsers.put("sawmill", new SawmillParser());
		contentParsers.put("smelter", new SmelterParser());
		contentParsers.put("compactor", new CompactorParser());
		contentParsers.put("crucible", new CrucibleParser());
		contentParsers.put("refinery", new RefineryParser());
		contentParsers.put("charger", new ChargerParser());

		contentParsers.put("dynamo_steam", new SteamParser());
		contentParsers.put("dynamo_magmatic", new MagmaticParser());
		contentParsers.put("dynamo_compression", new CompressionParser());
		contentParsers.put("dynamo_reactant", new ReactantParser());
		contentParsers.put("dynamo_enervation", new EnervationParser());
		contentParsers.put("dynamo_numismatic", new NumismaticParser());
	}

	private static void addFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles((file, name) -> name != null && (name.toLowerCase(Locale.US).endsWith(".json") || new File(file, name).isDirectory()));

		if (fList == null || fList.length <= 0) {
			ThermalExpansion.LOG.info("There are no content files present in " + folder + ".");
			return;
		}
		ThermalExpansion.LOG.info(fList.length + " content files present in " + folder + "/.");
		list.addAll(Arrays.asList(fList));
	}

	public static void parseFiles() {

		ModContainer mod = FMLCommonHandler.instance().findContainerFor(ThermalExpansion.MOD_ID);

		JsonContext ctx = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(mod, "assets/" + ThermalExpansion.MOD_ID + "/content/", null, new BiFunction<Path, Path, Boolean>() {

			@Override
			public Boolean apply(Path root, Path file) {

				String relative = root.relativize(file).toString();
				if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) {
					return true;
				}
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					for (Entry<String, JsonElement> contentEntry : json.entrySet()) {
						if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
							ThermalExpansion.LOG.debug("Content entry added from file " + relative + ": \"" + contentEntry.getKey() + "\"");
						} else {
							ThermalExpansion.LOG.error("Error parsing entry from file " + relative + ": \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
						}
					}
				} catch (IOException e) {
					ThermalExpansion.LOG.error("Error parsing content file " + relative + "!", e);
				} finally {
					IOUtils.closeQuietly(reader);
				}
				return true;
			}
		}, false, false);
	}

	private static boolean parseEntry(String type, JsonElement content) {

		if (contentParsers.containsKey(type)) {
			return contentParsers.get(type).parseContent(content);
		}
		return false;
	}

}
