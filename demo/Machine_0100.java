package demo;

import java.util.ArrayList;
import java.util.Random;


import common.Location;
import common.Machine;

public class Machine_0100 extends Machine {

	public Machine_0100() {
		//id = nextId++;
	}

	@Override
	public void setMachines(ArrayList<Machine> machines) {
		MacMachinelist = machines;			//Acquiring the machine list and setting IDs 
		for (int i = 0; i < MacMachinelist.size(); i++) {
			if (this.equals(MacMachinelist.get(i))) {
				this.id = i;
			}
		}
	}



	@Override
	public void setStepSize(int stepSize) {
		step = stepSize;
	}

	@Override
	public void setState(boolean isCorrect) {
		state = isCorrect;   //false if faulty
	}

	public void dirchange(int d){
		if (d==0) {
			//Multiplying the previous dir(x,y) with the matrix | 0  1 |
			//													| -1 0 |
			dir.setLoc(dir.getX()*0 + dir.getY()*(-1), dir.getX()+dir.getY()*0); 
		}

		if (d==1) {
			//Multiplying the previous dir(x,y) with the matrix | 0 -1 |
			//													| 1  0 |
			dir.setLoc(dir.getX()*0 + dir.getY()*(1), dir.getX()*(-1)+dir.getY()*0);			
		}

	}
	@Override
	public void setLeader() {

		//ROUND 0:
		rNum = 0;
		phNum++;

		NF = MacMachinelist.size()/3; //Assuming the maximum possible value as the NF from Game_demo
										// cannot be obtained
		Random rn = new Random();

		for (int i = 0; i < MacMachinelist.size(); i++) {
		DECLIST.add(-1);}   //initialing a decision lists which stores the decisions passed on by leader

		int decision = rn.nextInt(2);

		if (state==true) { //If the leader is correct and not faulty

			for (Machine machine : MacMachinelist) {	
				machine.sendMessage(this.id, phNum, rNum, decision);
			}
			
			for (int i = 0; i < MacMachinelist.size(); i++) {
				DECLIST.set(i, decision);
			}
			
		} else { //If the leader is faulty
				ArrayList<Integer> rand = new ArrayList<Integer>(); //Stores the NF IDs which are
																// randomly generated
				for (int i = 0; i < NF;) {
					int c = 0;
					int r = rn.nextInt(MacMachinelist.size()-1-2*NF)+1+2*NF; //r being atleast 2*t+1
					for (Integer numInteger : rand) {
						if(numInteger==r) c = 1;
					}
					if (c!=1) {
						rand.add(r);
						i++;
					}
				}

				for (Integer integer : rand) {
					MacMachinelist.get(integer).sendMessage(this.id, phNum, rNum, decision);
					DECLIST.set(integer, decision);
				}

				rand.clear();
		}

		//ROUND 1:
		
		
		//Each machine sends message to every machine in the list
		rNum = 1;
		for (int i = 0; i < MacMachinelist.size(); i++) {
			for (Machine machine : MacMachinelist) {
				machine.sendMessage(i, phNum, rNum, DECLIST.get(i)); 
			}
		}

		//Sending message to every machine indicating the end of round 2
		rNum = 3;
		for (Machine machine : MacMachinelist) {
			machine.sendMessage(0, phNum, rNum, -1);
		}

		DECLIST.clear();
	}

	@Override
	public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
		phNum = phaseNum;
		rNum = roundNum;
		NF  = MacMachinelist.size()/3;

		if (rNum==1) {
			if (state==true) { //If the machine is faulty
				if(Tcount< 2*NF + 1){  
					if (decision==1) { //Keeping count of both decisions
						dec1++; Tcount++;
					} 
					if(decision==0) {
						dec0++; Tcount++;
					}
				} else { //Once we got "enough" decisions
					if (dec0>dec1) { //Taking majority
						dn = 0;
						for (Machine machine : MacMachinelist) {
							machine.sendMessage(this.id, phNum, 2, dn);		
						}
					} else {
					    dn = 1;
						for (Machine machine : MacMachinelist) {
							machine.sendMessage(this.id, phNum, 2, dn);		
						}	
					}
					
				}
			} else {
				Random rn = new Random();
				int active = rn.nextInt(2);
				int D = rn.nextInt(2);

				if (active==1) { //Can stay silent or send a message
					for (Machine machine : MacMachinelist) {
						machine.sendMessage(this.id, phNum, 2, D);
					}
				}
			}
			
		}


		if (rNum == 2) { //Checking for 2*t+1 identical decisions
			if (Tcount2==0 && Tcount3==0) {
				dec2 = decision;
				Tcount2++;
			}
			if (Tcount2!=0 && Tcount3==0) {
				if(decision!=dec2)
				{
					dec3 = decision;
					Tcount3++;
				}
				else{
					Tcount2++;
				}
			}
			else{
				if(decision==dec3)
				{
					Tcount3++;
				}
				else{
					Tcount2++;
				}
			}
		}


		if(rNum == 3){
			int max = Math.max(Tcount2, Tcount3);
			if (max>= 2*NF +1) { //If received 2*t+1 identical decisions
				dirchange(dec2);

			} else {
				Random rn = new Random();
				int r = rn.nextInt(2);
				
				if(r==0) dirchange(0);
				if(r==1) dirchange(1);
				
				System.out.println("Error_"+id);
			}

			state = true; //Resetting 
			dec0 = dec1 = 0;
			Tcount = Tcount2 = Tcount3 =0;
			decision = dec2 = -1;
		}
	}

	@Override
	public
	void move() {
		pos.setLoc(pos.getX() + dir.getX()*step, 
					pos.getY() + dir.getY()*step);
	}

	@Override
	public
	String name() {
		return "demo_"+id;
	}

	@Override
	public Location getPosition() {
		return new Location(pos.getX(), pos.getY());
	}

	private ArrayList<Machine> MacMachinelist;
	private ArrayList<Integer> DECLIST = new ArrayList<Integer>();
	private int step;
	private Location pos = new Location(0,0);
	private Location dir = new Location(1,0); 
	private int id;
	private boolean state = true;
	private int phNum = -1;
	private int rNum = 0;
	private int dn;
	private int NF;
	private int Tcount=0;
	private int Tcount2=0;
	private int Tcount3=0;
	private int dec2 = 0;
	private int dec0 = 0;
	private int dec1 = 0;
	private int dec3 = 0;
}
