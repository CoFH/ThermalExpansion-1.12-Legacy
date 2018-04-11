package cofh.thermalexpansion.util.parsers;

import cofh.core.init.CoreProps;
import cofh.thermalexpansion.ThermalExpansion;
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
		contentParsers.put("pulverizer", new PulverizerParser());
		contentParsers.put("sawmill", new SawmillParser());
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
			ThermalExpansion.LOG.info("Reading template info from: " + contentFile + ":");
			for (Entry<String, JsonElement> contentEntry : contentList.entrySet()) {
				if (parseEntry(contentEntry.getValue())) {
					ThermalExpansion.LOG.debug("Content entry added: \"" + contentEntry.getKey() + "\"");
				} else {
					ThermalExpansion.LOG.error("Error handling entry: \"" + contentEntry.getKey() + "\" > Please check the parameters. If adding a recipe, it *may* conflict with an existing recipe or entry. If removing a recipe, the recipe may not have existed.");
				}
			}
		}
	}

	private static boolean parseEntry(JsonElement contentObject) {

		JsonObject content = contentObject.getAsJsonObject();
		String type = content.get("type").getAsString();

		if (contentParsers.containsKey(type)) {
			return contentParsers.get(type).parseContent(content);
		}
		return false;
	}

}
