package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import org.json.simple.JSONObject;

import java.util.Objects;

public class PlayerMovePacket extends DataPacket
{
    public String playerName;
    public String toServer;
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String playerNames = jsonObject.get("playerNames").toString();
        final String toServer = jsonObject.get("toServer").toString();

        if (playerNames.equalsIgnoreCase("ALL")) {
            for (CloudPlayer cloudPlayer : Cloud.getCloudPlayerProvider().getCloudPlayerMap().values()) {
                if (Cloud.getCloudPlayerProvider().existsPlayer(cloudPlayer.getPlayerName())) {
                    if (Cloud.getCloudServerProvider().existServer(cloudPlayer.getCurrentProxy())) {
                        final CloudServer server = Cloud.getCloudServerProvider().getServer(toServer);
                        if (server != null) {
                            if (server.getPlayerCount() < server.getTemplate().getMaxPlayers()) {
                                if (server.getState() == 1) {
                                    final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                                    playerTextPacket.playerName = playerName;
                                    Objects.requireNonNull(playerTextPacket);
                                    playerTextPacket.type = 0;
                                    playerTextPacket.value = Cloud.prefix + "§cThe Server is ingame!";
                                    Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                                } else {
                                    final CloudServer cloudServer = Cloud.getCloudServerProvider().getServer(cloudPlayer.getCurrentProxy());
                                    final PlayerMovePacket playerMovePacket = new PlayerMovePacket();
                                    playerMovePacket.playerName = playerName;
                                    playerMovePacket.toServer = toServer;
                                    cloudServer.pushPacket(playerMovePacket);
                                }
                            } else {
                                final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                                playerTextPacket.playerName = playerName;
                                Objects.requireNonNull(playerTextPacket);
                                playerTextPacket.type = 0;
                                playerTextPacket.value = Cloud.prefix + "§cThe Server is full!";
                                Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                            }
                        } else {
                            final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                            playerTextPacket.playerName = playerName;
                            Objects.requireNonNull(playerTextPacket);
                            playerTextPacket.type = 0;
                            playerTextPacket.value = Cloud.prefix + "§4The Server is not registered!";
                            Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                        }
                    }
                }
            }
            return;
        }

        String[] playerArray = playerNames.split(":");

        for (String playerName : playerArray) {
            if (Cloud.getCloudPlayerProvider().existsPlayer(playerName)) {
                final CloudPlayer cloudPlayer = Cloud.getCloudPlayerProvider().getCloudPlayer(playerName);
                if (Cloud.getCloudServerProvider().existServer(cloudPlayer.getCurrentProxy())) {
                    final CloudServer server = Cloud.getCloudServerProvider().getServer(toServer);
                    if (server != null) {
                        if (server.getPlayerCount() < server.getTemplate().getMaxPlayers()) {
                            if (server.getState() == 1) {
                                final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                                playerTextPacket.playerName = playerName;
                                Objects.requireNonNull(playerTextPacket);
                                playerTextPacket.type = 0;
                                playerTextPacket.value = Cloud.prefix + "§cThe Server is ingame!";
                                Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                            } else {
                                final CloudServer cloudServer = Cloud.getCloudServerProvider().getServer(cloudPlayer.getCurrentProxy());
                                final PlayerMovePacket playerMovePacket = new PlayerMovePacket();
                                playerMovePacket.playerName = playerName;
                                playerMovePacket.toServer = toServer;
                                cloudServer.pushPacket(playerMovePacket);
                            }
                        } else {
                            final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                            playerTextPacket.playerName = playerName;
                            Objects.requireNonNull(playerTextPacket);
                            playerTextPacket.type = 0;
                            playerTextPacket.value = Cloud.prefix + "§cThe Server is full!";
                            Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                        }
                    } else {
                        final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                        playerTextPacket.playerName = playerName;
                        Objects.requireNonNull(playerTextPacket);
                        playerTextPacket.type = 0;
                        playerTextPacket.value = Cloud.prefix + "§4The Server is not registered!";
                        Cloud.getCloudPlayerProvider().getCloudPlayer(playerName).getProxy().pushPacket(playerTextPacket);
                    }
                }
            }
        }
        super.handle(jsonObject, clientRequest);
    }
    
    @Override
    public String encode() {
        this.addValue("playerName", this.playerName);
        this.addValue("toServer", this.toServer);
        return super.encode();
    }
}
