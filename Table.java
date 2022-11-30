import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class Table extends Scene{
  private Texture tableTextureId;

  public Table(GL3 gl,
              Camera camera,
              Light[] lights,
              GlobalLight[] globalLights,
              SpotLight[] spotLights,
              Texture tableTextureId
            ){
    super(gl, camera, lights, globalLights, spotLights);
    this.tableTextureId = tableTextureId;
  }

  private SGNode root;
  private Model cube;

  private NameNode surface, legs;

  private TransformNode tableTransform, surfaceTransform;
  private TransformNode[] legTransforms = new TransformNode[4];

  private TransformNode tableScale, surfaceScale;
  private TransformNode[] legScales = new TransformNode[4];

  private ModelNode surfaceShape;
  private ModelNode[] legShapes = new ModelNode[4];



  public SGNode get_scene_graph(){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
    Material material = new Material(
                                      new Vec3(0.0f, 0.5f, 0.81f),
                                      new Vec3(0.0f, 0.5f, 0.81f),
                                      new Vec3(0.3f, 0.3f, 0.3f),
                                      32.0f
                                    );
    Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);
    cube = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, tableTextureId);

    root = new NameNode("root");

    Vec3 transform = new Vec3(0f, 0f, 0f);
    tableTransform = new TransformNode("Transform Table", Mat4Transform.translate(transform));

    Vec3 scale = new Vec3(1f, 1f, 1f);
    tableScale = new TransformNode("Scale Table", Mat4Transform.scale(scale));

    surface = new NameNode("surface");

    transform = new Vec3(0.5f, 1f, 0.5f);
    surfaceTransform = new TransformNode("Transform Surface", Mat4Transform.translate(transform));

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


    root.addChild(tableTransform);
      tableTransform.addChild(tableScale);
        tableScale.addChild(surface);
          surface.addChild(surfaceTransform);
          surfaceTransform.addChild(surfaceScale);
          surfaceScale.addChild(surfaceShape);
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