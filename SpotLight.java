import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class SpotLight extends Light {

  private Vec3 direction;
  private float cutOff;
  private float outerCutOff;
  private float constant = 0.01f;
  private float linear = 0.0008f;
  private float quadratic = 0.000032f;

  public SpotLight(GL3 gl, Vec3 direction, float cutOff, float outerCutOff) {
    super(gl);

    this.direction = direction;
    this.cutOff = cutOff;
    this.outerCutOff = outerCutOff;
  }

  public Vec3 getDirection(){
    return direction;
  }
  public float getCutOff(){
    return cutOff;
  }
  public float getOuterCutOff(){
    return outerCutOff;
  }

  public void setDirection(Vec3 d){
    this.direction = d;
  }
  public void setCutOff(float f){
    this.cutOff = f;
  }
  public void setOuterCutOff(float f){
    this.outerCutOff = f;
  }
}