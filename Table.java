import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class Table extends Scene{
  private Texture tableTexture;
  private Texture[] eggTextures;

  public Table(GL3 gl,
              Camera camera,
              Light[] lights,
              GlobalLight[] globalLights,
              SpotLight[] spotLights,
              Texture tableTexture,
              Texture[] eggTextures
            ){
    super(gl, camera, lights, globalLights, spotLights);
    this.tableTexture = tableTexture;
    this.eggTextures = eggTextures;
  }

  private SGNode root;
  private Model cube, sphere;

  private NameNode surface, legs, egg, pedestal;

  private TransformNode tableTransform, surfaceTransform, eggTransform, pedestalTransform;
  private TransformNode[] legTransforms = new TransformNode[4];

  private TransformNode eggRotateY;

  private TransformNode tableScale, surfaceScale, eggScale, pedestalScale;
  private TransformNode[] legScales = new TransformNode[4];

  private ModelNode surfaceShape, eggShape, pedestalShape;
  private ModelNode[] legShapes = new ModelNode[4];


  private Vec3 eggTransformVec3 = new Vec3(0f, 1f, 0f);
  private Vec3 surfaceTransformVec3 = new Vec3(0.5f, 1f, 0.5f);
  private Vec3 eggScaleVec3 = new Vec3(1f, 2f, 1f);

  private int step = 0;
  private int maxStep = 100;

  public void update_egg(float time){

    if (((time%20) > 1) && (step == 0)) return;

    float jump = (float)(Math.abs(2*Math.sin(3.14*((float)step/(float)maxStep))));

    Vec3 transform = new Vec3(
      0f,
      jump,
      0f
    );

    float rotate = (float)(360*jump);


    eggRotateY.setTransform(Mat4.multiply(
        Mat4Transform.translate(transform),
        Mat4Transform.rotateAroundY(rotate)
      )
    );
    eggRotateY.update();

    step++;

    if (step >= maxStep) {
      step = 0;
    }
    // eggRotateY.update();

    // eggRotateY.setTransform(Mat4Transform.rotateAroundY(time));
  }

  public SGNode get_scene_graph(){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
    Material material = new Material(
                                      new Vec3(0.5f, 0.5f, 0.5f),
                                      new Vec3(0.5f, 0.5f, 0.5f),
                                      new Vec3(0.3f, 0.3f, 0.3f),
                                      32.0f
                                    );
    Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);
    cube = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, tableTexture);

    root = new NameNode("root");

      Vec3 transform = new Vec3(-0.5f, 1f, -0.5f);
      tableTransform = new TransformNode("Transform Table", Mat4Transform.translate(transform));

      Vec3 scale = new Vec3(1.5f, 1.5f, 1.5f);
      tableScale = new TransformNode("Scale Table", Mat4Transform.scale(scale));

    surface = new NameNode("surface");

      surfaceTransform = new TransformNode("Transform Surface", Mat4Transform.translate(surfaceTransformVec3));

      scale = new Vec3(2f, 0.5f, 2f);
      surfaceScale = new TransformNode("Scale Surface", Mat4Transform.scale(scale));

      surfaceShape = new ModelNode("Cube Surface", cube);

    legs = new NameNode("legs");

      transform = new Vec3(0f, 0f, 0f);
      legTransforms[0] = new TransformNode("Transform Leg 0", Mat4Transform.translate(transform));

      transform = new Vec3(1f, 0f, 0f);
      legTransforms[1] = new TransformNode("Transform Leg 1", Mat4Transform.translate(transform));

      transform = new Vec3(0f, 0f, 1f);
      legTransforms[2] = new TransformNode("Transform Leg 2", Mat4Transform.translate(transform));

      transform = new Vec3(1f, 0f, 1f);
      legTransforms[3] = new TransformNode("Transform Leg 3", Mat4Transform.translate(transform));

      scale = new Vec3(0.25f, 2f, 0.25f);
      legScales[0] = new TransformNode("Scale Leg 0", Mat4Transform.scale(scale));
      legScales[1] = new TransformNode("Scale Leg 1", Mat4Transform.scale(scale));
      legScales[2] = new TransformNode("Scale Leg 2", Mat4Transform.scale(scale));
      legScales[3] = new TransformNode("Scale Leg 3", Mat4Transform.scale(scale));

      legShapes[0] = new ModelNode("Cube Leg 0", cube);
      legShapes[1] = new ModelNode("Cube Leg 1", cube);
      legShapes[2] = new ModelNode("Cube Leg 2", cube);
      legShapes[3] = new ModelNode("Cube Leg 3", cube);

    pedestal = new NameNode("pedestal");

      transform = new Vec3(0f, 0.3f, 0f);
      pedestalTransform = new TransformNode("Transform Pedestal", Mat4Transform.translate(transform));

      scale = new Vec3(1.25f, 0.25f, 1.25f);
      pedestalScale = new TransformNode("Scale Pedestal", Mat4Transform.scale(scale));

      pedestalShape = new ModelNode("Cube Pedestal", cube);

    egg = new NameNode("egg");

      eggTransform = new TransformNode("Transform Egg", Mat4Transform.translate(eggTransformVec3));

      eggScale = new TransformNode("Scale Egg", Mat4Transform.scale(eggScaleVec3));

      float rotate = 0f;
      eggRotateY = new TransformNode("Rotate Egg", Mat4Transform.rotateAroundY(rotate));

      mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

      sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, eggTextures[0], eggTextures[1]);

      eggShape = new ModelNode("Sphere egg", sphere);



    root.addChild(tableTransform);
      tableTransform.addChild(tableScale);
        tableScale.addChild(surface);
          surface.addChild(surfaceTransform);
          surfaceTransform.addChild(surfaceScale);
          surfaceScale.addChild(surfaceShape);
          surfaceTransform.addChild(pedestal);
            pedestal.addChild(pedestalTransform);
              pedestalTransform.addChild(pedestalScale);
                pedestalScale.addChild(pedestalShape);
          surfaceTransform.addChild(egg);
            egg.addChild(eggTransform);
              eggTransform.addChild(eggScale);
                eggScale.addChild(eggRotateY);
                  eggRotateY.addChild(eggShape);
        tableScale.addChild(legs);
          legs.addChild(legTransforms[0]);
          legTransforms[0].addChild(legScales[0]);
          legScales[0].addChild(legShapes[0]);

          legs.addChild(legTransforms[1]);
          legTransforms[1].addChild(legScales[1]);
          legScales[1].addChild(legShapes[1]);

          legs.addChild(legTransforms[2]);
          legTransforms[2].addChild(legScales[2]);
          legScales[2].addChild(legShapes[2]);

          legs.addChild(legTransforms[3]);
          legTransforms[3].addChild(legScales[3]);
          legScales[3].addChild(legShapes[3]);



    root.update();

    return root;
  }

}