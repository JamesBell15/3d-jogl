import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
  
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
  private SGNode robotRoot, roomRoot, tableRoot, lampOneRoot, lampTwoRoot;
  private Table table;
  private AnglePoiseLamp anglePoiseLamp1, anglePoiseLamp2;

  private Mesh mesh;
  private Shader shader;
  private Material material;

  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();

    Texture textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    Texture textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    Texture textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    Texture textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    Texture textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    Texture textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    Texture textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    Texture textureId7 = TextureLibrary.loadTexture(gl, "textures/window.png");
        
    globalLights[0] = new GlobalLight(
      new Vec3(1f, 0f, 0f),
      new Vec3(0.5f, 0f, 0.5f),
      new Vec3(0.5f, 0f, 0.5f),
      new Vec3(0.5f, 0f, 0.5f),
      true
    );
    globalLights[1] = new GlobalLight(
      new Vec3(0f, 0f, 1f),
      new Vec3(0f, 0.5f, 0f),
      new Vec3(0f, 0.5f, 0f),
      new Vec3(0f, 0.5f, 0f),
      true
    );

    material = new Material(new Vec3(0f, 1f, 0f), new Vec3(0f, 1f, 0f), new Vec3(0f, 1f, 0f), 200000f);

    float cutOff = (float) Math.cos(5.0*Math.PI/180.0);
    float outerCutOff = (float) Math.cos(35.0*Math.PI/180.0);

    spotLights[0] = new SpotLight(gl, new Vec3(0f, 0f, 0f), cutOff, outerCutOff, true);
    spotLights[0].setMaterial(material);
    // spotLights[0].setPosition(new Vec3(0f, 0f, 0f));
    spotLights[0].setCamera(camera);

    material = new Material(new Vec3(1f, 0f, 1f), new Vec3(1f, 0f, 1f), new Vec3(1f, 0f, 1f), 2000f);

    spotLights[1] = new SpotLight(gl, new Vec3(0f, 0f, 0f), cutOff, outerCutOff, true);
    spotLights[1].setMaterial(material);
    spotLights[1].setCamera(camera);

    Room room = new Room(gl, camera, lights, globalLights, spotLights, textureId1, textureId0, textureId7);
    roomRoot = room.get_scene_graph();

    Texture[] eggTextures = new Texture[]{textureId3, textureId4};

    table = new Table(gl, camera, lights, globalLights, spotLights, textureId5, eggTextures);
    tableRoot = table.get_scene_graph();

    anglePoiseLamp1 = new AnglePoiseLamp(
      gl,
      camera,
      lights,
      globalLights,
      spotLights,
      textureId1,
      spotLights[0],
      new Vec3(3f, 0f, 0f),
      new Vec3(2f, 2f, 2f),
      0f,
      -60.0f,
      150f,
      -90f
    );
    lampOneRoot = anglePoiseLamp1.get_scene_graph();

    anglePoiseLamp2 = new AnglePoiseLamp(
      gl,
      camera,
      lights,
      globalLights,
      spotLights,
      textureId1,
      spotLights[1],
      new Vec3(-5f, 0f, 0f),
      new Vec3(0.5f, 0.5f, 0.5f),
      180f,
      -60.0f,
      160f,
      -90f
    );
    lampTwoRoot = anglePoiseLamp2.get_scene_graph();

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
      shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
      material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
      modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
      sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, textureId1, textureId2);

      mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
      shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
      material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
      modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
      cube = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, textureId3, textureId4);

      cube2 = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, textureId5, textureId6);

      // robot

      float bodyHeight = 3f;
      float bodyWidth = 2f;
      float bodyDepth = 1f;
      float headScale = 2f;
      float armLength = 3.5f;
      float armScale = 0.5f;
      float legLength = 3.5f;
      float legScale = 0.67f;

      robotRoot = new NameNode("root");
      robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,-16));

      TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,legLength,0));

      NameNode body = new NameNode("body");
        Mat4 m = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode bodyTransform = new TransformNode("body transform", m);
          ModelNode bodyShape = new ModelNode("Cube(body)", cube);

      NameNode head = new NameNode("head");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,bodyHeight,0));
        m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode headTransform = new TransformNode("head transform", m);
          ModelNode headShape = new ModelNode("Sphere(head)", sphere);

     NameNode leftarm = new NameNode("left arm");
        TransformNode leftArmTranslate = new TransformNode("leftarm translate",
                                             Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
        leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode leftArmScale = new TransformNode("leftarm scale", m);
          ModelNode leftArmShape = new ModelNode("Cube(left arm)", cube2);

      NameNode rightarm = new NameNode("right arm");
        TransformNode rightArmTranslate = new TransformNode("rightarm translate",
                                              Mat4Transform.translate(-(bodyWidth*0.5f)-(armScale*0.5f),bodyHeight,0));
        rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundX(180));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode rightArmScale = new TransformNode("rightarm scale", m);
          ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube2);

      NameNode leftleg = new NameNode("left leg");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
        m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode leftlegTransform = new TransformNode("leftleg transform", m);
          ModelNode leftLegShape = new ModelNode("Cube(leftleg)", cube);

      NameNode rightleg = new NameNode("right leg");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
        m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode rightlegTransform = new TransformNode("rightleg transform", m);
          ModelNode rightLegShape = new ModelNode("Cube(rightleg)", cube);

      robotRoot.addChild(robotMoveTranslate);
        robotMoveTranslate.addChild(robotTranslate);
          robotTranslate.addChild(body);
            body.addChild(bodyTransform);
              bodyTransform.addChild(bodyShape);
            body.addChild(head);
              head.addChild(headTransform);
              headTransform.addChild(headShape);
            body.addChild(leftarm);
              leftarm.addChild(leftArmTranslate);
              leftArmTranslate.addChild(leftArmRotate);
              leftArmRotate.addChild(leftArmScale);
              leftArmScale.addChild(leftArmShape);
            body.addChild(rightarm);
              rightarm.addChild(rightArmTranslate);
              rightArmTranslate.addChild(rightArmRotate);
              rightArmRotate.addChild(rightArmScale);
              rightArmScale.addChild(rightArmShape);
            body.addChild(leftleg);
              leftleg.addChild(leftlegTransform);
              leftlegTransform.addChild(leftLegShape);
            body.addChild(rightleg);
              rightleg.addChild(rightlegTransform);
              rightlegTransform.addChild(rightLegShape);

      robotRoot.update();  // IMPORTANT - don't forget this
      //robotRoot.print(0, false);
      //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    double elapsedTime = getSeconds()-startTime;
    if (animation) updateLeftArm();
    anglePoiseLamp1.update_rotations(lampRotations[0]);
    lampOneRoot.draw(gl);
    anglePoiseLamp2.update_rotations(lampRotations[1]);
    lampTwoRoot.draw(gl);
    robotRoot.draw(gl);
    table.update_egg((float)elapsedTime);
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