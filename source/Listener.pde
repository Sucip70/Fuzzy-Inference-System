void mouseClicked(){
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

void keyPressed(){
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
          case 0:A.atc = float(textInput);break;
          case 1:A.egy = float(textInput);break;
          case 2:A.hlt = float(textInput);break;
          case 3:A.dex = float(textInput);break;
          case 4:B.atc = float(textInput);break;
          case 5:B.egy = float(textInput);break;
          case 6:B.hlt = float(textInput);break;
          case 7:B.dex = float(textInput);break;
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

boolean overObject(float d, float lx, float ly, float x, float y){
  float disX = lx - x;
  float disY = ly - y;
  if(sqrt(sq(disX)+sq(disY))<d/2)
    return true;
  return false;
}

boolean overRect(float x, float y, float x0, float y0, float w, float h){
  if(x0-w/2<x && x<x0+w/2 && y0-h/2<y && y<y0+h/2)return true;
  return false;
}
