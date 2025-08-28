package com.mukuru.webApi;

import io.javalin.*;

public class WebApiServer {
    private final Javalin server;

    public WebApiServer(){

        server = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });


//        server.get("/worlds", RobotWorldApiHandler::getCurrentWorld);
//        server.get("/worlds/{worldName}", RobotWorldApiHandler::getWorldByName);
//        server.post("/robot/{name}", RobotWorldApiHandler::getlaunchRobot);

    }

    public void start(int port) {
        server.start(port);
    }

    public void stop() {
        server.stop();
    }

    public static void main(String[] args){
        WebApiServer server = new WebApiServer();
        server.start(4000);
    }

}

