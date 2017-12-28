package net.teamfruit.skcraft.launcher.model.modpack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectServerInfo {
    private String serverHost;
    private int serverPort = 25565;

    public boolean isValid() {
        return !Strings.isNullOrEmpty(serverHost) && serverPort > 0 && serverPort < 65535;
    }
}
