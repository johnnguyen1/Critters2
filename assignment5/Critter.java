package assignment5;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

import static assignment5.Critter.CritterShape.STAR;

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR
	}

	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background
	 *
	 * critters must override at least one of the following three methods, it is not
	 * proper for critters to remain invisible in the view
	 *
	 * If a critter only overrides the outline color, then it will look like a non-filled
	 * shape, at least, that's the intent. You can edit these default methods however you
	 * need to, but please preserve that intent as you implement them.
	 */
	public javafx.scene.paint.Color viewColor() {
		return javafx.scene.paint.Color.WHITE;
	}

	public javafx.scene.paint.Color viewOutlineColor() {
		return viewColor();
	}

	public javafx.scene.paint.Color viewFillColor() {
		return viewColor();
	}

	public abstract CritterShape viewShape();

	private static String myPackage;
	private static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}


	/* rest is unchanged from Project 4 */


	private static java.util.Random rand = new java.util.Random();

	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}

	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}


	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() {
		return "";
	}

	private int energy = 0;

	protected int getEnergy() {
		return energy;
	}

	private int x_coord;
	private int y_coord;

    /*
    0 - right
    1 - right up (x++, y--)
    2 - up
    3 - left up
    4 - left
    5 - left down
    6 - down
    7 - right down

     */

	/**
	 * examines the location identified by the critter’s current coordinates and moving one or two positions
	 *
	 * @param direction where the creature wants to go
	 * @param steps     run or walk
	 * @return null if position is not occupied
	 */
	protected final String look(int direction, boolean steps) {
		this.energy -= Params.look_energy_cost;
		int x = this.x_coord;
		int y = this.y_coord;
		int iter = 1;
		if (steps) {
			iter = 2;
		}

		int d = direction;
		for (int i = 0; i < iter; i++) {
			int coord[] = newCoords(direction, x, y);
			x = coord[0];
			y = coord[1];
		}
		ArrayList<Integer> loc = sameLocation(x, y);
		if (loc.size() > 0) {

			return population.get(loc.get(0)).toString();
		}
		return "";

	}

	/**
	 * walks a critter in a specified direction
	 *
	 * @param direction the direction (0-8) of where the critter will walk
	 */
	protected final void walk(int direction) {
		energy -= Params.walk_energy_cost;
		int coord[] = newCoords(direction, this.x_coord, this.y_coord);
		x_coord = coord[0];
		y_coord = coord[1];
	}

	/**
	 * runs a critter in a specified direction
	 *
	 * @param direction the direction (0-8) of where the critter will run
	 */
	protected final void run(int direction) {   //how to do without zig zag
		energy += (2 * Params.walk_energy_cost);
		energy -= Params.run_energy_cost;
		walk(direction);
		walk(direction);
	}

	/**
	 * Determines what happens to a coordinate based on direction
	 *
	 * @param direction where to go
	 * @param x         x-coordinate
	 * @param y         y-coordinate
	 * @return array with x and y as 1st and 2nd element respectively
	 */
	protected final int[] newCoords(int direction, int x, int y) {
		int ret[] = new int[2];
		int d = direction;
		if (d == 2) {
			y = decByOne(y, 'y');
		} else if (d == 6) {
			y = incByOne(y, 'y');
		} else if (d == 0 || d == 1 || d == 7) {
			x = incByOne(x, 'x');
			if (d == 1) {
				y = decByOne(y, 'y');
			} else if (d == 7) {
				y = incByOne(y, 'y');
			}
		} else if (d == 3 || d == 4 || d == 5) {
			x = decByOne(x, 'x');
			if (d == 3) {
				y = decByOne(y, 'y');
			} else if (d == 5) {
				y = incByOne(y, 'y');
			}
		}
		ret[0] = x;
		ret[1] = y;
		return ret;

	}

	/**
	 * decreases a critter 1 unit on an axis, ensures cyclic movement
	 *
	 * @param coord the x or y coordinate
	 * @param axis  x or y axis
	 */
	private static int decByOne(int coord, char axis) { //0 is x, 1 is y
		int ax = Params.world_width;
		if (axis == 'y')
			ax = Params.world_height;
		if (coord == 0) {
			coord = coord - 1 + ax;
		} else
			coord--;
		return coord;
	}

	/**
	 * increases a critter 1 unit on an axis, ensures cyclic movement
	 *
	 * @param coord the x or y coordinate
	 * @param axis  x or y axis
	 */
	private static int incByOne(int coord, char axis) { //0 is x, 1 is y
		int ax = Params.world_width;
		if (axis == 'y')
			ax = Params.world_height;
		coord = (coord + 1) % ax;
		return coord;
	}


	/**
	 * sets an offspring's stats and places it in an adjacent position of its parent
	 *
	 * @param offspring the critter offspring
	 * @param direction is where the offspring is to be placed
	 */
	protected final void reproduce(Critter offspring, int direction) {
		if (energy < Params.min_reproduce_energy) return;
		energy = (int) Math.ceil((double) energy / 2.0);
		offspring.energy = (int) Math.floor((double) energy / 2.0);
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		offspring.energy += Params.walk_energy_cost;
		offspring.walk(direction);
		babies.add(offspring);
	}

	/**
	 * abstract method to determine each critter's timestep
	 */
	public abstract void doTimeStep();

	/**
	 * abstract method to determine each critter's fight
	 */
	public abstract boolean fight(String opponent);

	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 *
	 * @param critter_class_name name of critter
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Class c = Class.forName(myPackage + "." + critter_class_name);
			Object obj = c.newInstance();
			if (!(obj instanceof Critter)) {
				throw new InvalidCritterException(critter_class_name);
			}
			Critter crit = (Critter) c.newInstance();
			crit.x_coord = getRandomInt(Params.world_width);
			crit.y_coord = getRandomInt(Params.world_height);
			crit.energy = Params.start_energy;
			population.add(crit);

		} catch (ClassNotFoundException cne) {
			throw new InvalidCritterException(critter_class_name);
		} catch (InstantiationException ie) {
			throw new InvalidCritterException(critter_class_name);
		} catch (IllegalAccessException iae) {
			throw new InvalidCritterException(critter_class_name);
		}
	}

	/**
	 * create and initialize a baby Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 *
	 * @param baby_class_name name of baby critter
	 * @throws InvalidCritterException
	 */
	public static void makeBaby(String baby_class_name) throws InvalidCritterException {
		try {
			Class b = Class.forName(myPackage + "." + baby_class_name);
			Object obj = b.newInstance();
			if (!(obj instanceof Critter)) {
				throw new InvalidCritterException(baby_class_name);
			}
			Critter baby = (Critter) b.newInstance();
			baby.y_coord = getRandomInt(Params.world_height);
			baby.x_coord = getRandomInt(Params.world_width);
			baby.energy = Params.start_energy;
			babies.add(baby);


		} catch (InstantiationException ie) {
			throw new InvalidCritterException(baby_class_name);
		} catch (IllegalAccessException iae) {
			throw new InvalidCritterException(baby_class_name);
		} catch (ClassNotFoundException cne) {
			throw new InvalidCritterException(baby_class_name);
		}
	}

	/**
	 * Gets a list of critters of a specific type.
	 *
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
		Critter crit;
		try {
			Class c = Class.forName(myPackage + "." + critter_class_name);
			crit = (Critter) c.newInstance();
		} catch (ClassNotFoundException cne) {
			throw new InvalidCritterException(critter_class_name);
		} catch (InstantiationException ie) {
			throw new InvalidCritterException(critter_class_name);
		} catch (IllegalAccessException iae) {
			throw new InvalidCritterException(critter_class_name);
		}

		for (Critter w : population) {
			if (crit.getClass().isInstance(w))
				result.add(w);
		}
		return result;
	}

	/**
	 * Prints out how many Critters of each type there are on the board.
	 *
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string, 1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();
	}

	/* the TestCritter class allows some critters to "cheat". If you want to
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here.
	 *
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}

		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}

		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}

		protected int getX_coord() {
			return super.x_coord;
		}

		protected int getY_coord() {
			return super.y_coord;
		}


		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}

		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population.clear();
		babies.clear();
	}

	/**
	 * Does time step of all critters and removes all of the dead
	 */
	public static void worldTimeStep() {
		for (Critter c : population) {    //each critter does its timestep
			c.doTimeStep();
		}
		if (population.size() > 0) {   //clears those who die from moving
			ArrayList<Integer> dead = new ArrayList<>();
			for (int i = 0; i < population.size(); i++) {
				if (population.get(i).energy <= 0) {
					dead.add(i);
				}
			}
			remove(dead);
		}
		if (population.size() > 1) {
			int[][] elevMap = new int[Params.world_height][Params.world_width]; //grid to determine frequency at a location
			for (Critter c : population) {
				int col = c.x_coord;
				int row = c.y_coord;
				elevMap[row][col]++;
			}
			for (int row = 0; row < elevMap.length; row++) {
				for (int col = 0; col < elevMap[0].length; col++) {
					if (elevMap[row][col] > 1) {    //find all critters in the same position
						ArrayList<Integer> same = sameLocation(row, col);
						battle(same, row, col);
					}
				}
			}
		}
		updateRestEnergy(); //updates rest energy and removes dead
		for (int i = 0; i < Params.refresh_algae_count; i++) {  //creates algae
			try {
				makeBaby("Algae");
			} catch (InvalidCritterException ice) {
				System.out.println("error creating Algae");
			}
		}
		population.addAll(babies);  //add all children and algae
		babies.clear();

	}

	/**
	 * updates rest energy and removes dead critters
	 */
	private static void updateRestEnergy() {
		if (population.size() > 0) {
			ArrayList<Integer> dead = new ArrayList<>();
			for (int i = 0; i < population.size(); i++) {
				population.get(i).energy -= Params.rest_energy_cost;
				if (population.get(i).energy <= 0) {
					dead.add(i);
				}
			}
			remove(dead);
		}
	}

	/**
	 * removes dead critters given a list
	 *
	 * @param dead list of dead critters' indexes
	 */
	private static void remove(ArrayList<Integer> dead) {
		for (int i = dead.size() - 1; i >= 0; i--) { //work backward to not confuse indexes
			int ind = dead.get(i);
			population.remove(ind);
		}
	}

	/**
	 * Determines which creatures have the same location as the specified coordinate
	 *
	 * @param row x position
	 * @param col y position
	 * @return list the indexes of the critters with the specified position
	 */
	public static ArrayList<Integer> sameLocation(int row, int col) {
		ArrayList<Integer> sameList = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			if (population.get(i).x_coord == col && population.get(i).y_coord == row) {
				sameList.add(i);
			}
		}
		return sameList;
	}

	/**
	 * Handles encounters for every critter in a specified location
	 *
	 * @param x x position
	 * @param y y position
	 */
	private static void battle(ArrayList<Integer> inds, int x, int y) {
		//always going to deal with first 2 critters in a list;
		ArrayList<Integer> dead = new ArrayList<>();
		while (inds.size() > 1) {
			int a = inds.get(0);
			int b = inds.get(1);
			int aEnergy = population.get(a).energy;
			int bEnergy = population.get(b).energy;
			if (aEnergy <= 0) {
				inds.remove(0);
				dead.add(a);

			}
			if (bEnergy <= 0) {
				inds.remove(1);
				dead.add(b);
			}
			if (aEnergy <= 0 || bEnergy <= 0) {
				for (int i = dead.size() - 1; i >= 0; i--) { //work backward to not confuse indexes
					int ind = dead.get(i);
					population.remove(ind);
				}
				break;
			}
			boolean fAB = population.get(a).fight(population.get(b).toString());
			boolean fBA = population.get(b).fight(population.get(a).toString());
			int aRoll = getRandomInt(aEnergy);
			int bRoll = getRandomInt(bEnergy);
			if (!fAB) { //check if critter a has ran away
				if (population.get(a).x_coord != x || population.get(a).y_coord != y) {
					inds.remove(0);
					continue;
				} else
					aRoll = 0;
			}
			if (!fBA) { //check if critter b has ran away
				if (population.get(b).x_coord != x || population.get(b).y_coord != y) {
					inds.remove(1);
					continue;
				} else
					bRoll = 0;
			}
			if (aRoll >= bRoll) { //a wins if same or roll is greater
				population.get(a).energy += (0.5 * bEnergy);
				population.get(b).energy = 0;
				dead.add(b);
				inds.remove(1);
			} else if (aRoll < bRoll) {
				population.get(b).energy += (0.5 * aEnergy);
				population.get(a).energy = 0;
				dead.add(a);
				inds.remove(0);
			}

		}
		//remove dead critters
		remove(dead);

	}

	/**
	 * displays world as it is right now
	 */
	public static GridPane displayWorld() {
		GridPane gp = new GridPane();
		gp.setGridLinesVisible(true);
		Shape[][] grid = new Shape[Params.world_height][Params.world_width];

		//for (Critter c : population) {
		//	int col = c.x_coord;
		//	int row = c.y_coord;
		//	CritterShape cs = c.viewShape();
		//	grid[row][col] = getIcon(cs);
		//1}
		return gp;

		//prints out grid

	}

	private static Shape getIcon(CritterShape shape) {
		Shape s = null;
		int size = 100;

		switch (shape) {
			case CIRCLE:
				s = new Circle(size);
				s.setFill(Color.BLUE);
				break;
			case SQUARE:
				s = new Circle(size / 2);
				s.setFill(Color.RED);
				break;
			case TRIANGLE:
				s = new Circle(size / 2);
				s.setFill(Color.GREEN);
				break;
			case DIAMOND:
			case STAR:
		}
		// set the outline of the shape
		s.setStroke(javafx.scene.paint.Color.BLUE); // outline
		return s;


	}
}