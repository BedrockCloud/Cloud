package com.bedrockcloud.bedrockcloud;

import com.bedrockcloud.bedrockcloud.software.Software;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoftwareUtils {
    private static final Executor executor = Executors.newFixedThreadPool(10);

    @ApiStatus.Internal
    public void registerSoftwares() {
        SoftwareManager.getInstance().registerSoftware(new Software("WATERDOGPE", "Proxy software WaterdogPE", SoftwareType.PROXY, "https://github.com/WaterdogPE/WaterdogPE/releases/latest/download/Waterdog.jar", new ArrayList<String>() {{
            add("https://github.com/BedrockCloud/CloudBridge-Proxy/releases/latest/download/CloudBridge.jar");
        }}, "../../local/versions/waterdogpe/", "./local/plugins/waterdogpe/", "Waterdog.jar"));
        SoftwareManager.getInstance().registerSoftware(new Software("NUKKIT", "Server software Nukkit", SoftwareType.SERVER, "https://ci.opencollab.dev/job/NukkitX/job/Nukkit/job/master/lastSuccessfulBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar", new ArrayList<String>() {{
            add("https://github.com/BedrockCloud/CloudBridge-NK/releases/latest/download/CloudBridge.jar");
        }}, "./local/versions/nukkit/", "./local/plugins/nukkit/", "nukkit-1.0-SNAPSHOT.jar"));
    }

    public static CompletableFuture<Boolean> downloadAsync(final String url, final String destinationDirectory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL downloadUrl = new URL(url);
                String fileName = new java.io.File(downloadUrl.getPath()).getName();
                Path destination = Path.of(destinationDirectory, fileName);
                InputStream inputStream = downloadUrl.openStream();

                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
                return true;
            } catch (IOException e) {
                Cloud.getLogger().error("Download failed: " + e.getMessage());
                return false;
            }
        }, executor);
    }
}