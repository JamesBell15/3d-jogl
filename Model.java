import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class Model {
  
  private Mesh mesh;
  private Texture textureId1;
  private Texture textureId2;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light[] lights;
  private DirectionLight[] directionLights;
  
  public Model(GL3 gl, Camera camera, Light[] lights, DirectionLight[] directionLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1, Texture textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.lights = lights;
    this.directionLights = directionLights;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }
  
  public Model(GL3 gl, Camera camera, Light[] lights, DirectionLight[] directionLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1) {
    this(gl, camera, lights, directionLights, shader, material, modelMatrix, mesh, textureId1, null);
  }
  
  public Model(GL3 gl, Camera camera, Light[] lights, DirectionLight[] directionLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, lights, directionLights, shader, material, modelMatrix, mesh, null, null);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLights(Light[] lights) {
    this.lights = lights;
  }

  public void setDirectionLights(DirectionLight[] directionLights) {
    this.directionLights = directionLights;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    // gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    // dont use will break everything
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    // shader.setInt(gl, "numberOfLights", lights.length);
    for (int i = 0; i < lights.length; i++) {
      shader.setVec3(gl, String.format("lights[{0}].position", i), lights[i].getPosition());
      shader.setVec3(gl, String.format("lights[{0}].ambient", i), lights[i].getMaterial().getAmbient());
      shader.setVec3(gl, String.format("lights[{0}].diffuse", i), lights[i].getMaterial().getDiffuse());
      shader.setVec3(gl, String.format("lights[{0}].specular", i), lights[i].getMaterial().getSpecular());
    }

    // shader.setInt(gl, "numberOfDirLights", directionLights.length);
    for (int i = 0; i < directionLights.length; i++) {
      shader.setVec3(gl, String.format("directionLights[{0}].position", i), directionLights[i].getPosition());
      shader.setVec3(gl, String.format("directionLights[{0}].ambient", i), directionLights[i].getMaterial().getAmbient());
      shader.setVec3(gl, String.format("directionLights[{0}].diffuse", i), directionLights[i].getMaterial().getDiffuse());
      shader.setVec3(gl, String.format("directionLights[{0}].specular", i), directionLights[i].getMaterial().getSpecular());
      shader.setVec3(gl, String.format("directionLights[{0}].direction", i), directionLights[i].getDirection());
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());  

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      textureId1.bind(gl);
    }
    // if (textureId2!=null) {
    //   shader.setInt(gl, "second_texture", 1);
    //   gl.glActiveTexture(GL.GL_TEXTURE1);
    //   textureId2.bind(gl);
    // }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) textureId1.destroy(gl);
    if (textureId2!=null) textureId2.destroy(gl);
  }
  
}