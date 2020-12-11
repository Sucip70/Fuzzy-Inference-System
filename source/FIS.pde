float fuzz(float o, float a, float b, float c, float d){
  float tmp = 0;
  if(o>=a && o<=b)tmp = 1;
  else if(o>c && o<d)tmp = a>c?(o-c)/(d-c):(d-o)/(d-c);
  return tmp;
}

PVector defuzz(PVector O, float a, float b, float p, float q){
  float tmp = min(a, b);
  if(tmp != 0){
    O.x += (tmp*p+q)*tmp;
    O.y += tmp;
  }
  return O;
}

float aksiSerang(float dmg, float egy){  
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

float aksiPemulihan(float hlt, float dex){
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
