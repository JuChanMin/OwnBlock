package iKguana.ownblock;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;

public class MCAReplica {
	public static HashMap<String, HashMap<String, Config>> configs = new HashMap<>();
	public static HashMap<Config, Long> tc = new HashMap<>();

	public MCAReplica() {
		new AutoSaveTimer();
	}

	public static String getFileName(Vector3 vec) {
		int x = (int) Math.floor(vec.getFloorX() / 32);
		int z = (int) Math.floor(vec.getFloorZ() / 32);

		return "i" + x + "." + z + ".ob";
	}

	public static Config getFile(Position pos) {
		File path = new File(Server.getInstance().getDataPath() + "worlds" + File.separator + pos.getLevel().getFolderName() + File.separator + "obs");
		path.mkdirs();

		if (!configs.containsKey(pos.getLevel().getName()))
			configs.put(pos.getLevel().getName(), new HashMap<>());
		if (!configs.get(pos.getLevel().getName()).containsKey(getFileName(pos)))
			configs.get(pos.getLevel().getName()).put(getFileName(pos), new Config(path + File.separator + getFileName(pos), Config.YAML));

		return configs.get(pos.getLevel().getName()).get(getFileName(pos));
	}

	public static String get(Position pos) {
		Config cfg = getFile(pos);
		resetT(cfg);
		return cfg.getString(pos.getFloorX() + "." + pos.getFloorY() + "." + pos.getFloorZ(), "#BLANK");
	}

	public static void set(Position pos, String name) {
		Config cfg = getFile(pos);
		cfg.set(pos.getFloorX() + "." + pos.getFloorY() + "." + pos.getFloorZ(), name.toLowerCase());
		resetT(cfg);
	}

	public static void remove(Position pos) {
		Config cfg = getFile(pos);
		cfg.set(pos.getFloorX() + "." + pos.getFloorY() + "." + pos.getFloorZ(), "#BLANK");
		resetT(cfg);
	}

	public static void resetT(Config cfg) {
		if (tc.containsKey(cfg))
			tc.replace(cfg, (new Date()).getTime());
		else
			tc.put(cfg, (new Date()).getTime());
	}

	public static boolean isOld(Config cfg) {
		if (tc.containsKey(cfg))
			if ((new Date()).getTime() > tc.get(cfg) + 10000 * 60 * 5)
				return true;
		return false;
	}

	public static void saveAll(boolean force) {
		for (String lvl : configs.keySet())
			for (String key : configs.get(lvl).keySet()) {
				Config cfg = configs.get(lvl).get(key);
				if (force || isOld(cfg)) {
					cfg.save();
				}
			}
		removeOlds();
	}

	public static void removeOlds() {
		for (String lvl : configs.keySet()) {
			HashMap<String, Config> newMap = new HashMap<>();
			for (String key : configs.get(lvl).keySet()) {
				Config cfg = configs.get(lvl).get(key);
				if (!isOld(cfg))
					newMap.put(key, cfg);
				else if (tc.containsKey(cfg))
					tc.remove(cfg);
			}
			configs.replace(lvl, newMap);
		}
	}

	class AutoSaveTimer {
		Timer timer = new Timer();

		public AutoSaveTimer() {
			timer.schedule(new ASTask(), 0, 50000);
		}

		class ASTask extends TimerTask {
			public void run() {
				saveAll(false);
			}
		}
	}

}
