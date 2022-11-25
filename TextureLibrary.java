import java.io.File;
import java.io.FileInputStream;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public final class TextureLibrary {

  public static Texture loadTexture(GL3 gl3, String filename) {
    Texture t = null;
    try {
      File f = new File(filename);
      t = (Texture)TextureIO.newTexture(f, true);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
      t.bind(gl3);
    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename);
    }
    return t;
  }
}

