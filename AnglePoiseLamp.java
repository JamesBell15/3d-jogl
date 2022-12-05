import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class AnglePoiseLamp extends Scene {

  private Texture lampTextureId;

  public AnglePoiseLamp(GL3 gl,
              Camera camera,
              Light[] lights,
              GlobalLight[] globalLights,
              SpotLight[] spotLights,
              Texture lampTextureId,
              SpotLight spotlight,
              Vec3 baseTransformation,
              Vec3 scaleLamp,
              float lowerArmRotationY,
              float lowerArmRotationZ,
              float jointRotationZ,
              float headRotationZ
            ){
    super(gl, camera, lights, globalLights, spotLights);
    this.lampTextureId = lampTextureId;
    this.baseTransformation = baseTransformation;
    this.spotlight = spotlight;
    this.scaleLamp = scaleLamp;
    this.lowerArmRotationY = lowerArmRotationY;
    this.lowerArmRotationZ = lowerArmRotationZ;
    this.jointRotationZ = jointRotationZ;
    this.headRotationZ = headRotationZ;

  }

  private Model cube, sphere, light;

  private NameNode base, lowerArm, joint, upperArm, head, spotLight;

  private TransformNode lampTransform, baseTransform, baseArmTransform, lowerArmTransform,
                        jointTransform, upperArmTransform, headTransform, spotLightTransform;

  private Vec3 baseTransformation, scaleLamp;
  private float lowerArmRotationY, lowerArmRotationZ, jointRotationZ, headRotationZ;

  private TransformNode lampScale, baseScale, lowerArmScale, jointScale,
                        upperArmScale, headScale, spotLightScale;

  private TransformNode lowerArmRotateY, lowerArmRotateZ, jointRotateZ, headRotateZ;

  private ModelNode baseShape, lowerArmShape, jointShape, upperArmShape, headShape;

  private SpotLight spotlight;

  private SpotLightNode spotLightShape;

  private SpotLightAnchorNode spotLightAnchorNode;

  public void update_rotations(float[] rotations) {

    // Lower arm rotation Y
    if (lowerArmRotationY < rotations[0]) {
      lowerArmRotationY += 1;
      lowerArmRotateY.setTransform(Mat4Transform.rotateAroundY(lowerArmRotationY));
      lowerArmRotateY.update();
    } else if (lowerArmRotationY > rotations[0]) {
      lowerArmRotationY -= 1;
      lowerArmRotateY.setTransform(Mat4Transform.rotateAroundY(lowerArmRotationY));
      lowerArmRotateY.update();
    }

    // Lower arm rotation Z
    if (lowerArmRotationZ < rotations[1]) {
      lowerArmRotationZ += 1;
      lowerArmRotateZ.setTransform(Mat4Transform.rotateAroundZ(lowerArmRotationZ));
      lowerArmRotateZ.update();
    } else if (lowerArmRotationZ > rotations[1]) {
      lowerArmRotationZ -= 1;
      lowerArmRotateZ.setTransform(Mat4Transform.rotateAroundZ(lowerArmRotationZ));
      lowerArmRotateZ.update();
    }

    // Joint rotation Z
    if (jointRotationZ < rotations[2]) {
      jointRotationZ += 1;
      jointRotateZ.setTransform(Mat4Transform.rotateAroundZ(jointRotationZ));
      jointRotateZ.update();
    } else if (jointRotationZ > rotations[2]) {
      jointRotationZ -= 1;
      jointRotateZ.setTransform(Mat4Transform.rotateAroundZ(jointRotationZ));
      jointRotateZ.update();
    }

    // Head rotation Z
    if (headRotationZ < rotations[3]) {
      headRotationZ += 1;
      headRotateZ.setTransform(Mat4Transform.rotateAroundZ(headRotationZ));
      headRotateZ.update();
    } else if (headRotationZ > rotations[3]) {
      headRotationZ -= 1;
      headRotateZ.setTransform(Mat4Transform.rotateAroundZ(headRotationZ));
      headRotateZ.update();
    }
  }

  public SGNode get_scene_graph(){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_light_01.txt", "fs_light_01.txt");

    Material material = new Material(
                                      new Vec3(0.5f, 0.5f, 0.5f),
                                      new Vec3(0.5f, 0.5f, 0.5f),
                                      new Vec3(0.3f, 0.3f, 0.3f),
                                      32.0f
                                    );
    Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);
    light = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTextureId);

    shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");

    cube = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTextureId);

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

    sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTextureId);

    root = new NameNode("root");

      lampTransform = new TransformNode("Transform Lamp", Mat4Transform.translate(baseTransformation));

      lampScale = new TransformNode("Scale Lamp", Mat4Transform.scale(scaleLamp));

    base = new NameNode("base");

      Vec3 transform = new Vec3(0f, 0f, 0f);
      baseTransform = new TransformNode("Transform base", Mat4Transform.translate(transform));

      Vec3 scale = new Vec3(1f, 0.5f, 0.5f);
      baseScale = new TransformNode("Scale Base", Mat4Transform.scale(scale));

    baseShape = new ModelNode("Cube Base", cube);

      transform = new Vec3(0f, 0f, 0f);
      baseArmTransform = new TransformNode("Transform base arm", Mat4Transform.translate(transform));

      lowerArmRotateY =  new TransformNode("Rotate Y lower arm", Mat4Transform.rotateAroundY(lowerArmRotationY));

      lowerArmRotateZ =  new TransformNode("Rotate Z lower arm", Mat4Transform.rotateAroundZ(lowerArmRotationZ));

    lowerArm = new NameNode("lower arm");

      transform = new Vec3(0f, 1f, 0f);
      lowerArmTransform = new TransformNode("Transform Lower Arm", Mat4Transform.translate(transform));

      scale = new Vec3(0.25f, 2f, 0.25f);
      lowerArmScale = new TransformNode("Scale Lower Arm", Mat4Transform.scale(scale));

      lowerArmShape = new ModelNode("Sphere Lower Arm", sphere);

    joint = new NameNode("joint");

      jointRotateZ =  new TransformNode("Rotate Z joint", Mat4Transform.rotateAroundZ(jointRotationZ));

      transform = new Vec3(0f, 1f, 0f);
      jointTransform = new TransformNode("Transform joint", Mat4Transform.translate(transform));

      scale = new Vec3(0.3f, 0.3f, 0.3f);
      jointScale = new TransformNode("Scale joint", Mat4Transform.scale(scale));

      jointShape = new ModelNode("Sphere joint", sphere);

    upperArm = new NameNode("upper arm");

      transform = new Vec3(0f, 1f, 0f);
      upperArmTransform = new TransformNode("Transform upper Arm", Mat4Transform.translate(transform));

      scale = new Vec3(0.25f, 2f, 0.25f);
      upperArmScale = new TransformNode("Scale upper Arm", Mat4Transform.scale(scale));

      upperArmShape = new ModelNode("Sphere upper Arm", sphere);

      headRotateZ =  new TransformNode("Rotate Z head", Mat4Transform.rotateAroundZ(headRotationZ));

    head = new NameNode("head");

      transform = new Vec3(0f, 2f, 0f);
      headTransform = new TransformNode("Transform head", Mat4Transform.translate(transform));

      spotLightAnchorNode = new SpotLightAnchorNode("SpotLight anchor", spotlight);

      scale = new Vec3(0.5f, 0.2f, 0.25f);
      headScale = new TransformNode("Scale head", Mat4Transform.scale(scale));

    headShape = new ModelNode("Cube head", cube);

      transform = new Vec3(-0.25f, 0f, 0f);
      spotLightTransform = new TransformNode("Transform spotLight", Mat4Transform.translate(transform));

      scale = new Vec3(0.2f, 0.15f, 0.15f);
      spotLightScale = new TransformNode("Scale spotLight", Mat4Transform.scale(scale));

      spotLightShape = new SpotLightNode("Light spotlight", spotlight);


    root.addChild(lampTransform);
      lampTransform.addChild(lampScale);
        lampScale.addChild(base);
        base.addChild(baseTransform);
          baseTransform.addChild(baseScale);
            baseScale.addChild(baseShape);
        base.addChild(baseArmTransform);
          baseArmTransform.addChild(lowerArmRotateY);
            lowerArmRotateY.addChild(lowerArmRotateZ);
              lowerArmRotateZ.addChild(lowerArm);
                lowerArm.addChild(lowerArmTransform);
                  lowerArmTransform.addChild(lowerArmScale);
                    lowerArmScale.addChild(lowerArmShape);
                  lowerArmTransform.addChild(joint);
                    joint.addChild(jointTransform);
                      jointTransform.addChild(jointRotateZ);
                        jointRotateZ.addChild(jointScale);
                          jointScale.addChild(jointShape);
                        jointRotateZ.addChild(upperArm);
                          upperArm.addChild(upperArmTransform);
                            upperArmTransform.addChild(upperArmScale);
                              upperArmScale.addChild(upperArmShape);
                          upperArm.addChild(head);
                            head.addChild(headTransform);
                              headTransform.addChild(headRotateZ);
                              headRotateZ.addChild(spotLightAnchorNode);
                                spotLightAnchorNode.addChild(headScale);
                                  headScale.addChild(headShape);
                                spotLightAnchorNode.addChild(spotLightTransform);
                                  spotLightTransform.addChild(spotLightScale);
                                    spotLightScale.addChild(spotLightShape);


    root.update();

    return root;
  }

}