package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class HelloCollision extends SimpleApplication implements ActionListener {
    
    private static final String LEFT = "Left";
    private static final String RIGHT = "Right";
    private static final String UP = "Up";
    private static final String DOWN = "Down";
    private static final String JUMP = "Jump";

    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    public static void main(String[] args) {
      HelloCollision app = new HelloCollision();
      app.start();
    }

    @Override
    public void simpleInitApp() {
      bulletAppState = new BulletAppState();
      stateManager.attach(bulletAppState);

      viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
      flyCam.setMoveSpeed(100);
      setUpKeys();
      setUpLight();
      inputManager.setCursorVisible(true);

      assetManager.registerLocator("town.zip", ZipLocator.class);
      sceneModel = assetManager.loadModel("main.scene");
      sceneModel.setLocalScale(2f);

      CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
      landscape = new RigidBodyControl(sceneShape, 0);
      sceneModel.addControl(landscape);

      CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
      player = new CharacterControl(capsuleShape, 0.05f);
      player.setJumpSpeed(20);
      player.setFallSpeed(30);

      rootNode.attachChild(sceneModel);
      bulletAppState.getPhysicsSpace().add(landscape);
      bulletAppState.getPhysicsSpace().add(player);

      player.setGravity(new Vector3f(0,-30f,0));
      player.setPhysicsLocation(new Vector3f(0, 10, 0));
    }

    private void setUpLight() {
      // We add light so we see the scene
      AmbientLight al = new AmbientLight();
      al.setColor(ColorRGBA.White.mult(1.3f));
      rootNode.addLight(al);

      DirectionalLight dl = new DirectionalLight();
      dl.setColor(ColorRGBA.White);
      dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
      rootNode.addLight(dl);
    }

    private void setUpKeys() {
      inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
      inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
      inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
      inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
      inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
      inputManager.addListener(this, LEFT);
      inputManager.addListener(this, RIGHT);
      inputManager.addListener(this, UP);
      inputManager.addListener(this, DOWN);
      inputManager.addListener(this, JUMP);
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case JUMP:
                System.out.println(JUMP);
                if (isPressed) { player.jump(new Vector3f(0,20f,0));}
                break;
            case LEFT:
                System.out.println(LEFT);
                left = isPressed;
                break;
            case RIGHT:
                System.out.println(RIGHT);
                right= isPressed;
                break;
            case UP:
                System.out.println(UP);
                up = isPressed;
                break;
            case DOWN:
                System.out.println(DOWN);
                down = isPressed;
                break;
            default:
                break;
        }
    }

    @Override
      public void simpleUpdate(float tpf) {
          camDir.set(cam.getDirection()).multLocal(0.6f);
          camLeft.set(cam.getLeft()).multLocal(0.4f);
          walkDirection.set(0, 0, 0);
          if (left) {
              walkDirection.addLocal(camLeft);
          }
          if (right) {
              walkDirection.addLocal(camLeft.negate());
          }
          if (up) {
              walkDirection.addLocal(camDir);
          }
          if (down) {
              walkDirection.addLocal(camDir.negate());
          }
          player.setWalkDirection(walkDirection);
          cam.setLocation(player.getPhysicsLocation());
      }
}
