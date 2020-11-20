package online;

import java.net.InetSocketAddress;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class RecSysServer {
    private static final int DEFAULT_PORT = 6010;

    public RecSysServer() {
    }

    public static void main(String[] args) throws Exception {
        (new RecSysServer()).run();
    }

    public void run() throws Exception {
        int port = 6010;

        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException var7) {
        }
        //根据主机名和端口号创建套接字地址
        InetSocketAddress inetAddress = new InetSocketAddress("0.0.0.0", port);
        //创建Jetty服务器
        Server server = new Server(inetAddress);
        //创建Jetty服务器的环境handler
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setWelcomeFiles(new String[] { "index.html" });
        context.getMimeTypes().addMimeMapping("txt", "text/plain;charset=utf-8");
        context.addServlet(DefaultServlet.class, "/");
        context.addServlet(new ServletHolder(new MovieService()), "/getmovie");
        //设置Jetty的环境handler
        server.setHandler(context);
        System.out.print("RecSys Server has started.");
        //启动Jetty服务器
        server.start();
        server.join();
    }
}
