import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class DirectionLight extends Light {

  private Vec3 direction;

  public DirectionLight(GL3 gl) {
    super(gl);
  }

  public void setDirection(Vec3 v) {
    this.direction.x = v.x;
    this.direction.y = v.y;
    this.direction.z = v.z;
  }

  public void setDirection(float x, float y, float z) {
    this.direction.x = x;
    this.direction.y = y;
    this.direction.z = z;
  }

  public Vec3 getDirection() {
    return this.direction;
  }
}