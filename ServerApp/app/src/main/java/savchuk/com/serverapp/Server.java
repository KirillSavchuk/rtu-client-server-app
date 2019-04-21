package savchuk.com.serverapp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
    private MainActivity activity;
    private ServerSocket serverSocket;
    String message = "";
    private static final int socketServerPORT = 8080;

    public Server(MainActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    message += "#" + count++ + " no "
                            + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n";

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.message.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        int count;

        SocketServerReplyThread(Socket socket, int _count) {
            hostThreadSocket = socket;
            count = _count;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String messageReply = "Sveiks no Servera! Tu esi #" + count;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(messageReply);
                printStream.close();

                message += "Serveris atbildēja: " + messageReply + "\n";

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.message.setText(message);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                message += "Kļūda! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.message.setText(message);
                }
            });
        }
    }

    public String getIpAddress() {
        String ipAdress = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress())
                        ipAdress += "Serveris stārdā no: " + inetAddress.getHostAddress();
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ipAdress += "Kļūda! " + e.toString() + "\n";
        }
        return ipAdress;
    }
}
