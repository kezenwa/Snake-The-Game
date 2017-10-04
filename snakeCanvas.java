import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
//import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.awt.event.*;

import javax.swing.JOptionPane;

public class snakeCanvas extends Applet implements Runnable,KeyListener,MouseListener
{
private LinkedList<Point> snake;

private final int BOX_WIDTH = 15;
private final int BOX_HEIGHT = 15;
private final int GRID_WIDTH = 25;
private final int GRID_HEIGHT = 25;

private Point fruit;
private Point bug;

private Thread runThread;

private int Direction=direction.NO_DIRECTION;

private int score = 0;
private String highScore = "Nobody:0";
private Image menuImage=null ;
private Image bugImage=null;
private Image introImage=null;
private Image HelpImage=null;
private Image endImage;

private boolean inMenu=false;
private boolean atEnd=false;
private boolean won=false;
private boolean inIntro=true;
private boolean inHelp=false;

int xpos,ypos;

AudioClip audio;

public void paint(Graphics g)
{
    if(runThread== null)
    {
    this.setPreferredSize(new Dimension (640,480));
    this.addKeyListener(this);
    this.addMouseListener(this);
    runThread=new Thread(this);
    runThread.start();
    }
    if(inIntro)
    {
        showIntro(g);
    }
    else if(inMenu)
    {
    DrawMenu(g);
    } 
    else if(inHelp)
    {
   DrawHelp(g);
    }    	
else if(atEnd)
    {
DrawEndGame(g);
    }
else
    {
    if (snake==null)
        {
    snake=new LinkedList<Point>();
            GenerateDefaultSnake();
            PlaceFruit();
            PlaceBug();
        }
        if(highScore.equals(""))
        {
            //initialise the highscore
            highScore = GetHighscore();
        }
        Draw(g);
    } 
}

public void showIntro(Graphics g)
{
    if(this.introImage==null)
    try
    {
         URL imagePath= snakeCanvas.class.getResource("startpage.png");
         this.introImage=Toolkit.getDefaultToolkit().getImage(imagePath);
    }
    catch(Exception e)
    {
    e.printStackTrace();
    }

    try
    {
        g.drawImage(introImage,0,0,640,480,this);
        Thread.sleep(100);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
}

public void GenerateDefaultSnake()
{
    score = 0;
    snake.clear();
    snake.add(new Point(0,3));
    snake.add(new Point(0,2));
    snake.add(new Point(0,1));
    Direction=direction.NO_DIRECTION;
}
public void Draw(Graphics g)
{    
    g.clearRect(0, 0, BOX_WIDTH*GRID_WIDTH + 40, BOX_HEIGHT*GRID_HEIGHT + 40);
    DrawGrid(g);
    DrawSnake(g);
    DrawBug(g);
    DrawFruit(g);
    DrawScore(g);
}

public void DrawScore(Graphics g)
{
g.setFont(new Font("ComicSansMs",Font.BOLD,15));
g.drawString("Score : "+score, 0, BOX_HEIGHT *GRID_HEIGHT +20);
g.drawString("Highscore : "+highScore, 0, BOX_HEIGHT *GRID_HEIGHT +40);
}

public void CheckScore()
{    
if(score > Integer.parseInt(highScore.split(":")[1]))
{
//new highscore set
String name = JOptionPane.showInputDialog("HighScoree!!! Please Enter your name");
highScore = name + ":" + score;
File scoreFile = new File("highscore.dat");
if(!scoreFile.exists())
{
try
{
scoreFile.createNewFile();
}
catch (IOException e)
{
e.printStackTrace();
}
}
FileWriter writeFile = null;
BufferedWriter writer = null;
try
{
writeFile = new FileWriter(scoreFile);
writer = new BufferedWriter(writeFile);
writer.write(this.highScore);
}
catch(Exception e)
{
//errors if file is not found
}
finally
{
if(writer != null)
{
try
{
writer.close();
}
catch (Exception e){}
}
}
}
}

public void DrawGrid(Graphics g)
{
setBackground(Color.WHITE);
    g.fillRect(0,0,GRID_WIDTH * BOX_WIDTH,GRID_HEIGHT *BOX_HEIGHT );
   /* //draw vertical lines
    for(int x=BOX_WIDTH;x< GRID_WIDTH * BOX_WIDTH; x+=BOX_WIDTH)
    {
        g.drawLine(x, 0, x, BOX_HEIGHT*GRID_HEIGHT);
    }
    //draw horizontal lines
    for(int y= BOX_HEIGHT;y<GRID_HEIGHT*BOX_HEIGHT;y+=BOX_HEIGHT)
    {
        g.drawLine(0, y, GRID_WIDTH*BOX_WIDTH, y);
    }*/
}
public void DrawSnake(Graphics g)
{
    g.setColor(Color.GREEN);
    for(Point p:snake)
        g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
    g.setColor(Color.BLACK);
}

public void DrawFruit(Graphics g)
{
    g.setColor(Color.RED);
    g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
    g.setColor(Color.BLACK);
}

public void DrawBug(Graphics g)
{
    if(this.bugImage==null)
        try
    {
            URL imagePath= snakeCanvas.class.getResource("projbug.png");
            this.bugImage=Toolkit.getDefaultToolkit().getImage(imagePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        g.drawImage(bugImage,bug.x* BOX_WIDTH,bug.y* BOX_HEIGHT,15,15,this);
    
    }

public void DrawMenu(Graphics g)
{
    if(this.menuImage==null)
    {
        try
        {
            URL imagePath= snakeCanvas.class.getResource("snakehomepage.png");
            this.menuImage=Toolkit.getDefaultToolkit().getImage(imagePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    g.drawImage(menuImage,0,0,640,480,this);
    g.fillRect(450, 50, 100, 50);
    g.fillRect(450, 125, 100, 50);
    g.setColor(Color.GREEN);
    g.setFont(new Font("ComicSansMs",Font.BOLD,26));
    g.drawString("START",458,80);
    g.drawString("HELP",465,155);
    g.setColor(Color.BLACK);
}

public void DrawHelp(Graphics g)
{
    if(this.HelpImage==null)
    {
        try
        {
            URL imagePath= snakeCanvas.class.getResource("instructions.png");
            this.HelpImage=Toolkit.getDefaultToolkit().getImage(imagePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    g.drawImage(HelpImage,0,0,640,480,this);
    g.setColor(Color.BLACK);
    g.fillRect(500, 25, 100, 50);
    g.setColor(Color.GREEN);
    g.setFont(new Font("ComicSansMs",Font.BOLD,26));
    g.drawString("BACK",508,58);   
}

public void DrawEndGame(Graphics g)
{
try
{
        URL imagePath= snakeCanvas.class.getResource("endpagebackground.jpg");
        this.endImage=Toolkit.getDefaultToolkit().getImage(imagePath);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
g.drawImage(endImage,0,0,640,480,this);
g.setColor(Color.BLACK);
g.setFont(new Font("ComicSansMs",Font.BOLD,20));
    if(won)
    {
    g.drawString("Congratulations!! You Won! :)", getPreferredSize().width/2, getPreferredSize().height/2);
    }
    else
    {
    g.drawString("You Lose! Please try again!", getPreferredSize().width/2 - 100, getPreferredSize().height/2);
    }
    g.drawString("Your Score is : "+score, getPreferredSize().width/2 - 100, getPreferredSize().height/2 + 40);
    g.drawString("Press \"SPACE\" to Restart", getPreferredSize().width/2 - 100, getPreferredSize().height/2 + 80);
}

public void Move()
{
if(Direction == direction.NO_DIRECTION)
return;
    Point head=snake.peekFirst();
    Point newPoint=head;
    switch(Direction)
    {
    case direction.NORTH:
        newPoint=new Point(head.x,head.y-1);
        break;
    case direction.SOUTH:
        newPoint= new Point(head.x,head.y+1);
        break;
    case direction.WEST:
        newPoint= new Point(head.x-1,head.y);
        break;
    case direction.EAST:
        newPoint= new Point(head.x+1,head.y);
        break;
    }
    if(Direction!=direction.NO_DIRECTION);
    snake.remove(snake.peekLast());
    if(newPoint.equals(bug))
    {
    //snake has hit bug, reset game
    URL audioPath= snakeApplet.class.getResource("Sad_Trombone-Joe_Lamb-665429450.wav");
      AudioClip audio = getAudioClip(audioPath, "Sad_Trombone-Joe_Lamb-665429450.wav");
      audio.play();
    CheckScore();
    won=false;
    atEnd=true;
    return;
    }
    if(newPoint.equals(fruit))
    {
    //snake has hit fruit
    score+=10;
    Point addPoint= (Point) newPoint.clone();
    switch(Direction)
    {
    case direction.NORTH:
    newPoint=new Point(head.x,head.y-1);
    break;
   case direction.SOUTH:
       newPoint= new Point(head.x,head.y+1);
       break;
   case direction.WEST:
       newPoint= new Point(head.x-1,head.y);
       break;
   case direction.EAST:
       newPoint= new Point(head.x+1,head.y);
       break;
   }
        snake.push(addPoint);
        PlaceFruit();
        PlaceBug();
    }
    else if(newPoint.x<0 || newPoint.x> (GRID_WIDTH-1))
    {
    //user went oob, reset game
    URL audioPath= snakeApplet.class.getResource("Sad_Trombone-Joe_Lamb-665429450.wav");
    AudioClip audio = getAudioClip(audioPath, "Sad_Trombone-Joe_Lamb-665429450.wav");
    audio.play();
    CheckScore();
    won=false;
    atEnd=true;
    return;
    }
    else if(newPoint.y<0 ||newPoint.y> (GRID_HEIGHT-1))
    {
    //user went oob, reset game
    //WANT TO ADD SOUND
    URL audioPath= snakeApplet.class.getResource("Sad_Trombone-Joe_Lamb-665429450.wav");
    AudioClip audio = getAudioClip(audioPath, "Sad_Trombone-Joe_Lamb-665429450.wav");
    audio.play();
    CheckScore();
    won=false;
    atEnd=true;
    return;
    }
    else if(snake.contains(newPoint))
    {
        //snake collided with itself, reset game
    URL audioPath= snakeApplet.class.getResource("Sad_Trombone-Joe_Lamb-665429450.wav");
      AudioClip audio = getAudioClip(audioPath, "Sad_Trombone-Joe_Lamb-665429450.wav");
    audio.play();
    if(Direction!=direction.NO_DIRECTION)
    {
    CheckScore();
    won=false;
    atEnd=true;
    return;  
    }
    }
    else if(snake.size()== (GRID_WIDTH*GRID_HEIGHT))
    {
    //WE WON
    CheckScore();
    won=true;
    atEnd=true;
    return;
    }
    snake.push(newPoint);
}

public void PlaceFruit()
{
    Random rand=new Random();
    int randomX=rand.nextInt(GRID_WIDTH);
    int randomY=rand.nextInt(GRID_HEIGHT);
    Point randomPoint=new Point(randomX,randomY);
    while(snake.contains(randomPoint))
    {
        randomX=rand.nextInt(GRID_WIDTH);
        randomY=rand.nextInt(GRID_HEIGHT);
        randomPoint=new Point(randomX,randomY);
    }
    fruit=randomPoint;
}

public void PlaceBug()
{
Random rand=new Random();
    int randomX=rand.nextInt(GRID_WIDTH);
    int randomY=rand.nextInt(GRID_HEIGHT);
    Point randomPoint=new Point(randomX,randomY);
    while(snake.contains(randomPoint)||fruit.equals(randomPoint))
    {
        randomX=rand.nextInt(GRID_WIDTH);
        randomY=rand.nextInt(GRID_HEIGHT);
        randomPoint=new Point(randomX,randomY);
    }
    bug=randomPoint;
}

public void run() 
{
    while(true)
    {
    repaint();
        if(!inMenu && !atEnd)
        Move();
        try
        {
        Thread.currentThread();
        Thread.sleep(100);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

public String GetHighscore()
{
// format - UserName : Highscore
FileReader readFile = null;
BufferedReader reader = null;
try
{
readFile = new FileReader("highscore.dat");
reader = new BufferedReader(readFile);
return reader.readLine();
}
catch(Exception e)
{
return "Nobody:0";
}
finally
{
try
{
if(reader != null)
reader.close();
}
catch (IOException e)
{
e.printStackTrace();
}
}
}
    
public void keyTyped(KeyEvent e){}
    
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                if(Direction != direction.SOUTH)
                Direction=direction.NORTH;
                break;
                
            case KeyEvent.VK_DOWN:
                if(Direction != direction.NORTH)
                Direction=direction.SOUTH;
                break;
                
            case KeyEvent.VK_LEFT:
                if(Direction != direction.EAST)
            Direction=direction.WEST;
                break;
                
            case KeyEvent.VK_RIGHT:
            if(Direction != direction.WEST)
            Direction=direction.EAST;
                break;
            case KeyEvent.VK_ESCAPE:
                 inMenu=true;
                 break;
            case KeyEvent.VK_SPACE:
            if(atEnd)
            {
            atEnd=false;
            won=false;
            GenerateDefaultSnake();
            repaint();
            }
            break;
            case KeyEvent.VK_SHIFT:
                if(inIntro)
                {
                    inIntro=false;
                    inMenu=true;
                    repaint();
                }
                break;
        }
    }
    
    public void keyReleased(KeyEvent e) {}

    public void mouseClicked(MouseEvent m)
    {
    xpos=m.getX();
        ypos=m.getY();
        //Start Button
        if(xpos>450 && xpos<550)
            if(ypos>50 && ypos<100)
            {    
            inHelp=false;
            inMenu=false;
            repaint();
            }
        //Help Button	
        if(xpos>450 && xpos<550)
        if(ypos>125 && ypos<175)
        {   
        inMenu=false;
        inHelp=true;
        repaint();
        }
        //Back Button
        if(xpos>500 && xpos<600)
            if(ypos>25 && ypos<75)
            {   
            inMenu=true;
            repaint();
            }
    }

    public void mouseEntered(MouseEvent arg0) {}

    public void mouseExited(MouseEvent arg0) {}

    public void mousePressed(MouseEvent arg0) {}

    public void mouseReleased(MouseEvent arg0) {}
}