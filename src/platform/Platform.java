package platform;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import client.IPluginDescriptor;
import client.PluginDescriptor;
import client.PluginState;
import client.UnassignableException;
import platform.plugins.IAutorun;

public class Platform {

	private static List<IPluginDescriptor> pluginDescript;

	public static List<IPluginDescriptor> getPluginDescript() {
		return pluginDescript;
	}

	public static void setPluginDescript(List<IPluginDescriptor> pluginDescript) {
		Platform.pluginDescript = pluginDescript;
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {


		loadPluginDescriptors();
		// Run autoruns
		for (IPluginDescriptor plugin : pluginDescript) {
			if(plugin.getProperties().get("autorun").equals("True")){
				String classPlugin = plugin.getProperties().get("class");
				Class<?> cl = Class.forName(classPlugin);
				IAutorun obj = (IAutorun) cl.newInstance();
				
				obj.run();
			}
		}
	}

	public static Object getExtension(Class<?> targetClass)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return targetClass.newInstance();
	}

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

	public static void loadPluginDescriptors() {

		List<String> plugins = extractArrayFromJSON("plugins", "config.json");

		IPluginDescriptor desc;
		pluginDescript = new ArrayList<IPluginDescriptor>();
		
		for (String p : plugins) {

			String[] tmp = p.split(Pattern.quote("."));

			String pluginFile = "pluginConfig/" + tmp[tmp.length - 1] + ".json";

			Map<String, String> prop = jsonIntoMap(pluginFile);

			desc = new PluginDescriptor(prop);
			
			pluginDescript.add(desc);
		}
	}

	public static Object loadPlugin(IPluginDescriptor iPluginDescriptor, Class<?> need) {
		
		Object obj = null;
		
		if(iPluginDescriptor.getState() == PluginState.AVAILABLE){
			try {
				Class<?> cl = Class.forName(iPluginDescriptor.getProperties().get("class"));
				
				if(need.isAssignableFrom(cl)){
					obj = cl.newInstance();
				}else{
					throw new UnassignableException();
				}
				
			} catch (ClassNotFoundException | UnassignableException | InstantiationException | IllegalAccessException e) {
				iPluginDescriptor.setState(PluginState.FAILED);
			}
		}
		
		iPluginDescriptor.setState(PluginState.RUNNING);
		return obj;
	}
	
	private static List<String> extractArrayFromJSON(String prop, String fileName) {
		try {
			String json = new String();
			String line = null;
			try {

				BufferedReader reader = new BufferedReader(new FileReader(fileName));

				while ((line = reader.readLine()) != null) {
					json += line;
				}
				reader.close();

				JSONObject obj = new JSONObject(json);

				JSONArray array = obj.getJSONArray(prop);
				List<String> items = new ArrayList<String>();
				for (int i = 0; i < array.length(); ++i) {
					items.add(array.getString(i));
				}
				return items;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String extractStringFromJSON(String prop, String fileName) {
		try {
			String json = new String();
			String line = null;
			try {

				BufferedReader reader = new BufferedReader(new FileReader(fileName));

				while ((line = reader.readLine()) != null) {
					json += line;
				}
				reader.close();

				JSONObject obj = new JSONObject(json);

				return obj.getString(prop);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Map<String, String> jsonIntoMap(String fileName) {
		try {
			String json = new String();
			String line = null;
			try {

				BufferedReader reader = new BufferedReader(new FileReader(fileName));

				while ((line = reader.readLine()) != null) {
					json += line;
				}
				reader.close();

				JSONObject obj = new JSONObject(json);
				Map<String, String> map = new HashMap<>();
				
				for (Iterator iterator = obj.keys(); iterator.hasNext();) {
					Object o = iterator.next();
					map.put((String) o, obj.getString((String) o));

				}
				/*while (it.hasNext()) {
					o = obj.keys().next();
					System.out.println((String) o);
				}*/

				return map;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}


}
