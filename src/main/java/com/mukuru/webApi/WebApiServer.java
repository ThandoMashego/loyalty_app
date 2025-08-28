package com.mukuru.webApi;

import io.javalin.*;

public class WebApiServer {
    private final Javalin server;

    public WebApiServer(){

        server = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });

        server.post("/users", WebApiHandler::registerUser);
        server.post("/users/{userId}/send", WebApiHandler::sendMoney);
        server.get("/users/{userId}/points", WebApiHandler::getPoints);
        server.get("/users/{userId}/transactions", WebApiHandler::getTransactions);

        server.get("/rewards", WebApiHandler::getRewards);
        server.post("/rewards", WebApiHandler::addReward);
        server.post("/users/{userId}/rewards/{rewardId}/redeem", WebApiHandler::redeemReward);
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

