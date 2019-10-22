package iKguana.ownblock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public class OBListener implements Listener {
	public Item tool;

	public OBListener() {
		tool = Item.get(Item.WOODEN_SWORD);
		tool.setLore("막대를 클릭하여 블럭의 주인을 확인합니다.");
		tool.setCustomName("로그 확인툴");

		Item.addCreativeItem(tool);
	}

	public static ArrayList<String> observers = new ArrayList<>();

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent event) {
		if (!observers.contains(event.getPlayer().getName()))
			MCAReplica.set(event.getBlock(), event.getPlayer().getName());
	}

	// TODO REMOVE #1
	HashMap<String, Long> points = new HashMap<>();

	public boolean restrictCPS(PlayerInteractEvent event) {
		if (points.containsKey(event.getPlayer().getName())) {
			if (points.get(event.getPlayer().getName()) + 100 < (new Date()).getTime()) {
				points.replace(event.getPlayer().getName(), (new Date()).getTime());
			} else
				return true;
		} else
			points.put(event.getPlayer().getName(), (new Date()).getTime());
		return false;
	}
	// END

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.getPlayer().isOp())
			if (tool.equals(event.getItem())) {
				if (restrictCPS(event))// TODO REMOVE #1
					return;

				String owner = MCAReplica.get(event.getBlock());
				if (!owner.equalsIgnoreCase("#BLANK"))
					event.getPlayer().sendMessage(owner + "님의 블럭입니다.");
			}
	}

	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event) {
		String owner = MCAReplica.get(event.getBlock());
		if (!owner.equalsIgnoreCase("#BLANK"))
			if (event.getPlayer().isOp() || event.getPlayer().getName().equalsIgnoreCase(owner)) {
				MCAReplica.remove(event.getBlock());
			} else
				event.setCancelled();
	}
}
