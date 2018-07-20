
package jdz.bukkitUtils.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.interactableObject.InteractableObject;
import jdz.bukkitUtils.interactableObject.InteractableObjectFactory;
import jdz.bukkitUtils.interactableObject.InteractableObjectListener;

import static org.powermock.api.mockito.PowerMockito.*;

import lombok.Getter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JonosBukkitUtils.class)
public class InteractableObjectTests {

	private InteractableObjectFactory<InterObj> getFactory() {
		return new InteractableObjectFactory<InterObj>(InterObj.class, (metadata) -> {
			return new InterObj(metadata);
		});
	}

	private Metadatable meta = new Meta();
	private Plugin plugin = mock(Plugin.class);
	private InteractableObjectFactory<InterObj> factory = getFactory();
	
	@Before
	public void setup() {
	    mockStatic(JonosBukkitUtils.class);
	    when(JonosBukkitUtils.getInstance()).thenReturn(plugin);
	}

	@Test
	public void checkAssigned() {
		int a = 1, b = 4;

		InterObj obj = new InterObj(meta, a, b);
		assertEquals(a, obj.getA());
		assertEquals(b, obj.getB());

		InterObj obj2 = factory.getMaker().make(meta);

		assertEquals(a, obj2.getA());
		assertEquals(b, obj2.getB());
	}

	@Test
	public void checkListener() {
		int a = 1, b = 4;

		factory.register(plugin);

		Player player = mock(Player.class);
		Entity entity = mock(Entity.class);
		when(entity.getMetadata("interactType"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(plugin, InterObj.class.getSimpleName())));

		when(entity.hasMetadata("a")).thenReturn(true);
		when(entity.hasMetadata("b")).thenReturn(true);
		when(entity.getMetadata("a")).thenReturn(Arrays.asList(new FixedMetadataValue(plugin, a)));
		when(entity.getMetadata("b")).thenReturn(Arrays.asList(new FixedMetadataValue(plugin, b)));

		PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(player, entity, new Vector());
		new InteractableObjectListener().onInteract(event);
		factory.unregister();
		assertTrue(InterObj.isInteracted());
	}

	@Test
	public void checkThrowsOnMultiRegister() {
		Plugin plugin1 = mock(Plugin.class);
		Plugin plugin2 = mock(Plugin.class);
		getFactory().register(plugin1);
		assertThrows(Exception.class, () -> {
			getFactory().register(plugin2);
		});
		getFactory().unregister();
	}

	private static class InterObj extends InteractableObject {
		@Getter private static boolean interacted;
		@Getter private int a;
		@Getter private int b;

		protected InterObj(Metadatable object) {
			super(object);
			readMetadata(object);
		}

		protected InterObj(Metadatable object, int a, int b) {
			super(object);
			this.a = a;
			this.b = b;
			writeMetadata(object);
		}

		@Override
		public void onInteract(Player player) {
			interacted = true;
		}

		@Override
		public String[] getFields() {
			return new String[] { "a", "b" };
		}
	}

	private static class Meta implements Metadatable {
		private final Map<String, MetadataValue> map = new HashMap<>();

		@Override
		public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
			map.put(metadataKey, newMetadataValue);
		}

		@Override
		public List<MetadataValue> getMetadata(String metadataKey) {
			return Arrays.asList(map.get(metadataKey));
		}

		@Override
		public boolean hasMetadata(String metadataKey) {
			return map.containsKey(metadataKey);
		}

		@Override
		public void removeMetadata(String metadataKey, Plugin owningPlugin) {
			map.remove(metadataKey);
		}
	}

}