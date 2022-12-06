import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
  
/* I declare that this code is my own work */
/* Author James Bell jbell15@sheffield.ac.uk */

public class Hatch_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Hatch_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(5f,5f,20f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    // lights[0].dispose(gl);
    // lights[1].dispose(gl);
    spotLights[0].dispose(gl);
    spotLights[1].dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  public void toggleGlobalLight(int index){
    globalLights[index].setToggle(!globalLights[index].getToggle());
  }

  public void toggleSpotLight(int index){
    spotLights[index].setToggle(!spotLights[index].getToggle());
  }

  private float[][] lampRotations = {new float[4], new float[4]};

  public void setLampRotations(int index,
    float newLowerArmRotationY,
    float newLowerArmRotationZ,
    float newJointRotationZ,
    float newHeadRotationZ) {
    lampRotations[index][0] = newLowerArmRotationY;
    lampRotations[index][1] = newLowerArmRotationZ;
    lampRotations[index][2] = newJointRotationZ;
    lampRotations[index][3] = newHeadRotationZ;
  }

  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
   
  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }
  
  public void loweredArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();
  }
   
  public void raisedArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightArmRotate.update();
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective, modelMatrix;
  private Model floor, sphere, cube, cube2, twoTriangles;
  private GlobalLight[] globalLights = new GlobalLight[Constants.NUMBER_OF_GLOBAL_LIGHTS];
  private Light[] lights = new Light[Constants.NUMBER_OF_POINT_LIGHTS];
  private SpotLight[] spotLights = new SpotLight[Constants.NUMBER_OF_SPOT_LIGHTS];
  private SGNode robotRoot, roomRoot, spaceRoot, tableRoot, lampOneRoot, lampTwoRoot;
  private Table table;
  private AnglePoiseLamp anglePoiseLamp1, anglePoiseLamp2;
  private Space space;

  private Mesh mesh;
  private Shader shader;
  private Material material;

  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();

    Texture textureFloor = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    Texture textureWindow = TextureLibrary.loadTexture(gl, "textures/window.png");
    Texture textureWall = TextureLibrary.loadTexture(gl, "textures/wall.png");
    Texture textureDefaultWall = TextureLibrary.loadTexture(gl, "textures/default_wall.jpg");
    Texture textureEgg = TextureLibrary.loadTexture(gl, "textures/egg.png");
    Texture textureEggSpecular = TextureLibrary.loadTexture(gl, "textures/egg_specular.png");
    Texture textureWood = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    Texture texturePedestal = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    Texture textureSpace = TextureLibrary.loadTexture(gl, "textures/space_texture_2.jpg");
    Texture texturePlanet = TextureLibrary.loadTexture(gl, "textures/marsmap1k.jpg");
    Texture textureRing = TextureLibrary.loadTexture(gl, "textures/ring.png");
    Texture textureLamp1 = TextureLibrary.loadTexture(gl, "textures/lamp1.png");
    Texture textureLamp2 = TextureLibrary.loadTexture(gl, "textures/lamp2.png");
    Texture textureLampGrowth = TextureLibrary.loadTexture(gl, "textures/growth.png");
    Texture textureLampEye = TextureLibrary.loadTexture(gl, "textures/eye.png");
        
    globalLights[0] = new GlobalLight(
      new Vec3(1f, 0f, 0f),
      new Vec3(0.25f, 0.25f, 0.25f),
      new Vec3(0.25f, 0.25f, 0.25f),
      new Vec3(0.25f, 0.25f, 0.25f),
      true
    );
    globalLights[1] = new GlobalLight(
      new Vec3(0f, 0f, 1f),
      new Vec3(0.25f, 0.25f, 0.25f),
      new Vec3(0.25f, 0.25f, 0.25f),
      new Vec3(0.25f, 0.25f, 0.25f),
      true
    );

    material = new Material(new Vec3(0f, 1f, 0f), new Vec3(1f, 1f, 1f), new Vec3(0f, 1f, 0f), 200000f);

    float cutOff = (float) Math.cos(5.0*Math.PI/180.0);
    float outerCutOff = (float) Math.cos(35.0*Math.PI/180.0);

    spotLights[0] = new SpotLight(gl, new Vec3(0f, 0f, 0f), cutOff, outerCutOff, true);
    spotLights[0].setMaterial(material);
    spotLights[0].setCamera(camera);

    material = new Material(new Vec3(1f, 0f, 1f), new Vec3(1f, 1f, 1f), new Vec3(1f, 0f, 1f), 2000f);

    spotLights[1] = new SpotLight(gl, new Vec3(0f, 0f, 0f), cutOff, outerCutOff, true);
    spotLights[1].setMaterial(material);
    spotLights[1].setCamera(camera);

    Texture[] wallTextures = new Texture[]{textureWall, textureDefaultWall};

    Room room = new Room(gl, camera, lights, globalLights, spotLights, wallTextures, textureFloor, textureWindow);
    roomRoot = room.get_scene_graph();

    space = new Space(gl, camera, lights, globalLights, spotLights, textureSpace, texturePlanet, textureRing);
    spaceRoot = space.get_scene_graph();

    Texture[] eggTextures = new Texture[]{textureEgg, textureEggSpecular};

    table = new Table(gl, camera, lights, globalLights, spotLights, textureWood, texturePedestal, eggTextures);
    tableRoot = table.get_scene_graph();

    anglePoiseLamp1 = new AnglePoiseLamp(
      gl,
      camera,
      lights,
      globalLights,
      spotLights,
      textureLamp1,
      textureLampEye,
      textureLampGrowth,
      spotLights[0],
      new Vec3(6f, 0f, 0f),
      new Vec3(2f, 2f, 2f),
      0f,
      -60.0f,
      130f,
      -90f,
      true
    );

    lampOneRoot = anglePoiseLamp1.get_scene_graph();
    setLampRotations(0, 0f, -60.0f, 130f, -90f);

    anglePoiseLamp2 = new AnglePoiseLamp(
      gl,
      camera,
      lights,
      globalLights,
      spotLights,
      textureLamp2,
      textureLampEye,
      textureLampGrowth,
      spotLights[1],
      new Vec3(-5f, 0f, 0f),
      new Vec3(1f, 1f, 1f),
      180f,
      -30.0f,
      90f,
      -80f,
      false
    );
    lampTwoRoot = anglePoiseLamp2.get_scene_graph();
    setLampRotations(1, 180f, -30.0f, 90f, -80f);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    float elapsedTime = (float)(getSeconds()-startTime);
    if (animation) updateLeftArm();
    space.animate_planet(elapsedTime);
    spaceRoot.draw(gl);
    anglePoiseLamp1.update_rotations(lampRotations[0]);
    lampOneRoot.draw(gl);
    anglePoiseLamp2.update_rotations(lampRotations[1]);
    lampTwoRoot.draw(gl);
    table.update_egg(elapsedTime);
    tableRoot.draw(gl);
    // order matters draw transparent last
    roomRoot.draw(gl);
  }

  private void updateLeftArm() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    leftArmRotate.update();
  }


  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}