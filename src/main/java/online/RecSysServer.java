package online;

import java.net.InetSocketAddress;

import online.service.*;
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

        //创建servletcontexthandler
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setWelcomeFiles(new String[] { "index.html" });
        context.getMimeTypes().addMimeMapping("txt", "text/plain;charset=utf-8");

        //servlet主要功能在于交互式的浏览和修改数据，生成动态Web内容
        context.addServlet(DefaultServlet.class, "/");
        context.addServlet(new ServletHolder(new MovieService()), "/getmovie");
        context.addServlet(new ServletHolder(new UserService()), "/getuser");
        context.addServlet(new ServletHolder(new SimilarMovieService()), "/getsimilarmovie");
        context.addServlet(new ServletHolder(new ReconGenre()), "/getrecommendation");
        context.addServlet(new ServletHolder(new ReconUser()), "/getrecforyou");

        //设置url handler
        server.setHandler(context);
        System.out.print("RecSys Server has started.");
        
        //启动Jetty服务器
        server.start();
        server.join();
    }
}
