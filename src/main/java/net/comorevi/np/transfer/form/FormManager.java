package net.comorevi.np.transfer.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.utils.TextFormat;
import net.comorevi.np.transfer.TransferPlugin;
import net.comorevi.np.transfer.history.HistoryDataHandler;
import net.comorevi.np.transfer.network.ServerListLoader;
import net.comorevi.np.transfer.history.HistoryData;
import net.comorevi.np.transfer.network.data.OnlineServerEntry;
import ru.nukkitx.forms.elements.CustomForm;
import ru.nukkitx.forms.elements.ImageType;
import ru.nukkitx.forms.elements.ModalForm;
import ru.nukkitx.forms.elements.SimpleForm;

import java.util.LinkedList;

public class FormManager {
    private static final FormManager instance = new FormManager();

    public void sendTransferHomeWindow(Player player, String... homeMessage) {
        new SimpleForm()
                .setTitle("Transfer")
                .setContent(homeMessage.length == 0 ? "操作を選択してください。" : homeMessage[0])
                .addButton("サーバーリスト", ImageType.PATH, "textures/ui/World")
                .addButton("手動入力", ImageType.PATH, "textures/ui/book_addtextpage_default")
                .addButton("移動履歴", ImageType.PATH, "textures/ui/copy")
                .addButton("履歴削除", ImageType.PATH, "textures/ui/icon_trash")
                .addButton("Transferについて", ImageType.PATH, "textures/ui/Feedback")
                .send(player, (target, form, data) -> {
                    if (data == -1) {
                        target.sendMessage("[Transfer] Transferを終了しました。");
                        return;
                    }

                    if (form.getResponse().getClickedButtonId() == 0) {
                        sendTransferListWindow(target);
                    } else if (form.getResponse().getClickedButtonId() == 1) {
                        sendTransferManualWindow(target);
                    } else if (form.getResponse().getClickedButtonId() == 2) {
                        sendSeeTransferHistoryWindow(target);
                    } else if (form.getResponse().getClickedButtonId() == 3) {
                        sendDeleteTransferHistoryWindow(target);
                    } else {
                        sendAboutTransferWindow(player);
                    }
                });
    }

    public void sendTransferListWindow(Player player) {
        player.sendMessage("[Transfer] データを取得しています...");
        LinkedList<OnlineServerEntry.Server> serverList;
        try {
            serverList = ServerListLoader.getInstance().getOnlineServers();
        } catch (Exception e) {
            sendTransferHomeWindow(player, TextFormat.RED + "サーバーリストへの接続に失敗しました。");
            return;
        }

        SimpleForm simpleForm = new SimpleForm()
                .setTitle(TextFormat.GREEN + "MCServers" + TextFormat.RESET + ".JP")
                .setContent("移動先のサーバーを選択");

        serverList.forEach(server -> {
            if (server.getIs_display_address() == 1 && server.getIs_verified() == 1)
                simpleForm.addButton(server.getId() + ": " + server.getName(), ImageType.PATH, "textures/ui/World");
        });

        LinkedList<OnlineServerEntry.Server> copyServerList = serverList;
        simpleForm.send(player, (target, form, data) -> {
            if (data == -1) {
                sendTransferHomeWindow(target);
                return;
            }

            copyServerList.forEach(server -> {
                if (server.getId() == Integer.parseInt(form.getResponse().getClickedButton().getText().split(":")[0])) {
                    sendTransferConfirmWindow(target, server);
                }
            });
        });
    }

    public void sendTransferManualWindow(Player player) {
        new CustomForm()
                .setTitle("Transfer")
                .addLabel("移動先のサーバーを選択")
                .addInput("Address", "IPアドレスを入力...")
                .addInput("Port", "ポート番号を入力...", "19132")
                .send(player, (target, form, data) -> {
                    if (data == null) {
                        sendTransferHomeWindow(target);
                        return;
                    }

                    if (checkContent(form.getResponse())) {
                        try {
                            TransferPlugin.getInstance().transfer(target, form.getResponse().getInputResponse(1), Integer.parseInt(form.getResponse().getInputResponse(2)));
                            HistoryDataHandler.getInstance().addHistoryData(target, form.getResponse().getInputResponse(1), Integer.parseInt(form.getResponse().getInputResponse(2)));
                        } catch (NumberFormatException e) {
                            target.sendMessage("[Transfer] ポート番号は数値を入力してください。");
                        }
                    } else {
                        target.sendMessage("[Transfer] 未入力の項目があります。");
                    }
                });
    }

    public void sendSeeTransferHistoryWindow(Player player) {
        SimpleForm simpleForm = new SimpleForm()
                .setTitle("Transfer")
                .setContent("移動先のサーバーを選択");
        if (HistoryDataHandler.getInstance().getHistoryDataMap(player) != null) {
            HistoryDataHandler.getInstance().getHistoryDataMap(player).values().forEach((historyData) -> {
                simpleForm.addButton(historyData.getName() + "\n" + historyData.getAddress() + ":" + historyData.getPort());
            });
        } else {
            sendTransferHomeWindow(player, TextFormat.AQUA + "履歴データがありません。");
            return;
        }

        simpleForm.send(player, (target, form, data) -> {
            if (data == -1) {
                sendTransferHomeWindow(target);
                return;
            }
            sendTransferConfirmWindow(target, HistoryDataHandler.getInstance().getHistoryDataMap(target).get(form.getResponse().getClickedButton().getText().split("\n")[1].replace(".", "-").replace(":", "-")));
        });
    }

    public void sendDeleteTransferHistoryWindow(Player player) {
        new ModalForm()
                .setTitle("Transfer")
                .setContent("これまでの移動履歴を削除しますか？")
                .setButton1("削除する")
                .setButton2("やめる")
                .send(player, (target, form, data) -> {
                    if (form.getResponse().getClickedButtonId() == 0) {
                        HistoryDataHandler.getInstance().removePlayerData(target);
                        sendTransferHomeWindow(target, TextFormat.YELLOW + "移動履歴を削除しました。");
                    } else {
                        sendTransferHomeWindow(target);
                    }
                });
    }

    public void sendAboutTransferWindow(Player player) {
        new CustomForm()
                .setTitle("About Transfer")
                .addLabel("=== 作者 ===\n" +
                        "こもれび(Twitter: @popkechupki)")
                .addLabel("=== サーバーリストについて ===\n" +
                        "このプラグインのサーバーリストのデータは「mcservers.jp」から提供されるデータを利用しています。\n" +
                        "サーバーリストに表示されるサーバーはIPアドレスを公開していてかつ現在オンラインのもののみです。")
                .send(player, (target, form, data) -> {
                    sendTransferHomeWindow(target);
                });
    }

    public void sendTransferConfirmWindow(Player player, OnlineServerEntry.Server server) {
        String categoryLabel = "";
        try {
            if (!server.getCategories().equals("[]")) {
                categoryLabel = ServerListLoader.getInstance().getCategoryLabels(server);
            }
        } catch (Exception e) {
            sendTransferHomeWindow(player, TextFormat.RED + "サーバーリストへの接続に失敗しました。");
            return;
        }

        new ModalForm()
                .setTitle(TextFormat.GREEN + "MCServers" + TextFormat.RESET + ".JP")
                .setContent("=== 移動先のサーバー ===\n\n名前: " + server.getName() + "\nアドレス: " + server.getAddress() + "\nポート: " + server.getPort() + "\nカテゴリ: " + categoryLabel + "\n\n=== 説明 ===\n" + server.getDescription())
                .setButton1("移動する")
                .setButton2("やめる")
                .send(player, (target, form, data) -> {
                    if (form.getResponse().getClickedButtonId() == 0) {
                        LinkedList<String> serverAddressList = new LinkedList<>();
                        try {
                            ServerListLoader.getInstance().getOnlineServers().forEach(s -> {
                                serverAddressList.add(s.getAddress());
                            });
                        } catch (Exception e) {
                            sendTransferHomeWindow(player, TextFormat.RED + "サーバーリストへの接続に失敗しました。");
                            return;
                        }
                        if (serverAddressList.contains(server.getAddress())) {
                            TransferPlugin.getInstance().transfer(target, server.getAddress(), server.getPort());
                            HistoryDataHandler.getInstance().addHistoryData(target, server.getAddress(), server.getPort(), server.getName());
                        } else {
                            sendTransferHomeWindow(player, TextFormat.RED + "サーバーがオフラインです。");
                        }
                    } else {
                        sendTransferListWindow(target);
                    }
                });
    }

    public void sendTransferConfirmWindow(Player player, HistoryData historyData) {
        new ModalForm()
                .setTitle("Transfer")
                .setContent("=== 移動先のサーバー ===\n名前: " + historyData.getName() + "\nアドレス: " + historyData.getAddress() + "\nポート: " + historyData.getPort())
                .setButton1("移動する")
                .setButton2("やめる")
                .send(player, (target, form, data) -> {
                    if (form.getResponse().getClickedButtonId() == 0) {
                        TransferPlugin.getInstance().transfer(target, historyData.getAddress(), historyData.getPort());
                        HistoryDataHandler.getInstance().addHistoryData(target, historyData.getAddress(), historyData.getPort(), historyData.getName());
                    } else {
                        sendSeeTransferHistoryWindow(target);
                    }
                });
    }

    private boolean checkContent(FormResponseCustom response) {
        return !response.getInputResponse(1).equals("") && !response.getInputResponse(2).equals("");
    }

    public static FormManager getInstance() {
        return instance;
    }
}
