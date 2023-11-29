package com.metax.modules.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

/**
 * 监控中心
 * 
 * @author ruoyi
 */
@EnableAdminServer
@SpringBootApplication
public class MetaxMonitorApplication
{
    public static final String BANNER= "" +
            "\n" +
            "\n" +
            "    ____    ____    __________    _________        __        ____  ____\n" +
            "   |_   \\  /   _|   |_   ___  |  |  _   _  |      /  \\      |_  _||_  _|\n" +
            "     |   \\/   |      | |_  \\_|   |_/ | | \\_|     / /\\ \\       \\ \\  / /\n" +
            "     | |\\  /| |      |  _|  _        | |        / ____ \\       > `' <\n" +
            "    _| |_\\/_| |_    _| |___/ |      _| |_     _/ /    \\ \\_   _/ /'`\\ \\_\n" +
            "   |_____||_____|   |_________|    |_____|   |____|  |____| |____||____|" +
            "\n" +
            "\n" +
            "";
    public static void main(String[] args)
    {
        SpringApplication.run(MetaxMonitorApplication.class, args);
        System.out.println(BANNER);
    }
}
