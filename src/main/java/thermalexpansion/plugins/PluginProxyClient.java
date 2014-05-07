package thermalexpansion.plugins;

public class PluginProxyClient extends PluginProxy {

	@Override
	public void registerRenderInformation() {

		for (int i = 0; i < TEPlugins.pluginList.size(); i++) {
			TEPlugins.pluginList.get(i).registerRenderInformation();
		}
	}

}
