import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
  
public class GlobalLight {

  private Vec3 direction, ambient, diffuse, specular;
    
  public GlobalLight(Vec3 direction, Vec3 ambient, Vec3 diffuse, Vec3 specular) {
    this.direction = direction;
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
  }

  public Vec3 getDirection(){
    return direction;
  }
  public Vec3 getAmbient(){
    return ambient;
  }
  public Vec3 getDiffuse(){
    return diffuse;
  }
  public Vec3 getSpecular(){
    return specular;
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
}