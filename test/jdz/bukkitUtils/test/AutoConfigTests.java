
package jdz.bukkitUtils.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;

import jdz.bukkitUtils.config.AutoConfig;
import jdz.bukkitUtils.config.YML.AutoConfigIO;

public class AutoConfigTests {
	private static Field field = AutoConfigClass.class.getDeclaredFields()[0];
	private static Field testField = AutoConfigClass.class.getDeclaredFields()[1];
	
	@SuppressWarnings("unused") 
	private static class AutoConfigClass extends AutoConfig {
		private static List<String> stringList = new ArrayList<>(Arrays.asList("test"));
		private static String test = "sasdf";
		
		protected AutoConfigClass(Plugin plugin) {
			super(plugin);
		}
		
		public static void setField(Object value) throws ReflectiveOperationException {
			testField.set(null, value);
		}
	}
	
	@Before
	public void setup() {
		new AutoConfigClass(null);
	}
	
	@Test
	public void saveCollection() {
		ConfigurationSection section = mock(ConfigurationSection.class);
		AutoConfigIO.save(field.getGenericType(), field.getType(), section, "asdf", AutoConfigClass.stringList);
		verify(section).set("asdf.type", "java.util.ArrayList");
		verify(section).set("asdf.0", "test");
	}

	@Test
	public void loadCollection() throws Exception {
		ConfigurationSection section = mock(ConfigurationSection.class);
		ConfigurationSection subSection = mock(ConfigurationSection.class);

		when(section.getConfigurationSection("asdf")).thenReturn(subSection);

		when(subSection.getString("type")).thenReturn("java.util.ArrayList");
		when(subSection.getString("0")).thenReturn("test");

		when(subSection.getKeys(false)).thenReturn(new HashSet<String>(Arrays.asList("type", "0")));

		Object o = AutoConfigIO.parse(field.getGenericType(), field.getType(), section, "asdf");

		System.out.println(o);
	}

	@Test
	public void accessability() throws ReflectiveOperationException {
		AutoConfigClass.setField("asdf");
	}

}
