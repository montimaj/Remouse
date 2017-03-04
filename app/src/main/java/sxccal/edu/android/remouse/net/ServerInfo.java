package sxccal.edu.android.remouse.net;

/**
 * @author Sayantan Majumdar
 */

public class ServerInfo {
    private byte[] mServerPubKey;
    private String mServerInfo;
    private String mAddress;
    private boolean mStopFlag;
    private boolean mIsSelected;

    static final int SERVER_INFO_LENGTH = 600;

    public ServerInfo(byte[] publicKey, String serverInfo) {
        mServerPubKey = publicKey;
        mServerInfo = serverInfo;
        mStopFlag = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerInfo other = (ServerInfo) obj;
        return mAddress.equals(other.mAddress);
    }

    void setServerAddress(String address) { mAddress = address; }
    public void setSelected( boolean value) { mIsSelected = value; }
    void setStopFlag() { mStopFlag = true; }
    void clearStopFlag() { mStopFlag = false; }

    public boolean getStopFlag() { return mStopFlag; }
    public boolean isSelected() { return mIsSelected; }
    public byte[] getServerPubKey() { return mServerPubKey; }
    public String getServerInfo() { return mServerInfo; }
    public String getAddress() { return mAddress; }
}