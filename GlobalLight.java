import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
  
public class GlobalLight {

  private Vec3 direction, ambient, diffuse, specular;
  private boolean toggle;
    
  public GlobalLight(Vec3 direction, Vec3 ambient, Vec3 diffuse, Vec3 specular, boolean toggle) {
    this.direction = direction;
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.toggle = toggle;
  }

  public Vec3 getDirection(){
    return direction;
  }
  public boolean getToggle(){
    return toggle;
  }
  public Vec3 getAmbient(){
    if (toggle) return ambient;

    return new Vec3(0f, 0f, 0f);
  }
  public Vec3 getDiffuse(){
    if (toggle) return diffuse;

    return new Vec3(0f, 0f, 0f);
  }
  public Vec3 getSpecular(){
    if (toggle) return specular;

    return new Vec3(0f, 0f, 0f);
  }

  public void setDirection(Vec3 d){
    this.direction = d;
  }
  public void setAmbient(Vec3 a){
    this.ambient = a;
  }
  public void setDiffuse(Vec3 d){
    this.diffuse = d;
  }
  public void setSpecular(Vec3 s){
    this.specular = s;
  }
  public void setToggle(boolean b){
    this.toggle = b;
  }
}