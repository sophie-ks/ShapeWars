package com.mygdx.shapewars.model.system;

import static com.mygdx.shapewars.config.GameConfig.ENEMY_DAMAGE_ONE;
import static com.mygdx.shapewars.config.GameConfig.ENEMY_DAMAGE_TWO;
import static com.mygdx.shapewars.config.GameConfig.PLAYER_DAMAGE_ONE;
import static com.mygdx.shapewars.config.GameConfig.PLAYER_DAMAGE_TWO;
import static com.mygdx.shapewars.config.GameConfig.SHIP_HEIGHT;
import static com.mygdx.shapewars.config.GameConfig.SHIP_WIDTH;
import static com.mygdx.shapewars.config.GameConfig.MAX_BULLET_HEALTH;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.shapewars.model.components.ComponentMappers;
import com.mygdx.shapewars.model.components.HealthComponent;
import com.mygdx.shapewars.model.components.IdentityComponent;
import com.mygdx.shapewars.model.components.ParentComponent;
import com.mygdx.shapewars.model.components.PositionComponent;
import com.mygdx.shapewars.model.components.SpriteComponent;
import com.mygdx.shapewars.model.components.VelocityComponent;
import com.badlogic.gdx.math.Polygon;
import java.util.ArrayList;


public class RicochetSystem extends EntitySystem {
  private ImmutableArray<Entity> bullets;
  private ImmutableArray<Entity> tanks;
  private ArrayList<Polygon> bulletObstacles;


  private static volatile RicochetSystem instance;

  private RicochetSystem(ArrayList<Polygon> bulletObstacles) {
    this.bulletObstacles = bulletObstacles;
  };

  public void addedToEngine(Engine engine) {
    bullets = engine.getEntitiesFor(
        Family.all(PositionComponent.class, VelocityComponent.class, SpriteComponent.class, HealthComponent.class)
            .exclude(IdentityComponent.class).get());
    tanks = engine.getEntitiesFor(
        Family.all(PositionComponent.class, VelocityComponent.class, SpriteComponent.class, HealthComponent.class,
            IdentityComponent.class).get());
  }

  public void update(float deltaTime) {
    for (Entity bullet : bullets) {
      PositionComponent bulletPositionComponent = ComponentMappers.position.get(bullet);
      VelocityComponent bulletVelocityComponent = ComponentMappers.velocity.get(bullet);
      SpriteComponent bulletSpriteComponent = ComponentMappers.sprite.get(bullet);
      HealthComponent bulletHealthComponent = ComponentMappers.health.get(bullet);

      // Check if bullet hits tank
      for (Entity tank : tanks) {
        PositionComponent tankPositionComponent = ComponentMappers.position.get(tank);
        SpriteComponent tankSpriteComponent = ComponentMappers.sprite.get(tank);
        HealthComponent tankHealthComponent = ComponentMappers.health.get(tank);
        IdentityComponent tankIdentityComponent = ComponentMappers.identity.get(tank);

        if (checkCollisionWithTank(bulletPositionComponent, tankPositionComponent, tankSpriteComponent)) {
          ParentComponent bulletParentComponent = ComponentMappers.parent.get(bullet);
          if (tank.equals(bulletParentComponent.getParent())) {
            if (bulletHealthComponent.getHealth() == MAX_BULLET_HEALTH) {
              break;
            }
          }
          tank.remove(SpriteComponent.class);
          if (tankIdentityComponent.getId() == 0) {
            if (tankHealthComponent.getHealth() >= 100) {
              tank.add(new SpriteComponent(PLAYER_DAMAGE_ONE, SHIP_WIDTH, SHIP_HEIGHT));
            }
            else if (tankHealthComponent.getHealth() >= 60) {
              tank.add(new SpriteComponent(PLAYER_DAMAGE_TWO, SHIP_WIDTH, SHIP_HEIGHT));
            }
          } else {
            if (tankHealthComponent.getHealth() >= 100) {
              tank.add(new SpriteComponent(ENEMY_DAMAGE_ONE, SHIP_WIDTH, SHIP_HEIGHT));
            }
            else if (tankHealthComponent.getHealth() >= 60) {
              tank.add(new SpriteComponent(ENEMY_DAMAGE_TWO, SHIP_WIDTH, SHIP_HEIGHT));
            }
          }
          tankHealthComponent.takeDamage(40);
          bulletHealthComponent.takeDamage(100);
          break;
        }
      }

      // calculate and set position
      float radians = MathUtils.degreesToRadians * bulletVelocityComponent.getDirection();

      float newX = bulletPositionComponent.getPosition().x + MathUtils.cos(radians) * bulletVelocityComponent.getValue();
      float newY = bulletPositionComponent.getPosition().y + MathUtils.sin(radians) * bulletVelocityComponent.getValue();

      boolean hasHitX = false;
      boolean hasHitY = false;

      Polygon rect = CollisionSystem.<Polygon>getCollisionWithWall(bullet, bulletObstacles, newX, newY);
      if (rect.getVertices().length > 0) {
        Rectangle wallsRect = rect.getBoundingRectangle();
        // adjust newX and newY based on collision direction
        if (bulletPositionComponent.getPosition().x <= wallsRect.getX()) {
          hasHitX = true;
          if (bulletPositionComponent.getPosition().y + bulletSpriteComponent.getSprite().getHeight() <= wallsRect.getY()) {
            newY = wallsRect.getY() - bulletSpriteComponent.getSprite().getHeight();
            hasHitX = false;
            hasHitY = true;
          } else if (bulletPositionComponent.getPosition().y >= wallsRect.getY() + wallsRect.getHeight()) {
            newY = wallsRect.getY() + wallsRect.getHeight();
          }
          newX = wallsRect.getX() - bulletSpriteComponent.getSprite().getWidth();
          // left collision
        } else if (bulletPositionComponent.getPosition().x >= wallsRect.getX() + wallsRect.getWidth()) {
          hasHitX = true;
          if (bulletPositionComponent.getPosition().y + bulletSpriteComponent.getSprite().getHeight() <= wallsRect.getY()) {
            newY = wallsRect.getY() - bulletSpriteComponent.getSprite().getHeight();
            hasHitX = false;
            hasHitY = true;
            // right collision
          } else if (bulletPositionComponent.getPosition().y >= wallsRect.getY() + wallsRect.getHeight()) {
            newY = wallsRect.getY() + wallsRect.getHeight();
            hasHitX = false;
            hasHitY = true;
          }
          newX = wallsRect.getX() + wallsRect.getWidth();
          // top collision
        } else if (bulletPositionComponent.getPosition().y + bulletSpriteComponent.getSprite().getHeight() <= wallsRect.getY()) {
          newY = wallsRect.getY() - bulletSpriteComponent.getSprite().getHeight();
          hasHitY = true;
          // bottom collision
        } else if (bulletPositionComponent.getPosition().y >= wallsRect.getY() + wallsRect.getHeight()) {
          newY = wallsRect.getY() + wallsRect.getHeight();
          hasHitY = true;
        }
      }


      // set new position
      bulletPositionComponent.setPosition(newX, newY);

      // set direction
      if (hasHitX || hasHitY) {
        bulletHealthComponent.takeDamage(1);
        if (hasHitX && hasHitY) {
          bulletVelocityComponent.setDirection((180 - bulletVelocityComponent.getDirection()) * -1);
        } else if (hasHitX && !hasHitY) {
          bulletVelocityComponent.setDirection(180 - bulletVelocityComponent.getDirection());
        } else {
          bulletVelocityComponent.setDirection(bulletVelocityComponent.getDirection() * -1);
        }
        // System.out.println(velocity.getDirection());
      }

      bulletSpriteComponent.getSprite().setRotation(bulletVelocityComponent.getDirection() + 90);

      bulletSpriteComponent.getSprite().setPosition(bulletPositionComponent.getPosition().x, bulletPositionComponent.getPosition().y);
    }
  }

  private boolean checkCollisionWithTank(PositionComponent bulletPosition, PositionComponent tankPositionComponent,
      SpriteComponent tankSpriteComponent) {
    float x1 = tankPositionComponent.getPosition().x;
    float y1 = tankPositionComponent.getPosition().y;
    float width = tankSpriteComponent.getSprite().getWidth();
    float height = tankSpriteComponent.getSprite().getWidth();
    float x2 = bulletPosition.getPosition().x;
    float y2 = bulletPosition.getPosition().y;
    return x2 >= x1 && x2 <= x1 + width && y2 >= y1 && y2 <= y1 + height;
  }

  public static RicochetSystem getInstance(ArrayList<Polygon> obstacles) {
    if (instance == null) {
      synchronized (FiringSystem.class) {
        if (instance == null) {
          instance = new RicochetSystem(obstacles);
        }
      }
    }
    return instance;
  }
}
