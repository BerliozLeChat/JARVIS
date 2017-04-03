package platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import client.UnassignableException;
import platform.plugins.IAutorun;

/**
 *  Platform,
 *
 *
 */
public class Platform {

	private static List<IPluginDescriptor> pluginDescript;
	
	/**
	 * 
	 * @return 
	 */
	public static List<IPluginDescriptor> getPluginDescript() {
		return pluginDescript;
	}

	/**
	 * 
	 * @param pluginDescript
	 */
	public static void setPluginDescript(List<IPluginDescriptor> pluginDescript) {
		Platform.pluginDescript = pluginDescript;
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException {		

		loadPluginDescriptors();
		// Run autoruns
		for (IPluginDescriptor plugin : pluginDescript) {
			if(plugin.getProperties().get("autorun").equals("True")){
				IAutorun obj = (IAutorun) loadPlugin(plugin, IAutorun.class);

				obj.run();
			}
		}		
	}

	/**
	 * 
	 * @param need
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List<IPluginDescriptor> getExtensions(Class<?> need) throws ClassNotFoundException {

		List<IPluginDescriptor> plugins = new ArrayList<IPluginDescriptor>();
		
		for (IPluginDescriptor plugin : pluginDescript) {
			
			String interfacePath = plugin.getProperties().get("interface");

			if(interfacePath.equals(need.getName())){
				plugins.add(plugin);
			}
		}
		
		return plugins;
	}
	
	/**
	 * 
	 * @param need
	 * @param properties
	 * @return 
	 * @throws ClassNotFoundException
	 */
	public static List<IPluginDescriptor> getExtensions(Class<?> need, Map<String, Object> properties) throws ClassNotFoundException {

		List<IPluginDescriptor> plugins = new ArrayList<IPluginDescriptor>();
		
		for (IPluginDescriptor plugin : pluginDescript) {
			
			String interfacePath = plugin.getProperties().get("interface");

			if(interfacePath.equals(need.getName())){
				
				boolean matches = true;
				for (Object key : properties.keySet()){
					matches = matches && (plugin.getProperties().get(key).equals(properties.get(key)));
				}
				if (matches){
					plugins.add(plugin);
				}
			}
		}
		
		return plugins;
	}

	/**
	 * 
	 * @throws FileNotFoundException
	 */
	private static void loadPluginDescriptors() throws FileNotFoundException {

		
		InputStream input = new FileInputStream(new File("config.yaml"));
	    Yaml yaml = new Yaml();
	    @SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) yaml.load(input);
		@SuppressWarnings("unchecked")
		List<String> plugins = (List<String>) map.get("plugins");

		IPluginDescriptor desc;
		pluginDescript = new ArrayList<IPluginDescriptor>();
		
		for (String p : plugins) {
			String[] tmp = p.split(Pattern.quote("."));
			String pluginFile = "pluginConfig/" + tmp[tmp.length - 1] + ".yaml";
			
			InputStream pluginConf = new FileInputStream(new File(pluginFile));
			@SuppressWarnings("unchecked")
			Map<String, String> prop = (Map<String, String>) yaml.load(pluginConf);
			desc = new PluginDescriptor(prop);
			pluginDescript.add(desc);
			
		}
	}

	/**
	 * 
	 * @param iPluginDescriptor
	 * @param need
	 * @return
	 */
	public static Object loadPlugin(IPluginDescriptor iPluginDescriptor, Class<?> need) {
		
		Object obj = null;
		
		if(iPluginDescriptor.getState() == PluginState.AVAILABLE){
			try {
				Class<?> cl = Class.forName(iPluginDescriptor.getProperties().get("class"));
				
				if(need.isAssignableFrom(cl)){
					obj = cl.newInstance();
					iPluginDescriptor.addInstance(obj);
				}else{
					throw new UnassignableException();
				}
				iPluginDescriptor.setState(PluginState.RUNNING);
				
			} catch (ClassNotFoundException | UnassignableException | InstantiationException | IllegalAccessException e) {
				iPluginDescriptor.setState(PluginState.FAILED);
			}
		} else if (iPluginDescriptor.getState() == PluginState.RUNNING) {
			if (iPluginDescriptor.getProperties().get("singleton").equals("True")){
				return iPluginDescriptor.getInstances().get(0);
			} else {
				try {
					Class<?> cl = Class.forName(iPluginDescriptor.getProperties().get("class"));
					
					if(need.isAssignableFrom(cl)){
						obj = cl.newInstance();
						iPluginDescriptor.addInstance(obj);
					}else{
						throw new UnassignableException();
					}
					iPluginDescriptor.setState(PluginState.RUNNING);
					
				} catch (ClassNotFoundException | UnassignableException | InstantiationException | IllegalAccessException e) {
					iPluginDescriptor.setState(PluginState.FAILED);
				}
			}
		}
		return obj;
	}
}
