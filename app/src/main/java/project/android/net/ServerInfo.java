package project.android.net;

/**
 * Encapsulation of the data sent during UDP broadcast by the server.
 *
 * <p>
 *     This data is received by the {@link BroadcastReceiverThread}. This
 *     contains :
 *     <ul>
 *         <li>the public key of the server, </li>
 *         <li>status of the broadcast, and</li>
 *         <li>address of the server</li>
 *     </ul>
 * </p>
 * <p>
 *     This message helps a client to discover a server.
 * </p>
 *
 * @see project.android.net.BroadcastReceiverThread
 */
public class ServerInfo {
    private byte[] mServerPubKey;
    private String mServerInfo;
    private String mAddress;
    private String mPairingKey;
    private boolean mStopFlag;

    static final int SERVER_INFO_LENGTH = 600;

    /**
     * Constructor.
     * Initializes this <code>ServerInfo</code>.
     *
     * @param publicKey public key of the server.
     * @param serverInfo canonical hostname of the server.
     */
    public ServerInfo(byte[] publicKey, String serverInfo) {
        mServerPubKey = publicKey;
        mServerInfo = serverInfo;
        mStopFlag = false;
    }

    /**
     * Compares two <code>ServerInfo</code> objects.
     *
     * @param obj the object to be compared.
     * @return <code>true</code>, if the two objects are equal, <br/>
     *         <code>false</code>, otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        ServerInfo other = (ServerInfo) obj;
        return mAddress.equals(other.mAddress);
    }

    /**
     * Sets the IPv4 server address.
     * @param address IPv4 server address.
     */
    void setServerAddress(String address) { mAddress = address; }

    /**
     * Sets the pairing key used during client-server authentication.
     * @param pairingKey the pairing key.
     */
    public void setPairingKey(String pairingKey) { mPairingKey = pairingKey; }

    /**
     * Returns the status of the server.
     * @return server status.
     */
    boolean getStopFlag() { return mStopFlag; }

    /**
     * Returns the server public key.
     * @return server public key.
     */
    byte[] getServerPubKey() { return mServerPubKey; }

    /**
     * Returns the canonical host name of the server.
     * @return the canonical host name.
     */
    public String getServerInfo() { return mServerInfo; }

    /**
     * Returns the IPv4 address of the server.
     * @return the IPv4 server address.
     */
    public String getAddress() { return mAddress; }

    /**
     * Returns the pairing key used during client-server authentication.
     * @return the pairing key.
     */
    String getPairingKey() { return mPairingKey; }
}