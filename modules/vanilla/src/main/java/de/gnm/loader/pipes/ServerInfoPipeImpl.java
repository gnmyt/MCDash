package de.gnm.loader.pipes;

import de.gnm.mcdash.api.helper.PropertyHelper;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;

import java.util.Objects;

public class ServerInfoPipeImpl implements ServerInfoPipe {

    private final String serverVersion;

    public ServerInfoPipeImpl(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    @Override
    public String getServerSoftware() {
        return "vanilla";
    }

    @Override
    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public int getServerPort() {
        return Integer.parseInt(Objects.requireNonNull(PropertyHelper.getProperty("server-port")));
    }
}
