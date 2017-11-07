
package jdz.jbu.commands;

import java.lang.annotation.Repeatable;


@Repeatable(CommandLabels.class)
public @interface CommandLabel {
	public String value();
}
