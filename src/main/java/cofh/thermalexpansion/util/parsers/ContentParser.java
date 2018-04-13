package cofh.thermalexpansion.util.parsers;

import cofh.core.init.CoreProps;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.parsers.dynamo.*;
import cofh.thermalexpansion.util.parsers.machine.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;

public class ContentParser {

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

		JsonParser parser = new JsonParser();

		ArrayList<File> contentFileList = new ArrayList<>();
		addFiles(contentFileList, contentFolder);

		for (int i = 0; i < contentFileList.size(); ++i) {
			File contentFile = contentFileList.get(i);
			if (contentFile.isDirectory()) {
				addFiles(contentFileList, contentFile);
				continue;
			}
			JsonObject contentList;
			try {
				contentList = (JsonObject) parser.parse(new FileReader(contentFile));
			} catch (Throwable t) {
				ThermalExpansion.LOG.error("Critical error reading from a content file: " + contentFile + " > Please be sure the file is correct!", t);
				continue;
			}
			ThermalExpansion.LOG.info("Reading content from: " + contentFile + "...");
			for (Entry<String, JsonElement> contentEntry : contentList.entrySet()) {
				if (parseEntry(contentEntry.getKey(), contentEntry.getValue())) {
					ThermalExpansion.LOG.debug("Content entry added: \"" + contentEntry.getKey() + "\"");
				} else {
					ThermalExpansion.LOG.error("Error parsing entry: \"" + contentEntry.getKey() + "\" > Please make sure the entry is a valid JSON Array.");
				}
			}
			//			for (IContentParser contentParser : contentParsers.values()) {
			//				// TODO: Recipe diagnostics.
			//			}
		}
	}

	private static boolean parseEntry(String type, JsonElement content) {

		if (contentParsers.containsKey(type)) {
			return contentParsers.get(type).parseContent(content);
		}
		return false;
	}

}
