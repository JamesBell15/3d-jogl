import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class Scene {
  protected GL3 gl;
  protected Camera camera;
  protected Light[] lights;
  protected DirectionLight[] directionLights;

  protected SGNode root;

  public Scene(GL3 gl,
              Camera camera,
              Light[] lights,
              DirectionLight[] directionLights){
    this.gl = gl;
    this.camera = camera;
    this.lights = lights;
    this.directionLights = directionLights;
  }

  public SGNode get_scene_graph(){
    return root;
  }

}