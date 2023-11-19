package freezingAggregation;

//import agents120.Agent;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class Agent implements Steppable {
	
	boolean frozen = false;
	int x; //location on the x-axis
	int y; //location on the y-axis
	int dirx; //x direction of change
	int diry; //y direction of change 
	
	public Agent(boolean frozen, int x, int y, int dirx, int diry) {
		super();
		this.x = x;
		this.y = y;
		this.dirx = dirx;
		this.diry = diry;
		this.frozen = frozen;
	}
	
	public void move(Environment state) {
		if(state.random.nextBoolean(state.pRandom)) {
			dirx = state.random.nextInt(3) - 1;
			diry = state.random.nextInt(3) - 1;
		}
		placeAgent(state);
		
	}
	
	public void placeAgent(Environment state) {
		int tempx;
		int tempy;
		if(state.bounded) { 
			tempx = bx(x + dirx, state);
			tempy = by(y + diry, state);
		}
		else {
			tempx = state.sparseSpace.stx(x + dirx);
			tempy = state.sparseSpace.stx(y + diry);
		}
		
		//Get the bag of objects at location <tempx, tempy>
		//bag gives the list the neighbors that are surround, if location is occupied, can not move there
		Bag b = state.sparseSpace.getObjectsAtLocation(tempx, tempy);
				
		if(b == null) {
			x = tempx;
			y = tempy;
			state.sparseSpace.setObjectLocation(this, x, y);
		}
		if(state.broadRule) {
			if(state.bounded) {
				//getMooreNeightors for a bounded space
				b = state.sparseSpace.getMooreNeighbors(x, y, 1, SparseGrid2D.BOUNDED, false); 
			}
			else {
				//getMooreNeightors for a toroidal space
				b = state.sparseSpace.getMooreNeighbors(x, y, 1, SparseGrid2D.TOROIDAL, false);
			}
			testFrozen(state, b);
		}
		else if(b != null){
			testFrozen(state, b);
		}
	}
	
	//neighbors will contain the surrounding agents for the 
	//broad rule or one agent at a location for the narrow rule
	
	//use for to check the Neighbors
	//cast Neighbors to agent then check frozen
	//if so, jump out the loop, then freeze
	public void testFrozen(Environment state, Bag Neighbors) {
		for (int i = 0; i < Neighbors.numObjs; i++) {
			Agent a = (Agent)Neighbors.objs[i];
			if (a.frozen) {
				this.frozen = true;
				this.dirx = 0;
				this.diry = 0;
			}
		}
		return;
	}
	
	
	//For a bounded space, you need to test whether an agent's x and y coordinates are out of bounds for the next step. 
	//The minimum bounds are 0 for both the x and y directions, and the maximum bounds are gridWidth -1 and gridHeight - 1.
	public int bx(int x, Environment state) {
		if (x <= 0) {
			x = 0;
		}
		if (x >= state.gridWidth) {
			x = state.gridWidth - 1;
		}
		return x;
	}
	
	public int by(int y, Environment state) {
		if (y <= 0) {
			y = 0;
		}
		if (y >= state.gridHeight) {
			y = state.gridHeight - 1;
		}
		return y;
	}
	
	@Override
	public void step(SimState state) {
		if(frozen) return;
			move((Environment)state);

	}

}
