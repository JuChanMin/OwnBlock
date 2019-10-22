package iKguana.ownblock;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

public class OwnBlock extends PluginBase {

	public void onEnable() {
		new MCAReplica();
		getServer().getPluginManager().registerEvents(new OBListener(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getLabel().equals("ob")) {
			if (!OBListener.observers.contains(sender.getName())) {
				OBListener.observers.add(sender.getName());
				sender.sendMessage("on");
			} else {
				OBListener.observers.remove(sender.getName());
				sender.sendMessage("off");
			}
			return true;
		}
		return false;
	}

	public void onDisable() {
		MCAReplica.saveAll(true);
	}
}
