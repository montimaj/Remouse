package sxccal.edu.android.remouse.net.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * @author Sudipto Bhattacharjee
 * @author Sayantan Majumdar
 */
class Server {

    private boolean mStopFlag;
    private ServerSocket mServerSocket;
    private NetworkState mState;

    Server(NetworkState state, int port) throws IOException {
        this.mState = state;
        mServerSocket = new ServerSocket(port);
        mServerSocket.setSoTimeout(200);
        mStopFlag = false;
    }
    
    private void confirm(Socket socket, boolean value) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        if(value) {
            printWriter.println("1");
        } else  printWriter.println("0");
    }

    void setStopFlag() { mStopFlag = true; }

    void listen() throws IOException,InterruptedException {
        while(!mStopFlag) {
            try {
                Socket clientSocket = mServerSocket.accept();
                System.out.println(clientSocket.getInetAddress().getHostAddress() +
                        " wants to connect. Do you agree (0/1) ?");
                Scanner sc = new Scanner(System.in);
                int option = sc.nextInt();
                if (option == 1) {
                    confirm(clientSocket, true);
                    System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress());
                    ServerThread st = new ServerThread(mState, clientSocket);
                    Thread t = new Thread(st);
                    mState.add(clientSocket, st);
                    t.start();
                } else {
                    confirm(clientSocket, false);
                    clientSocket.close();
                }
            } catch (SocketTimeoutException | SocketException e) {}
        }
    }

    void close() throws IOException {
        mServerSocket.close();
    }
}