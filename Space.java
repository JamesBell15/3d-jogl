import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class Space extends Scene {
  private Texture spaceTexture, planetTexture, ringTexture;

  public Space(GL3 gl,
              Camera camera,
              Light[] lights,
              GlobalLight[] globalLights,
              SpotLight[] spotLights,
              Texture spaceTexture,
              Texture planetTexture,
              Texture ringTexture
            ){
    super(gl, camera, lights, globalLights, spotLights);
    this.spaceTexture = spaceTexture;
    this.planetTexture = planetTexture;
    this.ringTexture = ringTexture;
  }

  private SGNode root, edgeOfSpace, planet, ring;
  private Model twoTriangles, sphere;
  // space nodes
  private TransformNode spaceScale, spaceTransform;
  // edges
  private TransformNode[] edgeOfSpaceTransforms = new TransformNode[5];
  private TransformNode[] edgeOfSpaceRotations = new TransformNode[5];
  private ModelNode[] edgeOfSpaceShapes = new ModelNode[5];

  // planet
  private TransformNode planetScale;
  private TransformNode planetTransform;
  private TransformNode planetRotation;
  private ModelNode planetShape;

  // ring
  private TransformNode ringRotationZ, ringRotationY;
  private TransformNode ringScale;
  private ModelNode ringShape;

  public void animate_planet(float time){
    float rotateRing = (float)(2*time);
    float rotatePlanet = (float)(time);


    ringRotationY.setTransform(Mat4Transform.rotateAroundY(rotateRing));
    ringRotationY.update();

    planetRotation.setTransform(Mat4Transform.rotateAroundY(rotatePlanet));
    planetRotation.update();
  }

  public SGNode get_scene_graph(){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt.txt", "fs_spotlight.txt");
    Material material = new Material(
                                      new Vec3(1f, 1f, 1f),
                                      new Vec3(1f, 1f, 1f),
                                      new Vec3(0.3f, 0.3f, 0.3f),
                                      100.0f
                                    );
    Mat4 modelMatrix = Mat4Transform.scale(1f, 1f, 1f);

    // space
    root = new NameNode("root");

    Vec3 transform = new Vec3(0f, -30f, -40f);
    spaceTransform = new TransformNode("Transform Space", Mat4Transform.translate(transform));

    float scaleFactor = 64f;
    Vec3 scale = new Vec3(scaleFactor, scaleFactor, scaleFactor);
    spaceScale = new TransformNode("Scale space", Mat4Transform.scale(scale));

    twoTriangles = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, spaceTexture);

    NameNode edgeOfSpace = new NameNode("edge of space");

    // Bottom
      transform = new Vec3(0f, 0f, 0f);
      edgeOfSpaceTransforms[0] = new TransformNode("Transform Bottom", Mat4Transform.translate(transform));

      float rotate = 0f;
      edgeOfSpaceRotations[0] = new TransformNode("Rotate Bottom", Mat4Transform.rotateAroundX(rotate));

      edgeOfSpaceShapes[0] = new ModelNode("Two Triangles Bottom", twoTriangles);

    // Left
      transform = new Vec3(-0.5f, 0.5f, 0f);
      edgeOfSpaceTransforms[1] = new TransformNode("Transform Left", Mat4Transform.translate(transform));

      rotate = -90f;
      edgeOfSpaceRotations[1] = new TransformNode("Rotate Left", Mat4Transform.rotateAroundZ(rotate));

      edgeOfSpaceShapes[1] = new ModelNode("Two Triangles Left", twoTriangles);

    // Right
      transform = new Vec3(0.5f, 0.5f, 0f);
      edgeOfSpaceTransforms[2] = new TransformNode("Transform Right", Mat4Transform.translate(transform));

      rotate = 90f;
      edgeOfSpaceRotations[2] = new TransformNode("Rotate Right", Mat4Transform.rotateAroundZ(rotate));

      edgeOfSpaceShapes[2] = new ModelNode("Two Triangles Right", twoTriangles);

    // Back
      transform = new Vec3(0f, 0.5f, -0.5f);
      edgeOfSpaceTransforms[3] = new TransformNode("Transform Back", Mat4Transform.translate(transform));

      rotate = 90f;
      edgeOfSpaceRotations[3] = new TransformNode("Rotate Back", Mat4Transform.rotateAroundX(rotate));

      edgeOfSpaceShapes[3] = new ModelNode("Two Triangles Back", twoTriangles);

    // Top
      transform = new Vec3(0f, 1f, 0f);
      edgeOfSpaceTransforms[4] = new TransformNode("Transform Top", Mat4Transform.translate(transform));

      rotate = 180f;
      edgeOfSpaceRotations[4] = new TransformNode("Rotate Top", Mat4Transform.rotateAroundX(rotate));

      edgeOfSpaceShapes[4] = new ModelNode("Two Triangles Top", twoTriangles);

    // Planet
      planet = new NameNode("Planet");

      mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
      material = new Material(
                                new Vec3(1f, 1f, 1f),
                                new Vec3(1f, 1f, 1f),
                                new Vec3(0.3f, 0.3f, 0.3f),
                                1.0f
                              );
      sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, planetTexture);

      scale = new Vec3(0.3f, 0.3f, 0.3f);
      planetScale = new TransformNode("Transform Scale", Mat4Transform.scale(scale));

      transform = new Vec3(0.2f, 0.5f, -0.25f);
      planetTransform = new TransformNode("Transform Planet", Mat4Transform.translate(transform));

      rotate = 0f;
      planetRotation = new TransformNode("Transform Planet", Mat4Transform.rotateAroundY(rotate));

      planetShape = new ModelNode("Sphere Planet", sphere);

    // ring
      ring = new NameNode("Ring");

      rotate = 20f;
      ringRotationZ = new TransformNode("Rotate Ring Z", Mat4Transform.rotateAroundZ(rotate));

      rotate = 0f;
      ringRotationY = new TransformNode("Rotate Ring Y", Mat4Transform.rotateAroundY(rotate));

      scale = new Vec3(2f, 0.001f, 2f);
      ringScale = new TransformNode("Transform Ring", Mat4Transform.scale(scale));

      material = new Material(  new Vec3(0.3f, 0.3f, 0.3f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                1.0f
                              );
      sphere = new Model(gl, camera, lights, globalLights, spotLights, shader, material, modelMatrix, mesh, ringTexture);

      ringShape = new ModelNode("Sphere Ring", sphere);




    root.addChild(spaceTransform);
    spaceTransform.addChild(spaceScale);
    spaceScale.addChild(edgeOfSpace);

    for (int i = 0; i < edgeOfSpaceShapes.length; i++){
      edgeOfSpace.addChild(edgeOfSpaceTransforms[i]);
        edgeOfSpaceTransforms[i].addChild(edgeOfSpaceRotations[i]);
          edgeOfSpaceRotations[i].addChild(edgeOfSpaceShapes[i]);
    }

    spaceScale.addChild(planetTransform);
      planetTransform.addChild(planet);
        planet.addChild(planetScale);
          planetScale.addChild(planetRotation);
            planetRotation.addChild(planetShape);
          planetScale.addChild(ring);
            ring.addChild(ringRotationZ);
              ringRotationZ.addChild(ringRotationY);
                ringRotationY.addChild(ringScale);
                  ringScale.addChild(ringShape);

    root.update();

    return root;
  }

}