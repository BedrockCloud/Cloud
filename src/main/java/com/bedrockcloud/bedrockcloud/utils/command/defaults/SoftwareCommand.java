package com.bedrockcloud.bedrockcloud.utils.command.defaults;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.utils.console.Loggable;
import com.bedrockcloud.bedrockcloud.utils.command.Command;

import java.util.concurrent.CompletableFuture;

public class SoftwareCommand extends Command implements Loggable {

    public SoftwareCommand() {
        super("software", "software <all|pocketmine|waterdogpe>", "Update Proxy/Server softwares");
    }

    @Override
    public void onCommand(final String[] args) {
        if (args.length == 0) {
            Cloud.getLogger().info("Usage: software <all|pocketmine|waterdogpe>");
            return;
        }

        String option = args[0];

        switch (option) {
            case "all" -> updateAllSoftwares();
            case "pocketmine" -> updatePocketMine();
            case "waterdogpe" -> updateWaterdogPE();
            default -> Cloud.getLogger().info("Invalid option. Usage: software <all|pocketmine|waterdogpe>");
        }
    }

    private void updateAllSoftwares() {
        CompletableFuture<Boolean> pocketMineDownload = SoftwareUtils.downloadAsync(SoftwareManager.getInstance().getSoftware("POCKETMINE").getDownloadLink(), "./local/versions/pocketmine/PocketMine-MP.phar");
        CompletableFuture<Boolean> waterdogPEDownload = SoftwareUtils.downloadAsync(SoftwareManager.getInstance().getSoftware("WATERDOGPE").getDownloadLink(), "./local/versions/waterdogpe/WaterdogPE.jar");

        CompletableFuture<Void> allDownloads = CompletableFuture.allOf(pocketMineDownload, waterdogPEDownload);
        allDownloads.whenComplete((result, error) -> {
            if (error == null) {
                Cloud.getLogger().info("All softwares updated successfully!");
            } else {
                Cloud.getLogger().error("Failed to update one or more softwares: " + error.getMessage());
            }
        });
    }

    private void updatePocketMine() {
        SoftwareUtils.downloadAsync(SoftwareManager.getInstance().getSoftware("POCKETMINE").getDownloadLink(), "./local/versions/pocketmine/PocketMine-MP.phar").whenComplete((success, error) -> {
            if (success) {
                Cloud.getLogger().info("PocketMine updated successfully!");
            } else {
                Cloud.getLogger().error("Failed to update PocketMine: " + error.getMessage());
            }
        });
    }

    private void updateWaterdogPE() {
        SoftwareUtils.downloadAsync(SoftwareManager.getInstance().getSoftware("WATERDOGPE").getDownloadLink(), "./local/versions/waterdogpe/WaterdogPE.jar").whenComplete((success, error) -> {
            if (success) {
                Cloud.getLogger().info("WaterdogPE updated successfully!");
            } else {
                Cloud.getLogger().error("Failed to update WaterdogPE: " + error.getMessage());
            }
        });
    }
}