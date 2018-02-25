
package jdz.bukkitUtils.clickableSigns;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileLogger;

class InteractableSignFactory {
	private final List<Class<? extends InteractableSign>> classes;

	@SafeVarargs
	public InteractableSignFactory(Class<? extends InteractableSign>... classes) {
		this.classes = Arrays.asList(classes);
	}

	public final InteractableSign construct(Block block, String[] lines) throws InvalidSignException {
		if (block.getState() == null || !(block.getState() instanceof Sign))
			return null;

		Sign sign = (Sign) block.getState();
		for (int i = 0; i < lines.length; i++)
			sign.setLine(i, lines[i]);

		return construct(block, sign);
	}

	public final InteractableSign construct(Block block, Sign sign) throws InvalidSignException {
		if (block.getState() == null || !(block.getState() instanceof Sign))
			return null;

		for (Class<? extends InteractableSign> c : classes) {
			SignType[] types = c.getAnnotationsByType(SignType.class);
			for (SignType type : types) {
				if (type != null && sign.getLine(0).toLowerCase().contains("[" + type.value().toLowerCase() + "]"))
					try {
						return (InteractableSign) c.getConstructors()[0].newInstance(block, sign);
					}
					catch (InstantiationException | IllegalAccessException | InvocationTargetException
							| SecurityException e) {

						if (e.getCause() != null && e.getCause() instanceof InvalidSignException)
							throw (InvalidSignException) e.getCause();
						new FileLogger(JonosBukkitUtils.instance).createErrorLog(e);
					}
					catch (IllegalArgumentException e) {
						throw new InvalidSignException(
								type.value() + " doesn't have the constructor agruments Block and Sign!");
					}
			}
		}

		return null;
	}
}
