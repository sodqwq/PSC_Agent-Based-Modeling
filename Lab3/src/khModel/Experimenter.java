package khModel;

import observer.Observer;
import sim.engine.SimState;
import sim.util.Bag;
import sweep.ParameterSweeper;
import sweep.SimStateSweep;

public class Experimenter extends Observer {
	int n = 0;
	double sX = 0;
	double sY = 0;
	double sX2 = 0;
	double sY2 = 0;
	double sXY = 0;

	public Experimenter(String fileName, String folderName, SimStateSweep state, ParameterSweeper sweeper,
			String precision, String[] headers) {
		super(fileName, folderName, state, sweeper, precision, headers);
		// TODO Auto-generated constructor stub
	}

	public void pairCorrelation(Environment state) {
	    double time = (double)state.schedule.getTime();//get the current time
	    this.upDateTimeChart(0,time, correlation(), true, 1000);//update chart #0 with up to a 1000 milisecond delay
	}

	public void attractivness (Environment state) {
	   //TODO: take a look at the method 'pairCorrelation', 
	   // this is what you want to plot: (sX+sY)/(2*n)
	   // You should check that n > 0 before dividing and plotting
		double time = (double)state.schedule.getTime();//get the current time
		double attractivness_plot;
		if (n > 0) {
			attractivness_plot = (sX + sY )/(2*n);
			this.upDateTimeChart(0,time, attractivness_plot, true, 1000);
		}
		
	}
	
	public void attractivenessDistribution(Environment state) {
		   Bag agents = state.sparseSpace.allObjects;//get remaining agents
		   double [] data = new double[agents.numObjs];//This is were attractiveness scores are placed
		   for(int i = 0;i<data.length;i++) {
		      //TODO:  fill the data array with the attractiveness scores for
		       // the remaining agents that are in the "agents" bag
			   Agent agent = (Agent) agents.get(i); // Cast the object to Agent
		       data[i] = agent.attractiveness;
		   }
		   if(agents.numObjs > 0) {
		       this.upDateHistogramChart(0, (int)state.schedule.getSteps(), data, 10);
		       }
	}
	
	public void resetVariables() {
		n = 0;
		sX = 0;
		sY = 0;
		sX2 = 0;
		sY2 = 0;
		sXY = 0;
	}

	/**
	 * Takes two agents and gets their attractiveness values and then does the 
	 * calculation for preparing the data to calculate a correlation value	
	 * @param x
	 * @param y
	 */
	public void getData(Agent x, Agent y) {
		getData(x.attractiveness, y.attractiveness);
	}

	public void getData(double x, double y){
		sXY += x*y;
		sX += x;
		sY += y;
		sX2 += x*x;
		sY2 += y*y;
		n++;

	}

	public double correlation(){
		return (sXY - (sX*sY)/n)/Math.sqrt((sX2-(sX*sX)/n)*(sY2-(sY*sY)/n));
	}

	public void printData(Environment state) {
		int percent = (int)((n/(double)state.females)*100.0);
		if(n>1) {
			System.out.println(state.schedule.getSteps()+1 +"      "+percent+"      "+correlation() + "     "+(sX+sY)/(2*n));
		}
	}

	public void stop(Environment state) {

		Bag agents = state.sparseSpace.getAllObjects();
		if(agents == null || agents.numObjs == 1) {
			this.event.stop();
		}
	}

	public void populations(Environment state) {
		Bag tempMale, tempFemale;//temp bags to hold agents
		tempMale = state.male;
		tempFemale = state.female;
		state.male = state.nextMale;
		state.female = state.nextFemale;
		state.nextFemale = tempFemale;
		state.nextMale = tempMale;
		state.nextFemale.clear();
		state.nextMale.clear();
		for(int i=0; i<state.male.numObjs;i++) {
			Agent a = (Agent)state.male.objs[i];
			a.dated = false;
		}
		for(int i=0; i<state.female.numObjs;i++) {
			Agent a = (Agent)state.female.objs[i];
			a.dated = false;
		}
	}

	public void step(SimState state) {
		super.step(state);
	       Environment environment = (Environment)state;
	       stop(environment);
	       populations(environment);
	       if(step %this.state.dataSamplingInterval == 0) {
	    	   pairCorrelation((Environment) state);
	           attractivness((Environment) state);
	           attractivenessDistribution((Environment) state);
	    	   //If a sampling interval, record data
	               //set the dataSamplingInterval to 1 in the model tab window when you run this
	             //So that it plots every step
	       
	       }
	}
}
