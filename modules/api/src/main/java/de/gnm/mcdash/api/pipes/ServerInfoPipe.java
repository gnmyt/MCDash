package de.gnm.mcdash.api.pipes;

public interface ServerInfoPipe extends BasePipe {

    /**
     * Gets the server software
     *
     * @return the server software
     */
    String getServerSoftware();

    /**
     * Gets the server version
     *
     * @return the server version
     */
    String getServerVersion();

    /**
     * Gets the server port
     * @return the server port
     */
    int getServerPort();

}
