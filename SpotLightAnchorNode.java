import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class SpotLightAnchorNode extends SGNode {

  protected SpotLight light;
  private Vec3 orignalPosition;

  public SpotLightAnchorNode(String name, SpotLight l) {
    super(name);
    this.light = l;
    this.orignalPosition = l.getPosition();
  }

  private Vec3 getNewDirection(){
    float[][] values = worldTransform.getValues();

    Vec3 newPos = new Vec3(
      orignalPosition.x - values[0][3],
      orignalPosition.y - values[1][3],
      orignalPosition.z - values[2][3]
    );

    return newPos;
  }

  protected void update(Mat4 t) {
    super.update(t);
    light.setDirection(getNewDirection());
  }

  public void draw(GL3 gl) {
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

}