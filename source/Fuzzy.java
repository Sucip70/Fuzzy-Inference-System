import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Fuzzy extends PApplet {

Player A, B;
PVector colBack = new PVector(0, 0);
PFont font;

public void setup(){
  
  font = loadFont("ArialRoundedMTBold-48.vlw");
  textFont(font);
  noStroke();
  splitImage(0, loadImage("data/sprite1.png"));
  splitImage(1, loadImage("data/sprite2.png"));
  A = new Player(0);
  B = new Player(1);
}

public void splitImage(int o, PImage tmp){
  for(int i=0;i<8;i++){
    for(int j=0;j<8;j++){
      anim[o][i][j] = tmp.get(j*512, i*512, 512, 512);
    }
  }
}

int who = 0, delay = 0;
float forA = 0, forB = 0;
boolean play = false;
public void draw(){
  background(255);
  if(play){
    fill(0xffD234FF);
    rect(10, 10, 285, 480, 30);
    rect(305, 10, 285, 480, 30);
    if(A.hlt==0)A.act = 6;
    else if(B.hlt==0)B.act = 6;
    else{
      fill(who==0?0xffECFA35:255);
      rect(30, 420, 245, 50, 20);
      fill(who==1?0xffECFA35:255);
      rect(325, 420, 245, 50, 20);

      if(frameCount%160==0){
        if(who==0){
          B = A.doAction(B);
          forA = A.egy;
        }else{
          A = B.doAction(A);
          forB = B.egy;
        }
        who = (who+1)%2;
      }
      if(frameCount%160==1){
        if(who==0){
          if(forB>B.egy)B.act = 4;
          else if(forB<B.egy)B.act = 7;  
        }else{
          if(forA>A.egy)A.act = 4;
          else if(forA<A.egy)A.act = 7;  
        }
      }
    }
  }else{
    if(A.act==6||B.act==6){A.act=0;B.act=0;}
    fill(colBack.x==0?0xffBEC4BE:0xffD234FF);
    rect(10, 10, 285, 480, 30);
    fill(colBack.y==0?0xffBEC4BE:0xffD234FF);
    rect(305, 10, 285, 480, 30);
    
    fill(255);
    rect(30, 420, 245, 50, 20);
    rect(325, 420, 245, 50, 20);
  }

  button();
  A.sketch();
  B.sketch();
  
  desc(0, A);
  desc(1, B);
  status();
  
  if(isInput!=-1){
    textField(140+300*(isInput/4), 305+25*(isInput%4));//120, 18 || 200 314
  }
}

int dia = 85, pd = 0;
public void button(){
  fill(255);
  ellipse(width/2, height/2, dia+15, dia+15);
  fill(play?0xffFF1A21:0xff2EFFFD);
  ellipse(width/2, height/2, dia, dia);
  setButton();
}

public void setButton(){
  if(pd<0){
    pd+=2;
    dia-=2;
    if(pd==0)play=true;
  }else if(pd>0){
    pd-=2;
    dia+=2;
    if(pd==0)play=false;
  }
}

public void status(){
  fill(255);
  textSize(30);
  textAlign(CENTER);
  boolean actA = simulation(clone(A, 0), clone(B, 1));
  fill(actA?0xffFF008D:0xff4BFA35);
  rect(32, 422, 241, 46, 20);
  fill(255);
  text(actA?"Menyerang":"Bertahan", 150, 455);
  boolean actB = simulation(clone(B, 1), clone(A, 0));
  fill(actB?0xffFF008D:0xff4BFA35);
  rect(327, 422, 241, 46, 20);
  fill(255);
  text(actB?"Menyerang":"Bertahan", 450, 455);
  textAlign(LEFT);
}

public void desc(int x, Player O){
  fill(250);
  textSize(18);
  float yPos = 320;
  float xPos = 50;
  text("attack", xPos+x*300, yPos);
  text("energy", xPos+x*300, yPos+25);
  text("health", xPos+x*300, yPos+50);
  text("dexterity", xPos+x*300, yPos+75);
  
  xPos+=80;
  text(": "+O.atc, xPos+x*300, yPos);
  text(": "+O.egy, xPos+x*300, yPos+25);
  text(": "+O.hlt, xPos+x*300, yPos+50);
  text(": "+O.dex, xPos+x*300, yPos+75);
}

int isInput = -1;
String textInput = "";
public void textField(float x, float y){
  stroke(0xff9100BF);
  fill(0, 100);
  rect(-5, -5, width+10, height+10);
  fill(255);
  rect(x, y, 120, 18);
  textAlign(LEFT);
  textSize(16);
  fill(0);
  text(textInput, x+3, y+16);
  float tw = textWidth(textInput)+x+3;
  if((2*frameCount/60)%2>0)
    line(tw, y+2, tw, y+16);
  fill(255);
  noStroke();
}
public float fuzz(float o, float a, float b, float c, float d){
  float tmp = 0;
  if(o>=a && o<=b)tmp = 1;
  else if(o>c && o<d)tmp = a>c?(o-c)/(d-c):(d-o)/(d-c);
  return tmp;
}

public PVector defuzz(PVector O, float a, float b, float p, float q){
  float tmp = min(a, b);
  if(tmp != 0){
    O.x += (tmp*p+q)*tmp;
    O.y += tmp;
  }
  return O;
}

public float aksiSerang(float dmg, float egy){  
  float dmgRendah = fuzz(dmg, 0, 15, 15, 31); //damage rendah
  float dmgBesar = fuzz(dmg, 45, 100, 27, 45); //damage besar
  float egyRendah = fuzz(egy, 0, 20, 20, 40); //energy rendah
  float egyBesar = fuzz(egy, 70, 90, 30, 70); //energy besar
  
  PVector z = new PVector(0, 0);
  
  z = defuzz(z, dmgBesar, egyBesar, 40, 60);  //IF damage besar DAN energy besar THEN priorityA tinggi
  z = defuzz(z, dmgBesar, egyRendah, -20, 60);  //IF damage besar DAN energy rendah THEN priorityA medium
  z = defuzz(z, dmgRendah, egyBesar, -20, 60);  //IF damage rendah ATAU energy besar THEN priorityA medium
  z = defuzz(z, dmgRendah, egyRendah, -40, 40);  //IF damage rendah DAN energy rendah THEN priorityA rendah

  if(z.x==0||z.y==0)return 0;
  return z.x/z.y;
}

public float aksiPemulihan(float hlt, float dex){
  float hltRendah = fuzz(hlt, 0, 50, 50, 80);  //damage rendah
  float hltBesar = fuzz(hlt, 100, 120, 80, 100);  //damage besar
  float dexRendah = fuzz(dex, 0, 45, 45, 60);  //energy rendah
  float dexBesar = fuzz(dex, 70, 100, 50, 70);  //energy besar

  PVector z = new PVector(0, 0);
  
  z = defuzz(z, hltBesar, hltBesar, -20, 20);  //IF dexterity besar DAN health besar THEN priorityB rendah
  z = defuzz(z, hltBesar, dexRendah, 40, 60);  //IF dexterity besar DAN health rendah THEN priorityB tinggi
  z = defuzz(z, hltRendah, dexBesar, -20, 20);  //IF dexterity rendah ATAU health besar THEN priorityB rendah 
  z = defuzz(z, hltRendah, dexRendah, 40, 60);  //IF dexterity rendah DAN health rendah THEN priorityB tinggi

  if(z.x==0||z.y==0)return 0;
  return z.x/z.y;
}
public void mouseClicked(){
  float x = mouseX;
  float y = mouseY;
  if(play){
    if(overObject(85, width/2, height/2, x, y)){
      pd = 50;
    }
  }else{
    if(overObject(85, width/2, height/2, x, y)){
      pd = -50;
    }else if(overRect(x, y, width/4, height/2, width/2, height)){
      colBack.x = 1;
      colBack.y = 0;
    }else{
      colBack.y = 1;
      colBack.x = 0;
    }
    for(int i=0;i<8;i++){
      if(overRect(x, y, 200+(i/4)*300, 314+25*(i%4), 120, 18)){
        isInput = i;
        switch(isInput){
          case 0:textInput = str(A.atc);break;
          case 1:textInput = str(A.egy);break;
          case 2:textInput = str(A.hlt);break;
          case 3:textInput = str(A.dex);break;
          case 4:textInput = str(B.atc);break;
          case 5:textInput = str(B.egy);break;
          case 6:textInput = str(B.hlt);break;
          case 7:textInput = str(B.dex);break;
        }
        break;
      } 
    }
  }
}

public void keyPressed(){
  if(key=='a'){
    println("cok");
    A.act =(A.act+1)%8;
  }else if(key=='r'){
    A.egy = 90;
    A.hlt = 120;
    A.atc = random(20, 60);
    A.dex = random(40, 70);
    B.egy = 90;
    B.hlt = 120;
    B.atc = random(20, 60);
    B.dex = random(40, 70);
  }
  if(isInput!=-1){
    if(keyCode==BACKSPACE){
      if(textInput.length()!=0)
        textInput = textInput.substring(0, textInput.length()-1);
    }
    else if(keyCode==ENTER){
      if(textInput.length()!=0){
        switch(isInput){
          case 0:A.atc = PApplet.parseFloat(textInput);break;
          case 1:A.egy = PApplet.parseFloat(textInput);break;
          case 2:A.hlt = PApplet.parseFloat(textInput);break;
          case 3:A.dex = PApplet.parseFloat(textInput);break;
          case 4:B.atc = PApplet.parseFloat(textInput);break;
          case 5:B.egy = PApplet.parseFloat(textInput);break;
          case 6:B.hlt = PApplet.parseFloat(textInput);break;
          case 7:B.dex = PApplet.parseFloat(textInput);break;
        }
      }  
      textInput = "";
      isInput = -1;
    }
    else if(textInput.length()<10){
      char o = key;
      if(o>=48 && o<=58 || o=='.')textInput+=key;
    }
  }
}

public boolean overObject(float d, float lx, float ly, float x, float y){
  float disX = lx - x;
  float disY = ly - y;
  if(sqrt(sq(disX)+sq(disY))<d/2)
    return true;
  return false;
}

public boolean overRect(float x, float y, float x0, float y0, float w, float h){
  if(x0-w/2<x && x<x0+w/2 && y0-h/2<y && y<y0+h/2)return true;
  return false;
}
PImage[][][] anim = new PImage[2][8][8];
int[] fr = {5, 8, 3, 2, 6, 1, 7, 2};

class Player{
  float atc = 0, egy = 0, hlt = 0, dex = 0;
  int ch = 0;
  
  Player(int c){
    egy = 90;
    hlt = 120;
    atc = random(20, 60);
    dex = random(40, 70);
    ch = c;
  }
   
  int spFrame = 0;
  int act = 0;
  boolean flag = true;
  public void sketch(){
    imageMode(CENTER);
    pushMatrix();
    translate(150+300*ch, 160);
    scale(pow(-1, ch), 1);
    image(anim[ch][act][spFrame], 0, 0);
    popMatrix();
    if(frameCount%10==0){
      if(act==0)spFrame = (spFrame+1)%fr[act];
      else{
        if(flag){
          flag = false;
          spFrame = 0;
        }
        if(act==6){
          if(spFrame!=fr[act]-1)spFrame++;
        }else{
          spFrame = (spFrame+1)%fr[act];
          if(spFrame==0){
            act=0;
            spFrame=0;
            flag = true;
          }
        }
      }
    }
    barStatus();
    statNim();
  }
  
  public void barStatus(){
    float xMid = anim[ch][0][0].width/2-100 + 300*ch;
    float yMid = anim[ch][0][0].height/2-100;
    fill(255);
    rect(xMid-100, yMid-120, 200, 15, 40);
    fill(0xffFF1F57);  
    rect(xMid-100, yMid-120, map(hlt, 0, 120, 0, 200), 15, 40);  
    fill(255);
    rect(xMid-100, yMid-100, 200, 15, 40);  
    fill(0xff6CFFB7);
    rect(xMid-100, yMid-100, map(egy, 0, 90, 0, 200), 15, 40);  
  }
  
  public float getDamage(float def){
    return (0.266f*atc/def-0.032f)*150;
  }
  
  public float doAction2(float _hlt, float _dex){
    float dmg = getDamage(_dex);
    float serang = aksiSerang(dmg, egy);
    float pemulihan = aksiSerang(_hlt, _dex);
    if(serang>=pemulihan && egy>=10){
      _hlt -= dmg;
      if(_hlt<0){_hlt = 0;}
      egy -= 10;
      if(egy<0)egy = 0;
    }else{
      egy += 5;
      if(egy>90)egy=90;
      hlt += 10;
      if(hlt>120)hlt=120;
    }
    return _hlt;
  }
  
  public Player doAction(Player O){
    float dmg = getDamage(O.dex);
    float serang = aksiSerang(dmg, egy);
    float pemulihan = aksiSerang(O.hlt, O.dex);
    if(serang>=pemulihan && egy>=10){
      if(O.hlt-dmg<0)O.hltNim = -O.hlt;
      else O.hltNim = -dmg;
      if(egy-10<0)egyNim = -egy;
      else egyNim = -10;
    }else{
      if(egy+5>90)egyNim = 90-egy;
      else egyNim = 5;
      if(hlt+10>120)hltNim = hlt-110;
      else hltNim = 10;
    }
    return O;
  }
  
  float hltNim = 0;
  float egyNim = 0;
  public void statNim(){
    if(hltNim!=0){
      if(hltNim>0){
        if(hltNim<1){
          hltNim = 0;
          hlt += hltNim;
        }else{
          hltNim--;
          hlt++;    
        }
      }else if(hltNim<0){
        if(hltNim>-1){
          hltNim = 0;
          hlt -= hltNim;
        }else{
          hltNim++;
          hlt--;    
        }
      }
    }
    if(egyNim!=0){
      if(egyNim>0){
        if(egyNim<1){
          egyNim = 0;
          egy += egyNim;
        }else{
          egyNim--;
          egy++;    
        }
      }else if(egyNim<0){
        if(egyNim>-1){
          egyNim = 0;
          egy -= egyNim;
        }else{
          egyNim++;
          egy--;    
        }
      }
    }
  }
}

public Player clone(Player now, int c){
  Player tmp = new Player(c);
  tmp.atc = now.atc;
  tmp.egy = now.egy;
  tmp.hlt = now.hlt;
  tmp.dex = now.dex;
  
  return tmp;
}

public boolean simulation(Player tmpA, Player tmpB){
  float tA = tmpB.hlt;
  tmpB.hlt = tmpA.doAction2(tmpB.hlt, tmpB.dex);
  return tmpB.hlt<tA;
}
  public void settings() {  size(600, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Fuzzy" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
