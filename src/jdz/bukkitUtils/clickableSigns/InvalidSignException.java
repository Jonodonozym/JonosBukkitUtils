
package jdz.bukkitUtils.clickableSigns;

public class InvalidSignException extends Exception {
	private static final long serialVersionUID = -6857733185527698429L;

	public InvalidSignException(Exception e) {
		super(e);
	}

	public InvalidSignException(String e) {
		super(e);
	}
}
