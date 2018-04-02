package assignment5;
/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * John Nguyen
 * jhn595
 * Slip days used: <0>
 * Spring 2018
 */


import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

	static Scanner kb;    // scanner connected to keyboard input, or input file
	private static String inputFile;    // input file, used instead of keyboard input if specified
	static ByteArrayOutputStream testOutputString;    // if test specified, holds all console output
	private static String myPackage;    // package of Critter file.  Critter cannot be in default pkg.
	private static boolean DEBUG = false; // Use it or not, as you wish!
	static PrintStream old = System.out;    // if you want to restore output to console


	// Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}

	/**
	 * Main method.
	 *
	 * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name,
	 *             and the second is test (for test output, where all output to be directed to a String), or nothing.
	 */

	public static void main(String[] args) {

		if (args.length != 0) {
			try {
				inputFile = args[0];
				kb = new Scanner(new File(inputFile));
			} catch (FileNotFoundException e) {
				System.out.println("USAGE: java Main OR java Main <input file> <test output>");
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
			}
			if (args.length >= 2) {
				if (args[1].equals("test")) { // if the word "test" is the second argument to java
					// Create a stream to hold the output
					testOutputString = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(testOutputString);
					// Save the old System.out.
					old = System.out;
					// Tell Java to use the special stream; all console output will be redirected here from now
					System.setOut(ps);
				}
			}
		} else { // if no arguments to main
			kb = new Scanner(System.in); // use keyboard and console
		}

		// Do not alter the code above for your submission.
		// Write your code below.
        /*
        for (int i = 0; i < 100; i++) {
            if (i < 25) {
                try {
                    Critter.makeCritter("Craig");
                } catch (InvalidCritterException ice) {
                    System.out.println("error processing: make Craig 25");

                }
            }
            try {
                Critter.makeCritter("Algae");
            } catch (InvalidCritterException ice) {
                System.out.println("error processing: make Algae 100");
            }

        }
        Critter.displayWorld();
        */
		int count = 0;
		String input = "";
		while (true) {
			System.out.print("critter>");   //prompt user
			input = kb.nextLine();
			if (input.length() < 4) {
				System.out.println("error processing: " + input);
				continue;
			}
			if (input.equals("show")) {     //displays World
				Critter.displayWorld();
			} else if (input.substring(0, 4).equals("step") || input.equals("step")) {  //time step
				int step = 0;
				if (input.equals("step")) {
					Critter.worldTimeStep();
					continue;
				}
				try {
					step = Integer.parseInt(input.substring(5, input.length()));
				} catch (NumberFormatException nfe) {
					System.out.println("error processing: " + input);
				}
				for (int i = 0; i < step; i++) {
					Critter.worldTimeStep();
				}
				continue;
			} else if (input.substring(0, 4).equals("seed")) {  //seed world
				long step = 0;
				try {
					step = Long.parseLong(input.substring(5, input.length()));
				} catch (NumberFormatException nfe) {
					System.out.println("error processing: " + input);
				}
				Critter.setSeed(step);
			} else if (input.length()>4 && input.substring(0, 5).equals("stats")) { //displays stats of specific critter
				String[] splited = input.split(" ");
				if (splited.length != 2 ) {
					System.out.println("error processing: " + input);
					continue;
				}
				String class_name = splited[1];
				java.util.List<Critter> stat;
				try {
					stat = Critter.getInstances(class_name);
				} catch (InvalidCritterException ice) {
					System.out.println("error processing: " + input);
					break;
				}
				try {
					Class c = Class.forName(myPackage + "." + class_name);
					java.lang.reflect.Method method = c.getMethod("runStats", java.util.List.class);
					method.invoke(c, stat);     //use reflection to use proper runStats
				} catch (Throwable e) {
					System.out.println("error processing: " + input);
				}
				//makes critters
			} else if (input.substring(0, 4).equals("make")) {
				String[] splited = input.split(" ");
				if (splited.length != 3 ) {
					System.out.println("error processing: " + input);
					continue;
				}
				String class_name = splited[1];
				int num = 0;
				try {
					num = Integer.parseInt(splited[2]);
				} catch (NumberFormatException nfe) {
					System.out.println("error processing: " + input);
				}
				for (int i = 0; i < num; i++) {
					try {
						Critter.makeCritter(class_name);
					} catch (InvalidCritterException ice) {
						System.out.println("error processing: " + input);
						break;
					}
				}
				//quits
			} else if(input.equals("quit")){
				break;
			} else {
				System.out.println("error processing: " + input);
				continue;
			}

		}
		//Write your code above
		System.out.flush();


	}
}
