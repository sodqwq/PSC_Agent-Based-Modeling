package agents_120;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Agent implements Steppable {
	int x;
	int y;
	int xdir;
	int ydir;

	public Agent(int x, int y, int xdir, int ydir) {
		super();
		this.x = x;
		this.y = y;
		this.xdir = xdir;
		this.ydir = ydir;
	}

	public void move(Environment state) {
		if(!state.random.nextBoolean(state.pActive)) {
			return;
		}
		if(state.random.nextBoolean(state.pRandom)) {
			xdir = state.random.nextInt(3)-1;
			ydir = state.random.nextInt(3)-1;
		}
		placeAgent(state);
		
	}
	
	public void placeAgent(Environment state) {
		int tempx = state.sparseSpace.stx(x + xdir);
		int tempy = state.sparseSpace.sty(y + ydir);
		if(state.oneAgentperCell) {
			if(state.sparseSpace.getObjectsAtLocation(tempx, tempy) ==null) {
				x = tempx;
				y = tempy;
				state.sparseSpace.setObjectLocation(this, x, y);
			}
		}
		else {//false
			x = tempx;
			y = tempy;
			state.sparseSpace.setObjectLocation(this, x, y);
		}
	}
	
	public void aggregate(Environment state) {
		Bag neighbors = state.sparseSpace.getMooreNeighbors(x, y, state.searchRadius, state.sparseSpace.TOROIDAL, false);
		xdir = decideX(state, neighbors);
		ydir = decideY(state,neighbors);
		placeAgent(state);
	}
	
	public void flock(Environment state) {
		Bag neighbors = state.sparseSpace.getMooreNeighbors(x, y, state.searchRadius, state.sparseSpace.TOROIDAL, true);
		xdir = decideDirx(state,neighbors);
		ydir = decideDiry(state,neighbors);
		placeAgent(state);
	}
	
	public int decideX(Environment state, Bag neighbors) {
		int negX=0,posX=0;
		for(int i = 0; i< neighbors.numObjs;i++) {
			Agent a = (Agent)neighbors.objs[i];
			if(a.x > this.x) {
				posX++;
			}
			else if(a.x < this.x) {
				negX++;
			}
		}
		if(posX > negX)
			return 1;
		if(posX < negX) {
			return -1;
		}
		return 0;
	}
	
	public int decideY(Environment state, Bag neighbors) {
		int negY=0,posY=0;
		for(int i = 0; i< neighbors.numObjs;i++) {
			Agent a = (Agent)neighbors.objs[i];
			if(a.y > this.y) {
				posY++;
			}
			else if(a.y < this.y) {
				negY++;
			}
		}
		if(posY > negY)
			return 1;
		if(posY < negY) {
			return -1;
		}
		return 0;
	}
	public int decideDiry(Environment state, Bag neighbors){
	      int a = 0, b = 0, c = 0;// a(-1), b(0), c(1)
	      for(int i = 0; i< neighbors.numObjs;i++) {
	    	  Agent agent = (Agent)neighbors.objs[i];
	    	  int dy = agent.ydir;
	    	  if(dy == -1) { //add 1 to a
	    		  ++a;
	    		  }
	    	  else if(dy == 0){ //add 1 to b
	    		  ++b;
	    		  }
	    	  else {
	    		  ++c;//add 1 to c
	    	  }
	      }
	      if(a > b && a > c) {
	    	  return -1;
	      }
	      else if( b > a && b > c){
	    	  return 0;
	      }
	      else if(c > a && c > b) {
	    	  return 1;
	      }
	      else if(a == b && b == c) {
	    	  return state.random.nextInt(3)-1;	    	  
	      }
	      else if(a == b) {
	    	  if(a > c) {
	    		  return state.random.nextInt(3)-1;
	    	  }
	    	  else {
	    		  return 1;
	    	  }
	      }
	      else if (a == c) {
	    	  if(a > b) {
	    		  if(state.random.nextBoolean(0.5)) {
	    			  return -1;
	    			  }
	    		  else{
	    			  return 1;
	    			  }
	    		  }
	    	  else {
	    		  return 0;
	    		  }
	      }
	      else if (b == c) {
	    	  if(b > a) {
	    		  return state.random.nextInt(2);
	    		  }
	    	  else {
	    		  return -1;
	    		  }
	    	  }
	      else {
	    	  return state.random.nextInt(3)-1;
	      }
	}
	public int decideDirx(Environment state, Bag neighbors){
	      int a = 0, b = 0, c = 0;// a(-1), b(0), c(1)
	      for(int i = 0; i< neighbors.numObjs;i++) {
	    	  Agent agent = (Agent)neighbors.objs[i];
	    	  int dx = agent.xdir;
	    	  if(dx  == -1) { //add 1 to a
	    		  ++a;
	    		  }
	    	  else if(dx  == 0){ //add 1 to b
	    		  ++b;
	    		  }
	    	  else {
	    		  ++c;//add 1 to c
	    	  }
	      }
	      if(a > b && a > c) {
	    	  return -1;
	      }
	      else if( b > a && b > c){
	    	  return 0;
	      }
	      else if(c > a && c > b) {
	    	  return 1;
	      }
	      else if(a == b && b == c) {
	    	  return state.random.nextInt(3)-1;	    	  
	      }
	      else if(a == b) {
	    	  if(a > c) {
	    		  return state.random.nextInt(3)-1;
	    	  }
	    	  else {
	    		  return 1;
	    	  }
	      }
	      else if (a == c) {
	    	  if(a > b) {
	    		  if(state.random.nextBoolean(0.5)) {
	    			  return -1;
	    			  }
	    		  else{
	    			  return 1;
	    			  }
	    		  }
	    	  else {
	    		  return 0;
	    		  }
	      }
	      else if (b == c) {
	    	  if(b > a) {
	    		  return state.random.nextInt(2);
	    		  }
	    	  else {
	    		  return -1;
	    		  }
	    	  }
	      else {
	    	  return state.random.nextInt(3)-1;
	      }
	}
	public void step(SimState state) {
		Environment eState = (Environment)state;
		//eState.setpAggregate();
		//eState.setFlock(0.5/(1-0.1));
		if(eState.random.nextBoolean(eState.pAggregate)) {
			aggregate(eState);
		}
		else if(eState.random.nextBoolean(eState.flock)) {
			flock(eState);
		}
		else {
			move(eState);
		}

	}

}
