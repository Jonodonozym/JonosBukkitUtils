
package jdz.bukkitUtils.misc;

public class Random {
	private static final java.util.Random random = new java.util.Random();

	private Random() {}

	public static int nextInt(int bound) {
		return random.nextInt(bound);
	}

	public static long nextLong(long bound) {
		return (long) nextDouble(bound);
	}

	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	public static float nextFloat() {
		return random.nextFloat();
	}

	public static float nextFloat(float bound) {
		return random.nextFloat() * bound;
	}

	public static double nextDouble() {
		return random.nextDouble();
	}

	public static double nextDouble(double bound) {
		return random.nextDouble() * bound;
	}

	public static void setSeed(Long seed) {
		random.setSeed(seed);
	}
}
