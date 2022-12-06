import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/* I declare that this code is my own work */
/* Author James Bell jbell15@sheffield.ac.uk */

public class Hatch extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Hatch_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  public static void main(String[] args) {
    Hatch b1 = new Hatch("Hatch");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Hatch(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Hatch_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);
    
    JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

      JButton b = new JButton("Global Light 1 toggle");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Global Light 2 toggle");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Spot Light 1 toggle");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Spot Light 2 toggle");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 1 pose 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 1 pose 2");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 1 pose 3");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 2 pose 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 2 pose 2");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Lamp 2 pose 3");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.EAST);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("Global Light 1 toggle")) {
      glEventListener.toggleGlobalLight(0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Global Light 2 toggle")) {
      glEventListener.toggleGlobalLight(1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Spot Light 1 toggle")) {
      glEventListener.toggleSpotLight(0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Spot Light 2 toggle")) {
      glEventListener.toggleSpotLight(1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 1 pose 1")) {
      glEventListener.setLampRotations(0, 0f, -10.0f, 10f, 20f);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 1 pose 2")) {
      glEventListener.setLampRotations(0, 0f, -60.0f, 130f, -90f);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 1 pose 3")) {
      glEventListener.setLampRotations(0, -100f, -60.0f, 130f, -60f);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 2 pose 1")) {
      glEventListener.setLampRotations(1, 180f, -60.0f, 90f, 20f);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 2 pose 2")) {
      glEventListener.setLampRotations(1, 180f, -30.0f, 90f, -80f);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Lamp 2 pose 3")) {
      glEventListener.setLampRotations(1,  90f, 30.0f, 40f, -20f);
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }
  
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}