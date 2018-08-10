import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LXY
 * @email 403824215@qq.com
 * @date 2018/7/20 16:12
 */
class Server implements Runnable
{
    private static Map<String,Socket> map = new ConcurrentHashMap<String, Socket>();

    private Socket socket;

    public Server(Socket socket)
    {
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            String msg = null;
            while (true)
            {
                if(scanner.hasNextLine())
                {
                    msg = scanner.nextLine();
                    Pattern pattern = Pattern.compile("\r");
                    Matcher matcher = pattern.matcher(msg);
                    msg = matcher.replaceAll("");
                    //用户注册——格式：userName:用户名
                    if(msg.startsWith("userName:"))
                    {
                        String userName = msg.split("\\:")[1];
                        userRegist(userName,socket);
                        continue;
                    }
                    //群聊——格式：G:群聊信息
                    else if(msg.startsWith("G:"))
                    {
                        firstStep(socket);
                        String str = msg.split("\\:")[1];
                        groupChat(socket,str);
                        continue;
                    }
                    else if(msg.startsWith("P:") && msg.contains("-"))
                    {
                        firstStep(socket);
                        String userName = msg.split("\\:")[1].split("-")[0];
                        String str = msg.split("\\:")[1].split("-")[1];
                        privateChat(socket,userName,str);
                        continue;
                    }
                    else if(msg.contains("bye"))
                    {
                        firstStep(socket);
                        userExit(socket);
                        continue;
                    }
                    else
                    {
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        printStream.println("格式输入错误");
                        continue;

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void firstStep(Socket socket) throws IOException {
        Set<Map.Entry<String,Socket>> set = map.entrySet();
        for(Map.Entry<String,Socket> entry:set)
        {
            if(entry.getValue().equals(socket))
            {
                if(entry.getValue() == null)
                {
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    printStream.println("请先进行注册操作！格式为：[userName:用户名]");
                }
            }
        }
    }

    private void userRegist(String userName, Socket socket) {
        map.put(userName,socket);
        System.out.println("用户名：" + userName + "客户端" + socket +"上线了！！");
        System.out.println("当前在线人数为" + map.size() + "人");
    }

    private void groupChat(Socket socket, String msg) throws IOException {
        Set<Map.Entry<String,Socket>> set = map.entrySet();
        String userName = null;
        for(Map.Entry<String,Socket> entry:set)
        {
            if(entry.getValue().equals(socket))
            {
                userName = entry.getKey();
                break;
            }
        }
        for(Map.Entry<String,Socket> entry:set)
        {
            Socket client = entry.getValue();
            PrintStream printStream = new PrintStream(client.getOutputStream());
            printStream.println(userName + "说" + msg);
        }
    }

    private void privateChat(Socket socket, String userName, String msg) throws IOException {
        String curUser = null;
        Set<Map.Entry<String,Socket>> set = map.entrySet();
        for(Map.Entry<String,Socket> entry:set)
        {
            if(entry.getValue().equals(socket))
            {
                curUser = entry.getKey();
                break;
            }
        }
        Socket client = map.get(userName);
        PrintStream printStream = new PrintStream(client.getOutputStream());
        printStream.println(curUser + "私聊说" + msg);
    }

    private void userExit(Socket socket) {
        String userName = null;
        for(String key:map.keySet())
        {
            if(map.get(key).equals(socket))
            {
                userName = key;
                break;
            }
        }
        map.remove(userName,socket);
        System.out.println("用户" + userName + "已下线");
    }
}


public class MultiServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            //使用线程池
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            for(int i = 0;i < 20;i++)
            {
                System.out.println("欢迎来到聊天室。。。");
                Socket socket = serverSocket.accept();
                System.out.println("新人加入。。。");
                executorService.execute(new Server(socket));
            }
            executorService.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
