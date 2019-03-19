
package jdz.bukkitUtils.configuration.YML;

import java.text.ParseException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static jdz.bukkitUtils.utils.ItemUtils.*;

public class ItemStackParserSerializer implements ConfigParser<ItemStack>, ConfigSerializer<ItemStack> {
	@Override
	public void save(ConfigurationSection section, String path, Object value) {
		ConfigurationSection itemSection = section.getConfigurationSection(path);
		ItemStack item = (ItemStack) value;
		itemSection.set("item", item.getType().name());
		itemSection.set("amount", 1);
		if (hasName(item))
			itemSection.set("name", getName(item));
		if (hasLore(item))
			itemSection.set("lore", getLore(item));
		if (isDamageable(item))
			itemSection.set("damage", getDamage(item));
		if (hasEnchants(item))
			saveEnchants(item, section.createSection("enchants"));
		if (isPotion(item.getType()))
			savePotionEffects(item, section.createSection("effects"));
	}

	private void saveEnchants(ItemStack item, ConfigurationSection enchantsSection) {
		for (Enchantment enchant : item.getEnchantments().keySet()) {
			@SuppressWarnings("deprecation")
			ConfigurationSection enchantSection = enchantsSection.createSection(enchant.getName());
			enchantSection.set("level", item.getEnchantmentLevel(enchant));
		}
	}

	private void savePotionEffects(ItemStack item, ConfigurationSection effectsSection) {
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		for (PotionEffect effect : meta.getCustomEffects()) {
			ConfigurationSection effectSection = effectsSection.createSection(effect.getType().getName());
			effectSection.set("seconds", effect.getDuration() * 20);
			effectSection.set("level", effect.getAmplifier());
		}
	}

	@Override
	public ItemStack parse(ConfigurationSection section, String path) throws ParseException {
		ConfigurationSection itemSection = section.getConfigurationSection(path);

		Material material = Material.valueOf(itemSection.getString("item"));
		int amount = itemSection.getInt("amount", 1);

		ItemStack item = new ItemStack(material, amount);

		if (itemSection.contains("name"))
			setName(item, itemSection.getString("name"));

		if (itemSection.contains("lore"))
			setLore(item, itemSection.getStringList("lore"));

		if (itemSection.contains("damage"))
			setDamage(item, itemSection.getInt("damage"));
		if (itemSection.contains("data"))
			setData(item, itemSection.getInt("data"));

		if (itemSection.contains("enchants"))
			applyEnchants(item, itemSection);
		if (itemSection.contains("potionEffects") && isPotion(material))
			applyPotionEffects(item, itemSection);

		return item;
	}

	private void applyEnchants(ItemStack item, ConfigurationSection itemSection) {
		ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchants");
		for (String key : enchantsSection.getKeys(false))
			applyEnchant(item, key, enchantsSection.getConfigurationSection(key));
	}

	private void applyEnchant(ItemStack item, String key, ConfigurationSection enchantSection) {
		try {
			@SuppressWarnings("deprecation") // TODO replace with better method
			Enchantment enchant = Enchantment.getByName(key);
			int level = enchantSection.getInt("level", 0);
			item.addUnsafeEnchantment(enchant, level);
		}
		catch (Exception e) {
			Bukkit.getLogger().warning("Error parsing enchantment at" + enchantSection.getCurrentPath() + ", skipping");
		}
	}

	private boolean isPotion(Material material) {
		return material == Material.POTION || material == Material.TIPPED_ARROW || material == Material.LINGERING_POTION
				|| material == Material.SPLASH_POTION;
	}

	private void applyPotionEffects(ItemStack item, ConfigurationSection itemSection) {
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		ConfigurationSection effectsSection = itemSection.getConfigurationSection("effects");
		for (String effect : effectsSection.getKeys(false))
			applyPotionEffect(meta, effectsSection.getConfigurationSection(effect));
	}

	private void applyPotionEffect(PotionMeta meta, ConfigurationSection effectSection) {
		try {
			PotionEffectType type = PotionEffectType.getByName(effectSection.getString("type").toUpperCase());
			int ticks = effectSection.getInt("seconds", 1) * 20;
			int level = effectSection.getInt("level", 0);
			meta.addCustomEffect(new PotionEffect(type, ticks, level), true);
		}
		catch (Exception e) {
			Bukkit.getLogger()
					.warning("Error parsing potion effect at" + effectSection.getCurrentPath() + ", skipping");
		}
	}

}