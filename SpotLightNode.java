import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class SpotLightNode extends SGNode {

  protected SpotLight light;
  private Vec3 anchorDirection, orignalPosition;

  public SpotLightNode(String name, SpotLight l) {
    super(name);
    this.light = l;
    this.orignalPosition = l.getPosition();
    this.anchorDirection = l.getDirection();
  }

  private Vec3 getNewPosition(){
    float[][] values = worldTransform.getValues();

    Vec3 newPos = new Vec3(
      values[0][3],
      values[1][3],
      values[2][3]
    );

    return newPos;
  }

  protected void update(Mat4 t) {
    super.update(t);
    light.setPosition(getNewPosition());
  }

  public void draw(GL3 gl) {
    light.render(gl, worldTransform);
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

}