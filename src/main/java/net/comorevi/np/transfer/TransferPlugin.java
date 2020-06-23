package net.comorevi.np.transfer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.plugin.PluginBase;
import net.comorevi.np.transfer.form.FormHandler;

public class TransferPlugin extends PluginBase {
    private static TransferPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("transfer")) {
            if (!sender.isPlayer()) {
                getServer().getLogger().warning("ゲーム内からのみコマンドを実行できます。");
                return true;
            } else if (!sender.hasPermission("transfer.command.transfer")) {
                sender.sendMessage("[Transfer] コマンドの実行権限がありません。");
                return true;
            }

            FormHandler.getInstance().sendTransferHomeWindow((Player) sender);
        }
        return true;
    }

    public void transfer(Player player, String address, int port) {
        TransferPacket pk = new TransferPacket();
        pk.address = address;
        pk.port = port;
        player.dataPacket(pk);
        player.close("", address + ":" + port + "へ移動中", false);
    }

    public static TransferPlugin getInstance() {
        return instance;
    }
}
