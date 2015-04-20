package net.eithon.plugin.freebuild;

import java.util.List;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;

public class Config {
	public static void load(EithonPlugin plugin)
	{
		Configuration config = plugin.getConfiguration();
		V.load(config);
		C.load(config);
		M.load(config);

	}
	public static class V {
		public static int coolDownTimeInMinutes;
		public static  List<String> applicableWorlds;

		static void load(Configuration config) {
			coolDownTimeInMinutes = config.getInt("CoolDownTimeInMinutes", 30);
			applicableWorlds = config.getStringList("FreebuildWorldNames");	
		}

	}
	public static class C {

		static void load(Configuration config) {
		}

	}
	public static class M {
		public static ConfigurableMessage waitForCoolDown;
		public static ConfigurableMessage mustBeInFreebuildWord;

		static void load(Configuration config) {
			waitForCoolDown = config.getConfigurableMessage(
					"messages.WaitForCoolDown", 2, 
					"The remaining cool down period for switching Freebuild mode is %d minutes and %d seconds.");
			mustBeInFreebuildWord = config.getConfigurableMessage(
					"messages.MustBeInFreebuildWord", 0, 
					"You can only switch between survival and freebuild in the SurvivalFreebuild world.");
		}		
	}

}
