Player A, B;
PVector colBack = new PVector(0, 0);
PFont font;

void setup(){
  size(600, 500);
  font = loadFont("ArialRoundedMTBold-48.vlw");
  textFont(font);
  noStroke();
  splitImage(0, loadImage("data/sprite1.png"));
  splitImage(1, loadImage("data/sprite2.png"));
  A = new Player(0);
  B = new Player(1);
}

void splitImage(int o, PImage tmp){
  for(int i=0;i<8;i++){
    for(int j=0;j<8;j++){
      anim[o][i][j] = tmp.get(j*512, i*512, 512, 512);
    }
  }
}

int who = 0, delay = 0;
float forA = 0, forB = 0;
boolean play = false;
void draw(){
  background(255);
  if(play){
    fill(#D234FF);
    rect(10, 10, 285, 480, 30);
    rect(305, 10, 285, 480, 30);
    if(A.hlt==0)A.act = 6;
    else if(B.hlt==0)B.act = 6;
    else{
      fill(who==0?#ECFA35:255);
      rect(30, 420, 245, 50, 20);
      fill(who==1?#ECFA35:255);
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
    fill(colBack.x==0?#BEC4BE:#D234FF);
    rect(10, 10, 285, 480, 30);
    fill(colBack.y==0?#BEC4BE:#D234FF);
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
void button(){
  fill(255);
  ellipse(width/2, height/2, dia+15, dia+15);
  fill(play?#FF1A21:#2EFFFD);
  ellipse(width/2, height/2, dia, dia);
  setButton();
}

void setButton(){
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

void status(){
  fill(255);
  textSize(30);
  textAlign(CENTER);
  boolean actA = simulation(clone(A, 0), clone(B, 1));
  fill(actA?#FF008D:#4BFA35);
  rect(32, 422, 241, 46, 20);
  fill(255);
  text(actA?"Menyerang":"Bertahan", 150, 455);
  boolean actB = simulation(clone(B, 1), clone(A, 0));
  fill(actB?#FF008D:#4BFA35);
  rect(327, 422, 241, 46, 20);
  fill(255);
  text(actB?"Menyerang":"Bertahan", 450, 455);
  textAlign(LEFT);
}

void desc(int x, Player O){
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
void textField(float x, float y){
  stroke(#9100BF);
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
