
package jdz.bukkitUtils.commands.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface CommandMethod {
	public boolean withSender() default true;

	public boolean parseFlags() default false;
}
