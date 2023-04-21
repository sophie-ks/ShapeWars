package com.mygdx.shapewars.network.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.shapewars.model.ShapeWarsModel;
import com.mygdx.shapewars.network.data.GameResponse;
import com.mygdx.shapewars.network.data.LobbyResponse;

public class ClientListener extends Listener {

    private ShapeWarsModel model;

    public ClientListener(ShapeWarsModel model) {
        this.model = model;
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Client connected");
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Client disconnected");
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof LobbyResponse) {
            LobbyResponse response = (LobbyResponse) object;
            model.numPlayers = response.numPlayers;
            model.shipId = response.clientShipId;
            model.selectedMap = response.selectedMap;
            // the ordering here is important!

            if (!model.isGameActive && response.isGameActive) {
                model.createEntitiesFlag = true;
            }
            model.isGameActive = response.isGameActive;
        }
        if (object instanceof GameResponse) {
            GameResponse response = (GameResponse) object;
            if (!model.updateSystemStrategy.updated) {
                model.updateSystemStrategy.replaceData(response.ships, response.bullets);
                model.updateSystemStrategy.updated = true;
            }
        }
    }
}
