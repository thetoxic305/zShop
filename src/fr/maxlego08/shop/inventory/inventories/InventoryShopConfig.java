package fr.maxlego08.shop.inventory.inventories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.shop.ZShop;
import fr.maxlego08.shop.inventory.ItemButton;
import fr.maxlego08.shop.inventory.VInventory;
import fr.maxlego08.shop.save.Config;
import fr.maxlego08.shop.save.Lang;
import fr.maxlego08.shop.zcore.utils.enums.Permission;
import fr.maxlego08.shop.zcore.utils.inventory.Button;
import fr.maxlego08.shop.zshop.categories.Category;
import fr.maxlego08.shop.zshop.factories.ShopItem;
import fr.maxlego08.shop.zshop.factories.ShopItem.ShopType;
import fr.maxlego08.shop.zshop.inventories.InventoryObject;
import fr.maxlego08.shop.zshop.items.ShopItemConsomable;
import fr.maxlego08.shop.zshop.utils.EnumCategory;

public class InventoryShopConfig extends VInventory {

	private InventoryObject object;
	private Category category;
	private List<ShopItem> items;
	private int maxPage;

	public InventoryShopConfig() {

		this.disableClick = false;

	}

	@Override
	public boolean openInventory(ZShop main, Player player, int page, Object... args) throws Exception {

		object = getInventoryObject();

		category = (Category) args[0];

		items = main.getItems().getItems(category.getId());

		int pageSize = items.size() < (category.getInventorySize() - 9) ? items.size()
				: (category.getInventorySize() - 9);

		maxPage = main.getItems().getMaxPage(items, category, pageSize);

		/**
		 * Cr�ation du nom de l'inventaire en fonction de son type
		 */
		String inventoryName = category.isItem() ? Lang.shopInventoryItem : Lang.shopInventoryUniqueItem;

		inventoryName = inventoryName.replace("%page%", String.valueOf(page)).replace("%maxPage%",
				String.valueOf(maxPage));

		createInventory(inventoryName.replace("%category%", category.getName()), category.getInventorySize());

		if (!category.getType().equals(ShopType.ITEM)) {
			object.getDecorations(category.getId()).forEach((slot, button) -> addItem(slot, button.getInitButton()));
		}

		if (category.getType().equals(ShopType.ITEM_SLOT)) {

			List<ShopItemConsomable> itemConsomables = main.getItems().shorItems(items, category.getInventorySize(),
					maxPage, page);

			itemConsomables.forEach(item -> addItem(item.getTmpSlot(), item.getDisplayItem()));

		} else {

			items.forEach(item -> addItem(item.getSlot(), new ItemButton(item.getDisplayItem())));

		}

		if (category.getType().equals(ShopType.ITEM_SLOT)) {
			/**
			 * Ajout des boutons pour changer de page si besoin
			 */
			if (getPage() != 1)
				addItem(category.getPreviousButtonSlot(),
						new ItemButton(Lang.previousButton.getInitButton()).setClick(event -> {
							main.getShop().openShop(player, EnumCategory.SHOP, page - 1, object.getId(),
									Permission.SHOP_OPEN.getPermission(category.getId()), args);
						}));
			if (getPage() != maxPage)
				addItem(category.getNexButtonSlot(), new ItemButton(Lang.nextButton.getInitButton()));
		}

		if (!Config.disableBackButton)
			addItem(category.getBackButtonSlot(), new ItemButton(Lang.backButton.getInitButton()));

		return true;
	}

	@Override
	protected void onClose(InventoryCloseEvent event, ZShop plugin, Player player) {

		Map<Integer, Button> buttons = new HashMap<Integer, Button>();
		int slot = -1;

		for (ItemStack itemStack : event.getInventory().getContents()) {
			slot++;
			if (itemStack != null) {

				ShopItem item = searchItem(itemStack);

				if (item != null) {

					if (slot == item.getSlot())
						continue;

					if (category.getType().equals(ShopType.ITEM_SLOT)) {

						List<ShopItemConsomable> itemConsomables = plugin.getItems().shorItems(items,
								category.getInventorySize(), maxPage, getPage());

						if (this.contains(item, itemConsomables)) {
							int newSlot = slot + ((getPage() - 1) * category.getInventorySize());

							item.setSlot(newSlot < 0 ? 0 : newSlot);
						}

					}

				} else
					buttons.put(slot, new Button(itemStack));
			}
		}
		object.setDecorations(buttons, category.getId());

		plugin.getItems().save("items");
		plugin.getInventory().save();
	}

	public ShopItem searchItem(ItemStack itemStack) {
		return items.stream().filter(item -> item.getDisplayItem().isSimilar(itemStack)).findAny().orElse(null);
	}

	public boolean contains(ShopItem item, List<ShopItemConsomable> items) {
		return items.stream().filter(i -> i.equals(item)).findAny().isPresent();
	}

	@Override
	protected void onDrag(InventoryDragEvent event, ZShop plugin, Player player) {

	}

	@Override
	public VInventory clone() {
		return new InventoryShopConfig();
	}

}
