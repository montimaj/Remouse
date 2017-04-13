package project.android.net;

import java.net.Socket;

/**
 * Encapsulation of the data received from the client at the time of
 * establishing TCP connection.
 *
 * <p>
 *     The data contains :
 *     <ul>
 *         <li>Public key of the client</li>
 *         <li>Canonical hostname of the client</li>
 *         <li>Client <code>Socket</code></li>
 *     </ul>
 * </p>
 *
 * @see java.net.Socket
 * @see project.android.net.Client
 */

public class ClientInfo {
    private String mClientInfo;
    private byte[] mPublicKey;
    private Socket mClientSocket;

    /**
     * Constructor.
     *
     * <p>
     *     Initializes this <code>ClientInfo</code> object. The stored
     *     object of {@link java.net.Socket} (client <code>Socket</code>)
     *     is not initialized. It can be set by the {@link #setSocket(Socket)}
     *     method.
     * </p>
     *
     * @param clientInfo canonical hostname of the client.
     * @param publicKey public key of the client.
     * @see java.net.Socket
     * @see #setSocket(Socket)
     */
    public ClientInfo(String clientInfo, byte[] publicKey) {
        mClientInfo = clientInfo;
        mPublicKey = publicKey;
    }

    /**
     * Adds {@link java.net.Socket} of the client.
     *
     * @param socket the client {@link java.net.Socket} object to be
     *               stored.
     */
    public void setSocket(Socket socket) { mClientSocket = socket; }

    /**
     * Returns the client <code>Socket</code>.
     *
     * @return the client {@link java.net.Socket}.
     */
    public Socket getSocket() { return mClientSocket; }

    /**
     * Returns the canonical hostname of the client.
     *
     * @return canonical hostname of the client
     */
    public String getClientInfo() { return mClientInfo; }

    /**
     * Returns the public key of the client.
     *
     * @return public key of the client.
     */
    public byte[] getPublicKey() { return mPublicKey; }
}
