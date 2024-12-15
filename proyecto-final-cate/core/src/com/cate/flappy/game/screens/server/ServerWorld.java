package com.cate.flappy.game.screens.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.cate.flappy.Counter.Bird;
import com.cate.flappy.Counter.Counter;
import com.cate.flappy.Counter.Pipe;
import com.cate.flappy.net.RedUtiles;
import com.cate.flappy.screens.Screens;

import static com.cate.flappy.MainFlppyBird.*;

public class ServerWorld {
    final float WIDTH = Screens.WORLD_WIDTH;
    final float HEIGHT = Screens.WORLD_HEIGHT;

    static final int STATE_RUNNING = 0;
    static final int STATE_GAME_OVER = 1;
    private final ServerScreen screen;
    public int state;

    final float TIME_TO_SPAWN_PIPE = 1.5f;
    float timeToSpawnPipe;

    public World oWorldBox;
    public int score;

    public static Array<Bird> arrBirds;
    Array<Pipe> arrPipes;
    Array<Body> arrBodies;

    public ServerWorld(ServerScreen screen) {
        this.screen = screen;
        oWorldBox = new World(new Vector2(0, -13.0f), true);
        oWorldBox.setContactListener(new Collisions());

        this.arrBirds = new Array<>();
        arrPipes = new Array<>();
        arrBodies = new Array<>();

        timeToSpawnPipe = 1.5f;

        createBirds();
        createRoof();
        createFloor();

        state = STATE_RUNNING;
    }

    private void createBirds() {

        Bird bird1 = new Bird(1.35f, 4.75f, 0);
        Bird bird2 = new Bird(1.35f, 4.75f, 1);
        arrBirds.add(bird1);
        arrBirds.add(bird2);

        for (Bird bird : arrBirds) {
            BodyDef bd = new BodyDef();
            bd.position.set(bird.position);
            bd.type = BodyDef.BodyType.DynamicBody;

            Body oBody = oWorldBox.createBody(bd);

            CircleShape shape = new CircleShape();
            shape.setRadius(.25f);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = 8;
            fixture.filter.categoryBits = BIRD_CATEGORY;
            fixture.filter.maskBits = COUNTER_CATEGORY | PIPE_CATEGORY;

            oBody.createFixture(fixture);

            oBody.setFixedRotation(true);
            oBody.setUserData(bird);
            oBody.setBullet(true);

            bird.body = oBody;

            shape.dispose();
        }
    }

    private void createRoof() {
        BodyDef bd = new BodyDef();
        bd.position.set(0, HEIGHT);
        bd.type = BodyDef.BodyType.StaticBody;
        Body oBody = oWorldBox.createBody(bd);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, WIDTH, 0);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;

        oBody.createFixture(fixture);
        shape.dispose();
    }

    private void createFloor() {
        BodyDef bd = new BodyDef();
        bd.position.set(0, 1.1f);
        bd.type = BodyDef.BodyType.StaticBody;
        Body oBody = oWorldBox.createBody(bd);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, WIDTH, 0);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.filter.categoryBits = PIPE_CATEGORY;

        oBody.createFixture(fixture);
        shape.dispose();
    }

    public void addPipe() {
        float x = WIDTH + 2.5f;
        float y = MathUtils.random() * (1.5f) + .4f;


        Pipe bottomPipe = addPipe(x, y, false);


        Pipe topPipe = addPipe(x, y + 2f + Pipe.HEIGHT, true);


        addCounter(x, y + Counter.HEIGHT / 2f + Pipe.HEIGHT / 2f + .1f);


        RedUtiles.server.enviarMensaje("pipe_create#" + bottomPipe.id + "#" + x + "#" + y + "#0");
        RedUtiles.server.enviarMensaje("pipe_create#" + topPipe.id + "#" + x + "#" + (y + 2f + Pipe.HEIGHT) + "#1");

/*        NetUtils.server.enviarMensaje("pipe_create#" +"#" + x + "#" + y+"#0");
        NetUtils.server.enviarMensaje("pipe_create#+"#" + x + "#" + (y + 2f + Pipe.HEIGHT) +"#1");*/

    }

    private Pipe addPipe(float x, float y, boolean isTopPipe) {
        Pipe obj;
        if (isTopPipe)
            obj = new Pipe(x, y, Pipe.TYPE_UP);
        else
            obj = new Pipe(x, y, Pipe.TYPE_DOWN);

        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.KinematicBody;
        Body oBody = oWorldBox.createBody(bd);
        oBody.setLinearVelocity(Pipe.SPEED_X, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Pipe.WIDTH / 2f, Pipe.HEIGHT / 2f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.filter.categoryBits = PIPE_CATEGORY;
        oBody.createFixture(fixture);
        oBody.setFixedRotation(true);
        oBody.setUserData(obj);
        arrPipes.add(obj);
        shape.dispose();
        return obj;
    }

    private void addCounter(float x, float y) {
        Counter obj = new Counter();
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.KinematicBody;
        Body oBody = oWorldBox.createBody(bd);
        oBody.setLinearVelocity(Counter.SPEED_X, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Counter.WIDTH / 2f, Counter.HEIGHT / 2f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.isSensor = true;
        fixture.filter.categoryBits = COUNTER_CATEGORY;

        oBody.createFixture(fixture);
        oBody.setFixedRotation(true);
        oBody.setUserData(obj);

        shape.dispose();
    }

    public void update(float delta, boolean jump1, boolean jump2) {
        oWorldBox.step(delta, 8, 4);

        deleteObjects();

        timeToSpawnPipe += delta;

        if (timeToSpawnPipe >= TIME_TO_SPAWN_PIPE) {
            timeToSpawnPipe -= TIME_TO_SPAWN_PIPE;
            addPipe();
        }

        oWorldBox.getBodies(arrBodies);


        for (int i = 0; i < arrBirds.size; i++) {
            Bird bird = arrBirds.get(i);
            float oldX = bird.position.x;
            float oldY = bird.position.y;
            if (bird.body == null) continue;

            updateBird(bird.body, delta, i == 0 ? jump1 : jump2);
            if (bird.state == Bird.STATE_NORMAL && (oldX != bird.position.x || oldY != bird.position.y))
                RedUtiles.server.enviarMensaje("posicion#" + i + "#" + +bird.position.x + "#" + bird.position.y);
        }

        for (Body body : arrBodies) {
            if (body.getUserData() instanceof Pipe) {
                updatePipes(body);
            } else if (body.getUserData() instanceof Counter) {
                updateCounter(body);
            }
        }


        boolean end = true;
        for (Bird bird : arrBirds) {
            if (bird.state == Bird.STATE_NORMAL) end = false;
        }
        if (end) {
            int bestScore = -1;
            int bestScoreIndex = -1;
            for (int i = 0; i < arrBirds.size; i++) {
                if (arrBirds.get(i).score > bestScore) {
                    bestScore = arrBirds.get(i).score;
                    bestScoreIndex = i;
                }
                else if (arrBirds.get(i).score == bestScore) {
                    bestScoreIndex = 3;
                };
            }

            state = STATE_GAME_OVER;
            RedUtiles.server.terminarJuego(bestScoreIndex);
            this.screen.game.setScreen(new ServerScreen(this.screen.game));
        }
    }

    private void updateBird(Body body, float delta, boolean jump) {
        Bird bird = (Bird) body.getUserData();
        if (bird == null) return;
        bird.update(delta, body);
    /*    if (jump && bird.state == Bird.STATE_NORMAL) {
            body.setLinearVelocity(0, Bird.JUMP_SPEED);
        }*/
    }

    private void updatePipes(Body body) {
        boolean activeBirds = false;

        for (Bird bird : arrBirds) {
            if (bird.state == Bird.STATE_NORMAL) {
                activeBirds = true;
                break;
            }
        }

        if (activeBirds) {
            Pipe obj = (Pipe) body.getUserData();
            obj.update(body);
            RedUtiles.server.enviarMensaje("pipe_update#" + obj.id + "#" + obj.position.x + "#" + obj.position.y);
            if (obj.position.x <= -5) obj.state = Pipe.STATE_REMOVE;
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    private void updateCounter(Body body) {
        boolean activeBirds = false;
        float currentBirdX = 0;
        for (Bird bird : arrBirds) {
            if (bird.state == Bird.STATE_NORMAL) {
                activeBirds = true;
                currentBirdX = bird.position.x;
                break;
            }
        }

        if (activeBirds) {
            Counter obj = (Counter) body.getUserData();
            obj.update(body);
            if (obj.position.x <= -5 || obj.position.x < (currentBirdX - 2)) obj.state = Counter.STATE_REMOVE;
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    private void deleteObjects() {
        oWorldBox.getBodies(arrBodies);

        for (Body body : arrBodies) {
            if (!oWorldBox.isLocked()) {
                if (body.getUserData() instanceof Pipe) {
                    Pipe obj = (Pipe) body.getUserData();
                    if (obj.state == Pipe.STATE_REMOVE) {
                        arrPipes.removeValue(obj, true);
                        RedUtiles.server.enviarMensaje("pipe_delete#" + obj.id);
                        oWorldBox.destroyBody(body);
                    }
                } else if (body.getUserData() instanceof Counter) {
                    Counter obj = (Counter) body.getUserData();
                    if (obj.state == Counter.STATE_REMOVE) {
                        oWorldBox.destroyBody(body);
                    }
                }
            }
        }
    }

    class Collisions implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Fixture a = contact.getFixtureA();
            Fixture b = contact.getFixtureB();

            if (a.getBody().getUserData() instanceof Bird && b.getBody().getUserData() instanceof Bird) return;

            if (a.getBody().getUserData() instanceof Bird)
                beginContactBird(a, b);
            else if (b.getBody().getUserData() instanceof Bird)
                beginContactBird(b, a);
        }

        private void beginContactBird(Fixture bird, Fixture otraCosa) {
            Object somethingElse = otraCosa.getBody().getUserData();
            Bird birdObj = (Bird) bird.getBody().getUserData();

            if (somethingElse instanceof Counter) {
                Counter obj = (Counter) somethingElse;
                if (obj.state == Counter.STATE_NORMAL) {
                    System.out.println("score");

                    birdObj.score++;
                    RedUtiles.server.enviarMensaje("score#" + birdObj.id + "#" + birdObj.score);
                }
            } else if (somethingElse instanceof Pipe) {
                birdObj.state = Bird.STATE_DEAD;
                RedUtiles.server.enviarMensaje("bird_dead#" + birdObj.id);

            }
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
