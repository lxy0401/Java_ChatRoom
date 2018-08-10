import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author LXY
 * @email 403824215@qq.com
 * @date 2018/7/20 15:34
 */
//服务端
public class SingleServer {
    public static void main(String[] args) throws IOException {
        //创建服务器端的ServerSocket对象，等待客户端进行连接
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器的端口号为6666，等待客户端连接。。。");
        //侦听并接收服务器端的连接，返回套接字Socket对象
        Socket socket = serverSocket.accept();
        //获取客户端的输入流，读取客户端的输入内容
        Scanner scanner = new Scanner(socket.getInputStream());
        scanner.useDelimiter("\n");
        if(scanner.hasNext())
        {
            System.out.println("客户端发来消息：" + scanner.next());
        }
        //获取客户端的输出流，向客户端输出内容
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println("客户端你好，我是服务器端：" + serverSocket.getLocalPort());
        //关闭流
        serverSocket.close();
    }
}
