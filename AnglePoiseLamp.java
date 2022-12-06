import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class AnglePoiseLamp extends Scene {

  private Texture lampTexture;
  private boolean typeOfLamp;

  public AnglePoiseLamp(GL3 gl,
              Camera camera,
              Light[] lights,
              GlobalLight[] globalLights,
              SpotLight[] spotLights,
              Texture lampTexture,
              SpotLight spotlight,
              Vec3 baseTransformation,
              Vec3 scaleLamp,
              float lowerArmRotationY,
              float lowerArmRotationZ,
              float jointRotationZ,
              float headRotationZ,
              boolean typeOfLamp
            ){
    super(gl, camera, lights, globalLights, spotLights);
    this.lampTexture = lampTexture;
    this.baseTransformation = baseTransformation;
    this.spotlight = spotlight;
    this.scaleLamp = scaleLamp;
    this.lowerArmRotationY = lowerArmRotationY;
    this.lowerArmRotationZ = lowerArmRotationZ;
    this.jointRotationZ = jointRotationZ;
    this.headRotationZ = headRotationZ;
    this.typeOfLamp = typeOfLamp;
  }

  private Model cube, sphere, light;

  private NameNode base, lowerArm, joint, upperArm, head, spotLight, eyes, growth;

  private TransformNode lampTransform, baseTransform, baseArmTransform, lowerArmTransform,
                        jointTransform, upperArmTransform, headTransform, spotLightTransform;


  // eye: [stem, ball]
  private TransformNode[][] eyeTransforms = {new TransformNode[2], new TransformNode[2]};
  // stem rotations
  private TransformNode[] eyeRotations = new TransformNode[2];
  // stem, ball scales
  private TransformNode[][] eyeScales = {new TransformNode[2], new TransformNode[2]};
  private ModelNode[][] eyeShapes = {new ModelNode[2], new ModelNode[2]};

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
    light = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTexture);

    shader = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");

    cube = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTexture);

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

    sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, lampTexture);

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

    if (typeOfLamp) {

      // stem left eye
        transform = new Vec3(0f, 0.25f, 0f);
        eyeTransforms[0][0] = new TransformNode("Transform left eye stem", Mat4Transform.translate(transform));

        float rotate = 30f;
        eyeRotations[0] =  new TransformNode("Rotate X left eye stem", Mat4Transform.rotateAroundX(rotate));

        scale = new Vec3(0.1f, 0.75f, 0.1f);
        eyeScales[0][0] = new TransformNode("Scale left eye stem", Mat4Transform.scale(scale));

        eyeShapes[0][0] = new ModelNode("Sphere left eye stem", sphere);
      // ball left eye
        transform = new Vec3(0f, 0.5f, 0f);
        eyeTransforms[0][1] = new TransformNode("Transform left eye ball", Mat4Transform.translate(transform));

        scale = new Vec3(0.25f, 0.25f, 0.25f);
        eyeScales[0][1] = new TransformNode("Scale left eye ball", Mat4Transform.scale(scale));

        eyeShapes[0][1] = new ModelNode("Sphere left eye ball", sphere);

      // stem right eye
        transform = new Vec3(0f, 0.25f, 0f);
        eyeTransforms[1][0] = new TransformNode("Transform right eye stem", Mat4Transform.translate(transform));

        rotate = -30f;
        eyeRotations[1] =  new TransformNode("Rotate X right eye stem", Mat4Transform.rotateAroundX(rotate));

        scale = new Vec3(0.1f, 0.75f, 0.1f);
        eyeScales[1][0] = new TransformNode("Scale right eye stem", Mat4Transform.scale(scale));

        eyeShapes[1][0] = new ModelNode("Sphere right eye stem", sphere);
      // ball right eye
        transform = new Vec3(0f, 0.5f, 0f);
        eyeTransforms[1][1] = new TransformNode("Transform right eye ball", Mat4Transform.translate(transform));

        scale = new Vec3(0.25f, 0.25f, 0.25f);
        eyeScales[1][1] = new TransformNode("Scale right eye ball", Mat4Transform.scale(scale));

        eyeShapes[1][1] = new ModelNode("Sphere right eye ball", sphere);

      headRotateZ.addChild(eyeRotations[0]);
        eyeRotations[0].addChild(eyeTransforms[0][0]);
          eyeTransforms[0][0].addChild(eyeScales[0][0]);
            eyeScales[0][0].addChild(eyeShapes[0][0]);
        eyeRotations[0].addChild(eyeTransforms[0][1]);
          eyeTransforms[0][1].addChild(eyeScales[0][1]);
            eyeScales[0][1].addChild(eyeShapes[0][1]);

      headRotateZ.addChild(eyeRotations[1]);
        eyeRotations[1].addChild(eyeTransforms[1][0]);
          eyeTransforms[1][0].addChild(eyeScales[1][0]);
            eyeScales[1][0].addChild(eyeShapes[1][0]);
        eyeRotations[1].addChild(eyeTransforms[1][1]);
          eyeTransforms[1][1].addChild(eyeScales[1][1]);
            eyeScales[1][1].addChild(eyeShapes[1][1]);

    } else {

    }


    root.update();

    return root;
  }

}