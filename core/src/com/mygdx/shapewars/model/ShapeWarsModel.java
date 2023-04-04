package com.mygdx.shapewars.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.shapewars.controller.Joystick;
import com.mygdx.shapewars.model.components.HealthComponent;
import com.mygdx.shapewars.model.components.IdentityComponent;
import com.mygdx.shapewars.model.components.PositionComponent;
import com.mygdx.shapewars.model.components.SpriteComponent;
import com.mygdx.shapewars.model.components.VelocityComponent;
import com.mygdx.shapewars.model.system.DeathSystem;
import com.mygdx.shapewars.model.system.InputSystem;
import com.mygdx.shapewars.model.system.MovementSystem;
import com.mygdx.shapewars.model.system.SpriteSystem;
import com.mygdx.shapewars.model.system.RicochetSystem;
import com.mygdx.shapewars.network.Role;
import com.mygdx.shapewars.network.client.ClientConnector;
import com.mygdx.shapewars.network.server.ServerConnector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShapeWarsModel {

    public static final int TANK_WIDTH = 75;
    public static final int TANK_HEIGHT = 75;
    public static final int NUM_PLAYERS = 2; // todo add lobby
    public SpriteBatch batch;
    public static Engine engine;
    public static MovementSystem movementSystem;
    public static RicochetSystem ricochetSystem;
    public static DeathSystem deathSystem;

    private static TiledMap map;
    private Role role = Role.Server; // change with client/ hosts screens
    public InputSystem inputSystem;
    public SpriteSystem spriteSystem;
    public ServerConnector serverConnector; // todo implement strategy pattern
    public ClientConnector clientConnector;
    public String clientId;
    public HashMap<String, Integer> clientTankMapping = new HashMap<>();
    public Joystick joystick;

    public ShapeWarsModel() {
        TmxMapLoader loader = new TmxMapLoader();
        /*
            current map structure:
            0 = groundLayer
            1 = collisionLayer
            2 = bulletLayer
            3 = spawnLayer
            4, 5, ... = non-existent yet
         */

        map = loader.load("maps/map2.tmx"); // make server send this AFTER sophie is done
        batch = new SpriteBatch();
        engine = new Engine();

        joystick = new Joystick(200, 200, 70, 40);

        if (this.role == Role.Server) {
            this.serverConnector = new ServerConnector(this);
            TiledMapTileLayer spawnLayer = (TiledMapTileLayer) map.getLayers().get(3);

            List<Vector2> spawnCells = new ArrayList<>();
            for (int y = 0; y < spawnLayer.getHeight(); y++) {
                for (int x = 0; x < spawnLayer.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = spawnLayer.getCell(x, y);
                    if (cell != null) {
                        spawnCells.add(new Vector2(x, y));
                    }
                }
            }

            for (int i = 0; i < NUM_PLAYERS; i++) {
                Entity tank = new Entity();
                Vector2 cell = spawnCells.get(i);
                tank.add(new PositionComponent(cell.x * spawnLayer.getTileWidth(), cell.y * spawnLayer.getTileHeight()));
                tank.add(new VelocityComponent(0, 0));
                tank.add(new SpriteComponent("tank_graphics.png", TANK_WIDTH, TANK_HEIGHT)); // change to support multiple colors
                tank.add(new HealthComponent(100));
                tank.add(new IdentityComponent(i));
                engine.addEntity(tank);
            }
            movementSystem = movementSystem.getInstance(map);
            engine.addSystem(movementSystem);


        } else if (this.role == Role.Client) {
            this.clientConnector = new ClientConnector(this);
            this.clientId = UUID.randomUUID().toString();
            // todo send initial request (initial position <int, int>; map name <string>; tank sprite files <string[]>)

            for (int i = 0; i < NUM_PLAYERS; i++) {
                Entity tank = new Entity();
                tank.add(new PositionComponent(0, 0));
                tank.add(new VelocityComponent(0, 0));
                tank.add(new SpriteComponent("tank_graphics.png", TANK_WIDTH, TANK_HEIGHT)); // todo change to support multiple colors
                tank.add(new HealthComponent(100));
                tank.add(new IdentityComponent(i));
                engine.addEntity(tank);
            }
        }

        inputSystem = InputSystem.getInstance(role, clientConnector, clientId, joystick);
        movementSystem = MovementSystem.getInstance();
        ricochetSystem = RicochetSystem.getInstance();
        deathSystem = DeathSystem.getInstance();
        engine.addSystem(inputSystem);
        spriteSystem = spriteSystem.getInstance();
        engine.addSystem(spriteSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(ricochetSystem);
        engine.addSystem(deathSystem);
    }

    public static void update() {
        engine.update(Gdx.graphics.getDeltaTime());
    }

    public static void addToEngine(Entity entity) {
      engine.addEntity(entity);
    }

    public static void removeFromEngine(Entity entity) {
        engine.removeEntity(entity);
      }

    public static TiledMap getMap() {
        return map;
    }

    public Joystick getJoystick() {
        return joystick;
    }

    public static TiledMapTileLayer getLayer(int layerId) {
      return (TiledMapTileLayer) getMap().getLayers().get(layerId);
    }
}
