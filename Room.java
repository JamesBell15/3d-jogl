import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class Room {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private Texture wallTextureId;
  private Texture floorTextureId;
  private Texture windowTextureId;

  public Room(GL3 gl,
              Camera camera,
              Light light,
              Texture wallTextureId,
              Texture floorTextureId,
              Texture windowTextureId
            ){
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.wallTextureId = wallTextureId;
    this.floorTextureId = floorTextureId;
    this.windowTextureId = windowTextureId;
  }

  private SGNode root;
  private Model twoTriangles;
  // room nodes
  // floor
  private TransformNode roomScale, floorTransform, floorScale;
  // wall
  private TransformNode wallTransform0, wallRotate0, wallScale0;
  private TransformNode wallTransform1, wallRotate1, wallScale1;
  // window
  private TransformNode windowTransform, windowRotate, windowScale;

  public SGNode get_scene_graph(){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt.txt", "fs_tt_texture.txt");
    Material material = new Material(
                                      new Vec3(0.0f, 0.5f, 0.81f),
                                      new Vec3(0.0f, 0.5f, 0.81f),
                                      new Vec3(0.3f, 0.3f, 0.3f),
                                      32.0f
                                    );
    Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);

    // Room
    root = new NameNode("root");
    float scaleFactor = 16f;
    Vec3 scale = new Vec3(scaleFactor, scaleFactor, scaleFactor);
    roomScale = new TransformNode("Scale Room", Mat4Transform.scale(scale));

    // Floor
    twoTriangles = new Model(gl, camera, light, shader, material, modelMatrix, mesh, floorTextureId);

    NameNode roomFloor = new NameNode("floor");
    Vec3 transform = new Vec3(0f, 0f, 0f);
    floorTransform = new TransformNode("Transform Floor", Mat4Transform.translate(transform));

    scale = new Vec3(1f, 1f, 1f);
    floorScale = new TransformNode("Scale Floor", Mat4Transform.scale(scale));

    ModelNode floorShape = new ModelNode("Two Triangles(floor)", twoTriangles);

    // Walls
    twoTriangles = new Model(gl, camera, light, shader, material, modelMatrix, mesh, wallTextureId);

    // Wall 0
    NameNode roomWall0 = new NameNode("wall 0");
    transform = new Vec3(0.5f, 0.5f, 0f);
    wallTransform0 = new TransformNode("Transform Wall 0", Mat4Transform.translate(transform));

    float rotate = 90f;
    wallRotate0 =  new TransformNode("Rotate Wall 0", Mat4Transform.rotateAroundZ(rotate));

    scale = new Vec3(1f, 1f, 1f);
    wallScale0 = new TransformNode("Scale Wall 0", Mat4Transform.scale(scale));

    ModelNode wallShape0 = new ModelNode("Two Triangles(Wall 0)", twoTriangles);

    // Wall 1
    NameNode roomWall1 = new NameNode("wall 1");
    transform = new Vec3(-0.5f, 0.5f, 0f);
    wallTransform1 = new TransformNode("Transform Wall 1", Mat4Transform.translate(transform));

    rotate = 270f;
    wallRotate1 =  new TransformNode("Rotate Wall 1", Mat4Transform.rotateAroundZ(rotate));

    scale = new Vec3(1f, 1f, 1f);
    wallScale1 = new TransformNode("Scale Wall 1", Mat4Transform.scale(scale));

    ModelNode wallShape1 = new ModelNode("Two Triangles(Wall 1)", twoTriangles);

    // Window
    shader = new Shader(gl, "vs_tt.txt", "fs_tt_transparent.txt");
    twoTriangles = new Model(gl, camera, light, shader, material, modelMatrix, mesh, windowTextureId);
    NameNode roomWindow = new NameNode("window");
    transform = new Vec3(0f, 0.5f, -0.5f);
    windowTransform = new TransformNode("Transform Window", Mat4Transform.translate(transform));

    rotate = 90f;
    windowRotate =  new TransformNode("Rotate Window", Mat4Transform.rotateAroundX(rotate));

    scale = new Vec3(1f, 1f, 1f);
    windowScale = new TransformNode("Scale Window", Mat4Transform.scale(scale));

    ModelNode windowShape = new ModelNode("Two Triangles(Window)", twoTriangles);


    root.addChild(roomScale);
      roomScale.addChild(roomFloor);
        roomFloor.addChild(floorTransform);
          roomScale.addChild(floorScale);
          floorScale.addChild(floorShape);
      roomScale.addChild(roomWall0);
        roomWall0.addChild(wallTransform0);
          wallTransform0.addChild(wallRotate0);
          wallRotate0.addChild(wallScale0);
          wallScale0.addChild(wallShape0);
      roomScale.addChild(roomWall1);
        roomWall1.addChild(wallTransform1);
          wallTransform1.addChild(wallRotate1);
          wallRotate1.addChild(wallScale1);
          wallScale1.addChild(wallShape1);
      roomScale.addChild(roomWindow);
        roomWindow.addChild(windowTransform);
          windowTransform.addChild(windowRotate);
          windowRotate.addChild(windowScale);
          windowScale.addChild(windowShape);

    root.update();

    return root;
  }

}