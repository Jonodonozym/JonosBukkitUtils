
package jdz.bukkitUtils.interactableObject;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ObjectType {
	public String value();
	public boolean canBreak() default false;
}
