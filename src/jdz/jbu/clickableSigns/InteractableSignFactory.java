
package jdz.jbu.clickableSigns;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import jdz.jbu.JonosBukkitUtils;
import jdz.jbu.fileIO.FileLogger;
import jdz.jbu.misc.WorldUtils;

class InteractableSignFactory {
	private final List<Class<? extends InteractableSign>> classes;
	
	@SafeVarargs
	public InteractableSignFactory(Class<? extends InteractableSign>... classes) {
		this.classes = Arrays.asList(classes);
	}

	public final InteractableSign construct(Block block) throws InvalidSignException {
		if (block.getState() == null || !(block.getState() instanceof Sign))
			return null;

		Sign sign = (Sign) block.getState();
		
		for (Class<? extends InteractableSign> c : classes) {
			SignType type = c.getAnnotation(SignType.class);
			if (type != null && sign.getLine(0).toLowerCase().contains("[" + type.value().toLowerCase() + "]"))
				try {
					return (InteractableSign) c.getConstructors()[0].newInstance(block);
				} catch (InstantiationException | IllegalAccessException
						| InvocationTargetException | SecurityException e) {
					new FileLogger(JonosBukkitUtils.instance).createErrorLog(e);
				} catch (IllegalArgumentException e) {
					throw new InvalidSignException(e);
				}
		}

		return null;
	}

	@Deprecated
	public static final InteractableSign construct(Block block, Class<? extends InteractableSign> c)
			throws InvalidSignException {
		if (block.getState() == null || !(block.getState() instanceof Sign))
			throw new InvalidSignException(
					"Block at " + WorldUtils.locationToString(block.getLocation()) + " is not a sign");

		Sign sign = (Sign) block.getState();
		SignType type = c.getAnnotation(SignType.class);

		if (type == null)
			throw new InvalidSignException("SignType annotation not used in " + c.getName() + " class");

		if (!sign.getLine(0).equalsIgnoreCase("[" + type.value() + "]"))
			throw new InvalidSignException("First line on the sign must be [" + type.value() + "]");

		try {
			return (InteractableSign) c.getConstructors()[0].newInstance(block);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			new FileLogger(JonosBukkitUtils.instance).createErrorLog(e);
			throw new InvalidSignException(e);
		}
	}
}
