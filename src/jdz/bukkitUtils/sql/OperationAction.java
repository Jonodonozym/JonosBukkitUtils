
package jdz.bukkitUtils.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OperationAction {
	RESTRICT("RESTRICT"), CASCADE("CASCADE"), SET_NULL("SET NULL"), SET_DEFAULT("SET DEFAULT");

	@Getter private final String SQLSyntax;
}
