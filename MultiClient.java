import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author LXY
 * @email 403824215@qq.com
 * @date 2018/7/20 15:55
 */
//客户端

//客户端读取服务器端信息的线程
class ClientReadServer implements Runnable
{
    private Socket socket;

    public ClientReadServer(Socket socket)
    {
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNext())
            {
                System.out.println(scanner.next());
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//客户端向服务端发送信息的线程
class ClientSendServer implements Runnable
{
    private Socket socket;

    public ClientSendServer(Socket socket)
    {
        this.socket = socket;
    }

    public void run() {
        try {
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while (true)
            {
                String msg = null;
                if(scanner.hasNext())
                {
                    msg = scanner.next();
                    printStream.println(msg);
                }
                if(msg.equals("bye"))
                {
                    scanner.close();
                    printStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

public class MultiClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",6666);
        Thread read = new Thread(new ClientReadServer(socket));
        Thread send = new Thread(new ClientSendServer(socket));
        read.start();
        send.start();
    }
}