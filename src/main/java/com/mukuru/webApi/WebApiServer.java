package com.mukuru.webApi;

import io.javalin.*;

public class WebApiServer {
    private final Javalin server;

    public WebApiServer(){

        server = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });


//        server.post("/customers/{id}", WebApiHandler::registerCustomer);
//        server.post("/customers/{id}/send", WebApiHandler::sendMoney);
//        server.get("/customers/{id}/points", WebApiHandler::getPoints);
//        server.get("/customers/{id}/transactions", WebApiHandler::getTransactions);
//        server.get("/rewards", WebApiHandler::getRewards);
//        server.post("/customers/{id}/redeem/{rewardId}", WebApiHandler::redeemReward);

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

