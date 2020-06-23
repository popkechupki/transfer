package net.comorevi.np.transfer.util;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.comorevi.np.transfer.TransferPlugin;
import net.comorevi.np.transfer.util.data.HistoryData;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryDataProvider {
    private static final HistoryDataProvider instance = new HistoryDataProvider();

    public void addHistoryData(Player player, String address, int port, String... name) {
        Config config = new Config(new File(TransferPlugin.getInstance().getDataFolder(), "config.yml"), Config.YAML);
        if (config.exists(player.getName())) {
            LinkedHashMap<String, Map<String, Object>> saveDataQueue = new LinkedHashMap<>();
            saveDataQueue.put(address.replace(".", "-") + "-" + port, Map.of("name", name.length == 0 ? address : name[0], "address", address, "port", port));
            AtomicInteger atomicInteger = new AtomicInteger(0);
            Objects.requireNonNull(getHistoryDataMap(player)).forEach((key, historyData) -> {
                if (atomicInteger.addAndGet(1) > 5) return;
                if (!key.equals(address.replace(".", "-") + "-" + port)) {
                    saveDataQueue.put(key, Map.of("name", historyData.getName(), "address", historyData.getAddress(), "port", historyData.getPort()));
                }
            });
            config.set(player.getName(), saveDataQueue);
        } else {
            config.set(player.getName(), Map.of(address.replace(".", "-") + "-" + port, Map.of("name", name.length == 0 ? address : name[0], "address", address, "port", port)));
        }
        config.save();
    }

    public void removePlayerData(Player player) {
        Config config = new Config(new File(TransferPlugin.getInstance().getDataFolder(), "config.yml"), Config.YAML);
        if (!config.exists(player.getName())) return;

        config.remove(player.getName());
        config.save();
    }

    public LinkedHashMap<String, HistoryData> getHistoryDataMap(Player player) {
        Config config = new Config(new File(TransferPlugin.getInstance().getDataFolder(), "config.yml"), Config.YAML);
        if (config.exists(player.getName())) {
            LinkedHashMap<String, HistoryData> result = new LinkedHashMap<>();
            config.getSection(player.getName()).getAllMap().forEach((key, value) -> {
                String keyPath = player.getName() + "." + key;
                result.put(key, new HistoryData(config.getString(keyPath + ".name"), config.getString(keyPath + ".address"), config.getInt(keyPath + ".port")));
            });
            return result;
        }
        return null;
    }

    public static HistoryDataProvider getInstance() {
        return instance;
    }
}
