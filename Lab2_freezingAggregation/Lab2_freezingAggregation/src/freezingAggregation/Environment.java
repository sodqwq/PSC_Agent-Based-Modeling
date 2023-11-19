package freezingAggregation;

import spaces.Spaces;
//import agents120.Agent;
import sweep.SimStateSweep;

public class Environment extends SimStateSweep {
	int n = 500; //number of agents
	double pRandom = 1.0; //probability of moving randomly
	boolean oneAgentperCell = false;
	boolean broadRule = true;//broad rule else narrow rule
	boolean bounded = true; //is the space bounded or unbounded?
	

	public Environment(long seed, Class observer) {
		super(seed, observer);
	}
	
	public void makeAgents() {
	    //First, you need to check to make sure there are not too many (n > gridWidth*grighHeight) agents
	    //Make sure each agent is placed in a unique location
		if(this.oneAgentperCell && n > gridWidth * gridHeight) {
			System.out.println("Too many agents, n = "+ n + " the space can hold = " + (gridWidth * gridHeight));
			return;
		}
		
	    //Create a frozen agent and place it in the middle
		Agent freeze = new Agent(true, gridWidth/2, gridHeight/2, 0, 0);
		//schedule.scheduleRepeating(freeze);
		this.sparseSpace.setObjectLocation(freeze, gridWidth/2, gridHeight/2);
		
	    //Create the remaining n-1 agents that are unfrozen and place them randomly in space
		for(int i = 0; i < n-1; i++) {
			int x = random.nextInt(gridWidth);
			int y = random.nextInt(gridHeight);
			if(this.oneAgentperCell) {
				while(sparseSpace.getObjectsAtLocation(x, y) != null) {
					x = random.nextInt(gridWidth);
					y = random.nextInt(gridHeight);
				}
			}
			int dirx = random.nextInt(3) - 1;
			int diry = random.nextInt(3) - 1;
			Agent a = new Agent(false, x,y,dirx,diry);
			schedule.scheduleRepeating(a); //It runs on each step and randomizes the order of each agent's actions
			this.sparseSpace.setObjectLocation(a, x, y);
		}
	}
	
	public void start() {
		super.start();//use already written code first
		spaces = Spaces.SPARSE;
		this.make2DSpace(spaces, gridWidth, gridHeight);
		makeAgents();
	}

}
