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
  void sketch(){
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
  
  void barStatus(){
    float xMid = anim[ch][0][0].width/2-100 + 300*ch;
    float yMid = anim[ch][0][0].height/2-100;
    fill(255);
    rect(xMid-100, yMid-120, 200, 15, 40);
    fill(#FF1F57);  
    rect(xMid-100, yMid-120, map(hlt, 0, 120, 0, 200), 15, 40);  
    fill(255);
    rect(xMid-100, yMid-100, 200, 15, 40);  
    fill(#6CFFB7);
    rect(xMid-100, yMid-100, map(egy, 0, 90, 0, 200), 15, 40);  
  }
  
  float getDamage(float def){
    return (0.266*atc/def-0.032)*150;
  }
  
  float doAction2(float _hlt, float _dex){
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
  
  Player doAction(Player O){
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
  void statNim(){
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

Player clone(Player now, int c){
  Player tmp = new Player(c);
  tmp.atc = now.atc;
  tmp.egy = now.egy;
  tmp.hlt = now.hlt;
  tmp.dex = now.dex;
  
  return tmp;
}

boolean simulation(Player tmpA, Player tmpB){
  float tA = tmpB.hlt;
  tmpB.hlt = tmpA.doAction2(tmpB.hlt, tmpB.dex);
  return tmpB.hlt<tA;
}
