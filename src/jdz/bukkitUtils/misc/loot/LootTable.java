
package jdz.bukkitUtils.misc.loot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

public abstract class LootTable {
	private final Map<Loot, Double> weighting = new HashMap<Loot, Double>();
	@Getter private static double totalWeight = 0;

	public void addLoot(Loot loot) {
		addLoot(loot, 1);
	}

	public void addLoot(Loot loot, double weight) {
		if (weighting.keySet().contains(loot))
			weighting.put(loot, weighting.get(loot));
		else
			weighting.put(loot, weight);
		totalWeight += weight;
	}

	public boolean removeLoot(Loot loot) {
		if (weighting.containsKey(loot)) {
			totalWeight -= weighting.remove(loot);
			return true;
		}
		return false;
	}

	public Loot getRandom() {
		double rand = Math.random() * totalWeight;
		for (Loot item : weighting.keySet()) {
			rand -= weighting.get(item);
			if (rand <= 0)
				return item;
		}
		return weighting.keySet().iterator().next();
	}

	public Set<Loot> getAllLoot() {
		return weighting.keySet();
	}

	public double getProbability(Loot loot) {
		if (!weighting.containsKey(loot))
			return -1;
		return weighting.get(loot) / totalWeight;
	}
}
