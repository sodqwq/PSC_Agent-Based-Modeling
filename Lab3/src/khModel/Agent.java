package khModel;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;

public class Agent implements Steppable {
	int x;//x,y coordinates
	int y;
	int dirx;//the direction of movement
	int diry;
	//KH model
	public boolean female;//determines whether an agent is a female == true, male == false
	public double attractiveness;//Attractiveness of this agent
	//TODO: add a double frustration = 1 variable
	public double frustration = 1.0;
	public double dates = 1;//starts with 1 and incremented by 1 with each date.
	public boolean dated = false;//flag for dating on each round
	public Stoppable event;//allows to remove an agent from the simulation.


	public Agent(int x, int y, boolean female, double attractiveness) {
		super();
		this.x = x;
		this.y = y;
		this.female = female;
		this.attractiveness = attractiveness;
	}
	
	public Agent(int x, int y, int dirx, int diry, boolean female, double attractiveness) {
		super();
		this.x = x;
		this.y = y;
		this.dirx = dirx;
		this.diry = diry;
		this.female = female;
		this.attractiveness = attractiveness;
	}

	/**
	 * Finds a date for an agent of the opposite sex randomly from the male or female populations.
	 * @param state
	 * @return
	 */
	public Agent findDate(Environment state) {
		if(female ) {//agent gender
			if(state.male.numObjs==0)
				return null;//if empty return null
			return (Agent)state.male.objs[state.random.nextInt(state.male.numObjs)];
		}
		else {
			if(state.female.numObjs==0)
				return null;
			return (Agent)state.female.objs[state.random.nextInt(state.female.numObjs)];
		}
	}

	/**
	 * This method finds a date from the local neighborhood in space of this agent.
	 * @param state
	 * @return
	 */

	public Agent findLocalDate(Environment state) {
		Bag neighbors ; //TODO: Get the local neighbors
		// draw agents randomly from the neighbors bag if it is not empty.  If an agent is of the opposite sex and not dated, then
		//return it.
		neighbors = state.sparseSpace.getMooreNeighbors(x, y, state.dateSearchRadius, state.sparseSpace.TOROIDAL, true);
		neighbors.shuffle(state.random);
		
		if(neighbors != null) {
			for (int i = 0; i < neighbors.numObjs; i++) {
				Agent a = (Agent)neighbors.objs[i];
				if(female) { // if current agent is female, find male neighbor
					if(a.female == false && a.dated == false) {
						return a;
					}
				}
				else { // if current agent is male, find female neighbor
					if(a.female == true && a.dated == false) {
						return a;
					}
				}
			}
		}
		return null; //a place holder for when you will return an agent that has not dated or null if none can be found or all of dated
	}
	
	
	public void replicate (Environment state, boolean gender){
		  //Your code here. It should do the following. 
	      //(1) Create a new agent (a spatial agent will work 
	      //for non-spatial as well)
	      //Agent(int x, int y, int dirx, int diry, 
	      //  boolean female, double attractiveness)
	      //(2) initialize it with the same gender (to make sure 
	      //the gender ratio remains constant),
		
	      //(3) give it a new random attractiveness score, and 
		int x = state.random.nextInt(state.gridWidth);
		int y = state.random.nextInt(state.gridHeight);
		int dirx = state.random.nextInt(3)-1;
		int diry = state.random.nextInt(3)-1;
		double attractiveness = state.random.nextDouble(true, true) * 10;
		
		Agent a = new Agent(x, y, dirx, diry, gender, attractiveness);
	      //(4) schedule it
		a.event = state.schedule.scheduleRepeating(a); 
	      //(5) create a new graphic for the agent:
		if(gender) {
			//if female, red dot
			state.gui.setOvalPortrayal2DColor(a, (float)1, (float)0, (float)0, 
					(float)(attractiveness/state.maxAttractiveness));
		}
		else {
			//if male, blue dot
			state.gui.setOvalPortrayal2DColor(a, (float)0, (float)0, (float)1, 
					(float)(attractiveness/state.maxAttractiveness));
		}
	     //Hint: How did we make gender agents in the Environment?  
	}

	/**
	 * KH closing time rule
	 * @param state
	 * @param p
	 * @return
	 */
	public double ctRule(Environment state, double p) {
		return Math.pow(p, ct(state));
	}
	
	public double ct(Environment state) {
		if (state.maxDates+1-dates >= 0)
			return (state.maxDates+1-dates)/(state.maxDates);
		else
			return 0.0;
	}
	/**
	 * Attractiveness rule
	 * @param state
	 * @param a
	 * @return
	 */
	public double p1(Environment state, Agent a) {
		return Math.pow(a.attractiveness, state.choosiness)/Math.pow(state.maxAttractiveness, state.choosiness);
	}

	public double p2(Environment state, Agent a) {
		return Math.pow(state.maxAttractiveness-Math.abs(this.attractiveness - a.attractiveness), state.choosiness)/
				Math.pow(state.maxAttractiveness, state.choosiness);
	}

	/**
	 * Mixed rule
	 * @param state
	 * @param a
	 * @return
	 */
	public double p3(Environment state, Agent a) {
		return (p1(state,a)+p2(state,a))/2.0;
	}
	

	/**
	 * Implements the mixed rule 2 or frustration rule;
	 * @param state
	 * @param a
	 * @return
	 */

	public double p4(Environment state, Agent a) {
		// p4 = Fp1 + (1 - F)p2
		double p4 = p1(state,a) * frRule(state) + p2(state,a) * (1 - frRule(state)); 
		return p4;// when finished this should return a probability you calculate
	}
	
	public double frRule(Environment state) {
		// Implement the FR step function
		if(this.dates <= state.maxDates) {
			return (state.maxDates + 1 - this.dates)/state.maxDates;
		}
		else {
			return 0.0;
		}
		//replace with appropriate code
	}

	public void remove(Environment state) {
		if(female) 
			state.female.remove(this);//remove from the population
		else
			state.male.remove(this);
		state.sparseSpace.remove(this);//remove it from space
		event.stop();//remove from the schedule
	}

	public void nextPopulationStep(Environment state) {
		dated = true; //set dated to true.
		if(female) {
			state.nextFemale.add(this);
			state.female.remove(this);
		}
		else {
			state.nextMale.add(this);
			state.male.remove(this);
		}
	}
	
	/**
	 * Whether a simulation is spatial or non-spatial, once given an Agent a (partner), it 
	 * handles the mutual discision process.
	 * @param state
	 * @param a
	 */
	public void date(Environment state, Agent a) {
		double p;
		double q;
		switch(state.rule) {
		case ATTRACTIVE:
			p = p1(state,a);
			q = a.p1(state, this);
			break;
		case SIMILAR:
			p = p2(state,a);
			q = a.p2(state, this);
			break;
		case MIXED:
			p = p3(state,a);
			q = a.p3(state, this);
			break;
		// TODO: add a case for the p4 rule
		case FRUSTRATION:
			p = p4(state,a);
			q = a.p4(state, this);
			break;
		default:
			p = p1(state,a);
			q = a.p1(state, this);
			break;	
		}
		p = ctRule(state,p);
		q = ctRule(state,q);

		if(state.random.nextBoolean(p)&& state.random.nextBoolean(q)) {//couple decison
			if(female) {
				state.experimenter.getData(this, a);
			}
			else {
				state.experimenter.getData(a, this);
			}
			remove(state);
			a.remove(state);
			//TODO: When replacement is checked, code is needed here to handle replacement
			if(state.isReplicate()) {
				replicate(state, a.female);
			}
		} //end if test
		else {
			this.nextPopulationStep(state);
			a.nextPopulationStep(state);
		}
		if(dates <= state.maxDates) {
			dates++;
			frustration++;
		}
		if(a.dates <= state.maxDates) {
			a.dates++;
			a.frustration++;
		}
		//TODO: update frustration
	}

	/**
	 * Handles dates for non-spatial and spatial models. It finds a partner
	 * for spatial and non-spatial simulations and then uses date(Environment state, Agent a)
	 * @param state
	 */
	public void date(Environment state) {
//		Agent a = findDate(state);
//		if(a != null)
//			date(state, a);
		
		//TODO: replace the above code with an if than conditional
		//of the form:
		if(state.nonSpatialModel) {
		  Agent a = findDate(state);
		  if(a != null) {
			  date(state, a);
			}
		}
		else {
			Agent a = findLocalDate(state);
		  if(a != null) {
			  date(state, a);
			}
		}
	}

	public void placeAgent(Environment state) {
		if(state.oneCellPerAgent) {//only one agent per cell
			int tempx = state.sparseSpace.stx(x + dirx);//tempx and tempy location
			int tempy = state.sparseSpace.sty(y + diry);
			Bag b = state.sparseSpace.getObjectsAtLocation(tempx, tempy);
			if(b == null){//if empty, agent moves to new location
				x = tempx;
				y = tempy;
				state.sparseSpace.setObjectLocation(this, x, y);
			}//otherwise it does not move.
		}
		else {               
			x = state.sparseSpace.stx(x + dirx);
			y = state.sparseSpace.sty(y + diry);
			state.sparseSpace.setObjectLocation(this, x, y);
		}
	}
	/**
	 * Agents move randomly to a new location for either one agent per cell or possibly
	 * multiple agents per cell.
	 * @param state
	 */
	public void move(Environment state) {
		if(!state.random.nextBoolean(state.active)) {
			return;
		}
		if(state.random.nextBoolean(state.p)) {
			dirx = state.random.nextInt(3)-1;
			diry = state.random.nextInt(3)-1;
		}
		placeAgent(state);
	}


	public int decideX(Environment state, Bag neighbors) {
		int posX =0, negX =0;
		for(int i=0;i<neighbors.numObjs;i++) {
			Agent a = (Agent)neighbors.objs[i];
			if(a.x > x) {
				posX++;
			}
			else if(a.x < x) {
				negX++;
			}
		}
		if(posX > negX) {
			return 1;
		}
		else if (negX > posX) {
			return -1;
		}
		else {
			return state.random.nextInt(3)-1;
		}
	}

	public int decideY(Environment state, Bag neighbors) {
		int posY =0, negY =0;
		for(int i=0;i<neighbors.numObjs;i++) {
			Agent a = (Agent)neighbors.objs[i];
			if(a.y > y) {
				posY++;
			}
			else if(a.y < y) {
				negY++;
			}
		}
		if(posY > negY) {
			return 1;
		}
		else if (negY > posY) {
			return -1;
		}
		else {
			return state.random.nextInt(3)-1;
		}
	}

	public void aggregate (Environment state) {
		Bag b = state.sparseSpace.getMooreNeighbors(x, y, state.searchRadius, state.sparseSpace.TOROIDAL, false);
		dirx = decideX(state,b);
		diry = decideY(state,b);
		placeAgent(state);
	}

	public void step(SimState state) {
		Environment environment = (Environment)state;
		//TODO:
		//You will have to write code that does
		/*
		if(random probability to aggregate) {
			aggregate
		}
		else {
			move;
		}
		 */
		
		if (environment.random.nextBoolean(environment.aggregate)) {
			aggregate(environment);
		}
		else {
			move(environment);
		}

		if(!dated)
			date(environment);

	}

}
