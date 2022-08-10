package demo;
import java.util.*;

import common.Game;
import common.Machine;

public class Game_demo extends Game {

	// Providing the machine list to all machines
	@Override
	public void addMachines(ArrayList<Machine> machines, int numFaulty) {
		machlist = machines;
		NF = numFaulty;
		for (Machine machine : machlist) {
			machine.setMachines(machines);
		}
	}

	//Called at the beginning of every phase
	@Override
	public void startPhase() {
		
		int nf = NF;
		ArrayList<Integer> check = new ArrayList<Integer>(); //To check that the random 
															//numbers generated are not repeated

		Random rn = new Random();
		int random1 = rn.nextInt(machlist.size());
		machlist.get(random1).setState(false);

		nf--;
		check.add(random1);

		while (nf>0) {
			int random2 = rn.nextInt(machlist.size());

			boolean repeat = false;
			for (Integer ranInteger : check) {
				if(ranInteger==random2) repeat = true; //Checking for repeatition
			}
			if (!repeat) {
				machlist.get(random2).setState(false);
				check.add(random2);      //If didn't repeat add to the checklist and set as faulty
				nf--;
			}	
		}

		int ranleader = rn.nextInt(machlist.size());
		machlist.get(ranleader).setLeader();    //Picking a random leader

		check.clear();
	}

	private ArrayList<Machine> machlist;
	private int NF;
}
