
package jdz.bukkitUtils.commands.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Deprecated
@Retention(RUNTIME)
@Target(TYPE)
public @interface CommandAsync {

}
