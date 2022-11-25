import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
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
    light.dispose(gl);
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
  private Light light;
  private SGNode robotRoot, roomRoot;
  
  private Mesh mesh;
  private Shader shader;
  private Material material;

  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;

  // room nodes
  // floor
  private TransformNode roomScale, floorTransform, floorScale;
  // wall
  private TransformNode wallTransform0, wallRotate0, wallScale0;
  private TransformNode wallTransform1, wallRotate1, wallScale1;
  // window
  private TransformNode windowTransform, windowRotate, windowScale;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    
        
    light = new Light(gl);
    light.setCamera(camera);
    
    Room room = new Room(gl, camera, light, textureId1, textureId0);
    roomRoot = room.get_scene_graph();

    // Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    // Shader shader = new Shader(gl, "vs_tt.txt", "fs_tt_texture.txt");
    // Material material = new Material(
    //                                   new Vec3(0.0f, 0.5f, 0.81f),
    //                                   new Vec3(0.0f, 0.5f, 0.81f),
    //                                   new Vec3(0.3f, 0.3f, 0.3f),
    //                                   32.0f
    //                                 );
    // Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);
    // twoTriangles = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

    // // Room
    // roomRoot = new NameNode("root");
    // float scaleFactor = 16f;
    // Vec3 scale = new Vec3(scaleFactor, scaleFactor, scaleFactor);
    // roomScale = new TransformNode("Scale Room", Mat4Transform.scale(scale));

    // // Floor
    // NameNode roomFloor = new NameNode("floor");
    // Vec3 transform = new Vec3(0f, 0f, 0f);
    // floorTransform = new TransformNode("Transform Floor", Mat4Transform.translate(transform));

    // scale = new Vec3(1f, 1f, 1f);
    // floorScale = new TransformNode("Scale Floor", Mat4Transform.scale(scale));

    // ModelNode floorShape = new ModelNode("Two Triangles(floor)", twoTriangles);

    // // Walls
    // twoTriangles = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

    // // Wall 0
    // NameNode roomWall0 = new NameNode("wall 0");
    // transform = new Vec3(0.5f, 0.5f, 0f);
    // wallTransform0 = new TransformNode("Transform Wall 0", Mat4Transform.translate(transform));

    // float rotate = 90f;
    // wallRotate0 =  new TransformNode("Rotate Wall 0", Mat4Transform.rotateAroundZ(rotate));

    // scale = new Vec3(1f, 1f, 1f);
    // wallScale0 = new TransformNode("Scale Wall 0", Mat4Transform.scale(scale));

    // ModelNode wallShape0 = new ModelNode("Two Triangles(Wall 0)", twoTriangles);

    // // Wall 1
    // NameNode roomWall1 = new NameNode("wall 1");
    // transform = new Vec3(-0.5f, 0.5f, 0f);
    // wallTransform1 = new TransformNode("Transform Wall 1", Mat4Transform.translate(transform));

    // rotate = 270f;
    // wallRotate1 =  new TransformNode("Rotate Wall 1", Mat4Transform.rotateAroundZ(rotate));

    // scale = new Vec3(1f, 1f, 1f);
    // wallScale1 = new TransformNode("Scale Wall 1", Mat4Transform.scale(scale));

    // ModelNode wallShape1 = new ModelNode("Two Triangles(Wall 1)", twoTriangles);

    // // Window
    // NameNode roomWindow = new NameNode("window");
    // transform = new Vec3(0f, 0.5f, -0.5f);
    // windowTransform = new TransformNode("Transform Window", Mat4Transform.translate(transform));

    // rotate = 90f;
    // windowRotate =  new TransformNode("Rotate Window", Mat4Transform.rotateAroundX(rotate));

    // scale = new Vec3(1f, 1f, 1f);
    // windowScale = new TransformNode("Scale Window", Mat4Transform.scale(scale));

    // ModelNode windowShape = new ModelNode("Two Triangles(Window)", twoTriangles);


    // roomRoot.addChild(roomScale);
    //   roomScale.addChild(roomFloor);
    //     roomFloor.addChild(floorTransform);
    //       roomScale.addChild(floorScale);
    //       floorScale.addChild(floorShape);
    //   roomScale.addChild(roomWall0);
    //     roomWall0.addChild(wallTransform0);
    //       wallTransform0.addChild(wallRotate0);
    //       wallRotate0.addChild(wallScale0);
    //       wallScale0.addChild(wallShape0);
    //   roomScale.addChild(roomWall1);
    //     roomWall1.addChild(wallTransform1);
    //       wallTransform1.addChild(wallRotate1);
    //       wallRotate1.addChild(wallScale1);
    //       wallScale1.addChild(wallShape1);
    //   roomScale.addChild(roomWindow);
    //     roomWindow.addChild(windowTransform);
    //       windowTransform.addChild(windowRotate);
    //       windowRotate.addChild(windowScale);
    //       windowScale.addChild(windowShape);

    // roomRoot.update();



    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
    
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);
    
    cube2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6); 
    
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
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    
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
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    roomRoot.draw(gl);
    if (animation) updateLeftArm();
    robotRoot.draw(gl);
  }

  private void updateLeftArm() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    leftArmRotate.update();
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
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