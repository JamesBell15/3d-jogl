import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
  
public class Light {
  
  protected Material material;
  protected Vec3 position;
  protected Mat4 model;
  protected Shader shader;
  protected Camera camera;
  protected float constant = 1f;
  protected float linear = 0.8f;
  protected float quadratic = 0.032f;
  protected boolean toggle;


  public Light(GL3 gl, boolean toggle) {
    this.toggle = toggle;
    material = new Material();
    material.setAmbient(0.5f, 0.5f, 0.5f);
    material.setDiffuse(0.8f, 0.8f, 0.8f);
    material.setSpecular(0.8f, 0.8f, 0.8f);
    position = new Vec3(0f, 0f, 0f);
    model = new Mat4(1);
    shader = new Shader(gl, "vs_light_01.txt", "fs_light_01.txt");
    fillBuffers(gl);
  }

  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }

  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }

  public Vec3 getPosition() {
    return position;
  }

  public void setMaterial(Material m) {
    material = m;
  }

  public void setToggle(boolean b) {
    this.toggle = b;
  }

  public boolean getToggle() {
    return toggle;
  }

  public Material getMaterial() {
    if (toggle) return material;

    return new Material(new Vec3(0f, 0f, 0f),
      new Vec3(0f, 0f, 0f),
      new Vec3(0f, 0f, 0f),
      0f
    );
  }

  public float getConstant() {
    return constant;
  }

  public float getLinear() {
    return linear;
  }

  public float getQuadratic() {
    return quadratic;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera() {
    return camera;
  }

  public Mat4 getModel() {
    return model;
  }
  
  public void render(GL3 gl) {
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
    model = Mat4.multiply(Mat4Transform.translate(position), model);
    
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));
    
    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

    // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  
    protected float[] vertices = new float[] {  // x,y,z
      -0.5f, -0.5f, -0.5f,  // 0
      -0.5f, -0.5f,  0.5f,  // 1
      -0.5f,  0.5f, -0.5f,  // 2
      -0.5f,  0.5f,  0.5f,  // 3
       0.5f, -0.5f, -0.5f,  // 4
       0.5f, -0.5f,  0.5f,  // 5
       0.5f,  0.5f, -0.5f,  // 6
       0.5f,  0.5f,  0.5f   // 7
     };
    
    protected int[] indices =  new int[] {
      0,1,3, // x -ve 
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      1,5,7, // z +ve
      7,3,1, // z +ve
      6,4,0, // z -ve
      0,2,6, // z -ve
      0,4,5, // y -ve
      5,1,0, // y -ve
      2,3,7, // y +ve
      7,6,2  // y +ve
    };
    
  protected int vertexStride = 3;
  protected int vertexXYZFloats = 3;
  
  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  protected int[] vertexBufferId = new int[1];
  protected int[] vertexArrayId = new int[1];
  protected int[] elementBufferId = new int[1];
    
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
     
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  } 

}