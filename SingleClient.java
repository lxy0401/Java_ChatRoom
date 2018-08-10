import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author LXY
 * @email 403824215@qq.com
 * @date 2018/7/20 15:46
 */
//客户端
public class SingleClient {
    public static void main(String[] args) throws IOException {
        //客户端连接服务器，返回套接字Socket对象
        Socket socket = new Socket("127.0.0.1",6666);
        //获取服务端的输出流，向服务器端输出内容
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println("我是客户端" + socket.getLocalPort());
        //获取服务器端的输入流，读取服务器端的内容
        Scanner scanner = new Scanner(socket.getInputStream());
        scanner.useDelimiter("\n");
        if(scanner.hasNext())
        {
            System.out.println(scanner.next());
        }
        //关闭流
        socket.close();
    }
}
