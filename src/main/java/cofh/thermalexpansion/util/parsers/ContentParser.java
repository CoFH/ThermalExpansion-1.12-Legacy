package cofh.thermalexpansion.util.parsers;

import cofh.core.init.CoreProps;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.parsers.dynamo.*;
import cofh.thermalexpansion.util.parsers.machine.*;
import com.google.gson.*;
import gnu.trove.map.hash.THashMap;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;

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

	private static void copyInternalFiles() {

	}

	private static void addConstantFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles((file, name) -> name != null && ((name.toLowerCase(Locale.US).endsWith(".json") && name.startsWith("_")) || new File(file, name).isDirectory()));

		if (fList == null || fList.length <= 0) {
			return;
		}
		list.addAll(Arrays.asList(fList));
	}

	private static void addContentFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles((file, name) -> name != null && ((name.toLowerCase(Locale.US).endsWith(".json") && !name.startsWith("_")) || new File(file, name).isDirectory()));

		if (fList == null || fList.length <= 0) {
			return;
		}
		list.addAll(Arrays.asList(fList));
	}

	public static void parseFiles() {

		ModContainer mod = FMLCommonHandler.instance().findContainerFor(ThermalExpansion.MOD_ID);

		parseCustomConstants();
		parseConstants(mod);

		parseCustomContent();
		parseContent(mod);
	}

	public static void parseCustomConstants() {

		ArrayList<File> constantList = new ArrayList<>();
		addConstantFiles(constantList, contentFolder);

		for (int i = 0; i < constantList.size(); ++i) {
			File file = constantList.get(i);
			if (file.isDirectory()) {
				addConstantFiles(constantList, file);
				continue;
			}
			String fileName = file.getName();
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file.toPath());
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				for (Entry<String, JsonElement> contentEntry : json.entrySet()) {
					if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
						ThermalExpansion.LOG.debug("Content entry added from file " + fileName + ": \"" + contentEntry.getKey() + "\"");
					} else {
						ThermalExpansion.LOG.error("Error parsing entry from file " + fileName + ": \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
					}
				}
			} catch (Exception e) {
				ThermalExpansion.LOG.error("Error parsing content file " + fileName + "!", e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	public static void parseCustomContent() {

		ArrayList<File> contentList = new ArrayList<>();
		addContentFiles(contentList, contentFolder);

		for (int i = 0; i < contentList.size(); ++i) {
			File file = contentList.get(i);
			if (file.isDirectory()) {
				addContentFiles(contentList, file);
				continue;
			}
			String fileName = file.getName();
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file.toPath());
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				for (Entry<String, JsonElement> contentEntry : json.entrySet()) {
					if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
						ThermalExpansion.LOG.debug("Content entry added from file " + fileName + ": \"" + contentEntry.getKey() + "\"");
					} else {
						ThermalExpansion.LOG.error("Error parsing entry from file " + fileName + ": \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
					}
				}
			} catch (Exception e) {
				ThermalExpansion.LOG.error("Error parsing content file " + fileName + "!", e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	public static void parseConstants(ModContainer mod) {

		CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/content/", null, (root, file) -> {

			String fileName = file.getFileName().toString();
			if (!"json".equals(FilenameUtils.getExtension(fileName)) || !fileName.startsWith("_")) {
				return true;
			}
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file);
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				for (Entry<String, JsonElement> contentEntry : json.entrySet()) {
					if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
						ThermalExpansion.LOG.debug("Content entry added from file " + fileName + ": \"" + contentEntry.getKey() + "\"");
					} else {
						ThermalExpansion.LOG.error("Error parsing entry from file " + fileName + ": \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
					}
				}
			} catch (Exception e) {
				ThermalExpansion.LOG.error("Error parsing content file " + fileName + "!", e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
			return true;
		}, false, false);
	}

	public static void parseContent(ModContainer mod) {

		CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/content/", null, (root, file) -> {

			String fileName = file.getFileName().toString();
			if (!"json".equals(FilenameUtils.getExtension(fileName)) || fileName.startsWith("_")) {
				return true;
			}
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file);
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				for (Entry<String, JsonElement> contentEntry : json.entrySet()) {
					if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
						ThermalExpansion.LOG.debug("Content entry added from file " + fileName + ": \"" + contentEntry.getKey() + "\"");
					} else {
						ThermalExpansion.LOG.error("Error parsing entry from file " + fileName + ": \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
					}
				}
			} catch (Exception e) {
				ThermalExpansion.LOG.error("Error parsing content file " + fileName + "!", e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
			return true;
		}, false, false);
	}

	private static boolean parseEntry(String type, JsonElement content) {

		if (contentParsers.containsKey(type)) {
			return contentParsers.get(type).parseContent(content);
		}
		return false;
	}

}
