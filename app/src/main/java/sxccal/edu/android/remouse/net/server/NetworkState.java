package sxccal.edu.android.remouse.net.server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */

class NetworkState {

    private static HashMap<Socket, ServerThread> sConnectionMap = new HashMap<>();
    private static HashMap<InetAddress, Socket> sAddressMap = new HashMap<>();

    //public NetworkState() { mListModel = new DefaultListModel<>(); }

    HashMap<Socket, ServerThread> getConnectionMap() { return sConnectionMap; }

    //ServerThread getServerThread(InetAddress ia) { return sConnectionMap.get(sAddressMap.get(ia)); }

    public void add(Socket skt, ServerThread serverThread) {
        sConnectionMap.put(skt, serverThread);
        sAddressMap.put(skt.getInetAddress(), skt);
        /*if(!mListModel.contains(skt.getInetAddress().getHostAddress()))
            mListModel.addElement(skt.getInetAddress().getHostAddress());*/
    }

    void remove(Socket skt) {
        if(sConnectionMap.containsKey(skt)) sConnectionMap.remove(skt);
        if(sAddressMap.containsKey(skt.getInetAddress()))   sConnectionMap.remove(skt.getInetAddress());
        /*if(mListModel.contains(skt.getInetAddress().getHostAddress()))  mListModel.removeElement(skt.
                getInetAddress().getHostAddress());*/
    }
}