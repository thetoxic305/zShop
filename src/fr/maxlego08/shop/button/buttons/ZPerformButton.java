package fr.maxlego08.shop.button.buttons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.shop.api.button.Button;
import fr.maxlego08.shop.api.button.buttons.PerformButton;
import fr.maxlego08.shop.api.enums.ButtonType;
import fr.maxlego08.shop.api.enums.PlaceholderAction;
import fr.maxlego08.shop.api.sound.SoundOption;

public class ZPerformButton extends ZPlaceholderButton implements PerformButton {

	private final List<String> commands;

	private final List<String> consoleCommands;
	private final List<String> consoleRightCommands;
	private final List<String> consoleLeftCommands;

	private final List<String> consolePermissionCommands;
	private final String consolePermission;

	/**
	 * @param type
	 * @param itemStack
	 * @param slot
	 * @param permission
	 * @param message
	 * @param elseButton
	 * @param isPermanent
	 * @param action
	 * @param placeholder
	 * @param value
	 * @param commands
	 * @param closeInventory
	 */
	public ZPerformButton(ButtonType type, ItemStack itemStack, int slot, String permission, String message,
			Button elseButton, boolean isPermanent, PlaceholderAction action, String placeholder, String value,
			List<String> commands, List<String> consoleCommands, List<String> consoleRightCommands,
			List<String> consoleLeftCommands, boolean glow, SoundOption sound, List<String> consolePermissionCommands,
			String consolePermission, boolean isClose) {
		super(type, itemStack, slot, permission, message, elseButton, isPermanent, action, placeholder, value, glow,
				sound, isClose);
		this.commands = commands;
		this.consoleCommands = consoleCommands;
		this.consolePermissionCommands = consolePermissionCommands;
		this.consolePermission = consolePermission;
		this.consoleLeftCommands = consoleLeftCommands;
		this.consoleRightCommands = consoleRightCommands;
	}

	@Override
	public List<String> getCommands() {
		return commands;
	}

	@Override
	public void execute(Player player, ClickType type) {

		if (!checkPermission(player)) {
			return;
		}

		if (super.closeInventory()) {
			player.closeInventory();
		}

		if (type.equals(ClickType.RIGHT)) {
			papi(new ArrayList<String>(this.consoleRightCommands), player).forEach(command -> Bukkit
					.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));	
		}
		
		if (type.equals(ClickType.LEFT)) {
			papi(new ArrayList<String>(this.consoleLeftCommands), player).forEach(command -> Bukkit
					.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));	
		}
		
		papi(new ArrayList<String>(this.commands), player)
				.forEach(command -> player.performCommand(command.replace("%player%", player.getName())));

		papi(new ArrayList<String>(this.consoleCommands), player).forEach(command -> Bukkit
				.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));

		if (this.consolePermission != null && player.hasPermission(this.consolePermission)) {
			papi(new ArrayList<String>(consolePermissionCommands), player).forEach(command -> Bukkit
					.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));
		}
	}

	@Override
	@Deprecated
	public boolean closeInventory() {
		return super.closeInventory();
	}

	@Override
	public List<String> getConsoleCommands() {
		return consoleCommands;
	}

	@Override
	public List<String> getConsolePermissionCommands() {
		return consolePermissionCommands;
	}

	@Override
	public String getConsolePermission() {
		return consolePermission;
	}

}