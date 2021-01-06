package online;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;

import online.datamanager.DataManager;
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

        URL webRootLocation = this.getClass().getResource("/webroot/index.html");
        if (webRootLocation == null)
        {
            throw new IllegalStateException("Unable to determine webroot URL location");
        }

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$","/"));
        System.out.printf("Web Root URI: %s%n", webRootUri.getPath());

        DataManager.getInstance().loadData(webRootUri.getPath() + "sampledata/movies.csv", webRootUri.getPath() + "sampledata/links.csv", webRootUri.getPath() + "sampledata/ratings.csv", webRootUri.getPath() + "modeldata/item2vecEmb.csv", webRootUri.getPath() + "modeldata/userEmb.csv");

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

        //设置url handler
        server.setHandler(context);
        System.out.print("RecSys Server has started.");
        
        //启动Jetty服务器
        server.start();
        server.join();
    }
}
