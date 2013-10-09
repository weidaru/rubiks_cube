package cube;

import java.awt.Event;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

import cube.CubieManager.Mode;

/**
 * 
 * @author Yan Yufei
 * @author Shen Zhiwen
 * 
 */
@SuppressWarnings("serial")
public class CubeHero extends rubik {
	//The moveMap is used to store predefined sequences of movements.
	//For movements F-Front, B-Back, R-Right, L-Left, U-Up, D-Down, all clock-wise
	//"-" simply means counter-clock-wise, number means repeat the previous move for certain times
	//For further details, see <a href="http://alexfung.info/favorite/cube/cube.htm">Solving the Rubik's Cube Systematically</a> 
	private final static Map<String, String> moveMap = new TreeMap<String, String>();
	static {
		moveMap.put("CM1R", "R-D-R");
		moveMap.put("CM1F", "FDF-");
		moveMap.put("CT1", "R-DRFDF-");
		moveMap.put("CT1-", "FD-F-R-D-R");
		moveMap.put("CM3R", "R-D-RUR-DRU-");
		moveMap.put("CM3R-", "UR-D-RU-R-DR");
		moveMap.put("CT2", "R-DRFDF-UFD-F-R-D-RU-");
		moveMap.put("CT2-", "UR-DRFDF-U-FD-F-R-D-R");
		moveMap.put("EM1F", "C-D2CD2");
		moveMap.put("EM1RD", "C-D-CD");
		moveMap.put("EM1BR", "FMF-");
		moveMap.put("EM1BL", "F-M2F");
		moveMap.put("EF1B", "MFM-F-F-M2FM2");
		moveMap.put("EF2B", "MFM-F-F-M2FM2UM2F-M2FFMF-M-U-");
		moveMap.put("EM3", "U2C-U2C");
		moveMap.put("EM3-", "C-U2CU2");
		moveMap.put("EM4", "U2C2U2C2");
		moveMap.put("UEM4U-", "U-C2U2C2U-");
		moveMap.put("NM4", "CM2C-M2");
		moveMap.put("NM6", "CMC-M-");
		moveMap.put("U4", "UU^D-");
		moveMap.put("B4", "BB^F-");
	}
	//FaceColors is the class storing all the data for rubik's cube
	//As the name indicates, rubik's cube is presented as faces with different colors.
	private FaceColors faceCols;
	//The manager for cubies
	private CubieManager mgr;

	/**
	 * The method to solve rubik's cube, which is separated into 3 parts.
	 * Details for this method as well as other sub-methods can be found in 
	 * <a href="http://alexfung.info/favorite/cube/cube.htm">Solving the Rubik's Cube Systematically</a>
	 */
	public void solve() {
		faceCols = new FaceColors(getCopyOfFaceCols());
		mgr = new CubieManager(faceCols);

		System.out.println("\nStart solveConers");
		solveConers();

		System.out.println("\nStart solveEdges");
		solveEdges();

		System.out.println("\nStart solveCenters");
		solveCenters();
	}


	private void solveConers() {
		System.out.println("\nStart solveUpCorners");
		solveUpCorners();

		System.out.println("\nStart solveDownCorners");
		solveDownCorners();
	}

	private void solveUpCorners() {
		for (int i = 0; i < 3; i++) {
			String colorUFL = mgr.createCubie("UFL", Mode.CUBIE_MODE)
					.getColor();
			int colorU = Integer.valueOf(colorUFL.substring(0, 1));
			int colorF = Integer.valueOf(colorUFL.substring(1, 2));
			int colorL = Integer.valueOf(colorUFL.substring(2));
			Cubie cubieToUFR = mgr.createCubie("" + colorU + colorF
					+ (5 - colorL), Mode.COLOR_MODE);
			// move corners
			if (checkContains(cubieToUFR, "D")) {
				if (checkContains(cubieToUFR, "RBD")) {
					if (i == 2)
						addMoves("D-");
					else {
						if (cubieToUFR.getCubie().charAt(0) == 'D')
							addMoves("R");
						else
							addMoves("D-");
					}
				} else if (checkContains(cubieToUFR, "BLD")) {
					if (i == 2)
						addMoves("D2");
					else {
						if (cubieToUFR.getCubie().charAt(0) == 'D')
							addMoves("D-R");
						else
							addMoves("D2");
					}
				} else if (checkContains(cubieToUFR, "LFD")) {
					if (i == 2)
						addMoves("D");
					else {
						if (cubieToUFR.getCubie().charAt(0) == 'D')
							addMoves("D2R");
						else
							addMoves("D");
					}
				}
			} else {
				if (checkContains(cubieToUFR, "UBR")) {
					if (cubieToUFR.getCubie().charAt(0) == 'B')
						addMoves("R2");
					else
						addMoves("RD-");
				} else if (checkContains(cubieToUFR, "UBL")) {
					if (cubieToUFR.getCubie().charAt(0) == 'B')
						addMoves("B-R2");
					else
						addMoves("B-RD-");
				} else if (checkContains(cubieToUFR, "UFR")) {
					if (i == 2) {
						if (cubieToUFR.getCubie().charAt(0) == 'F')
							addMoves(moveMap.get("CT1"));
						else if (cubieToUFR.getCubie().charAt(0) == 'R')
							addMoves(moveMap.get("CT1-"));
						break;
					} else
						addMoves("R-");
				}
			}
			// twist corners
			if (cubieToUFR.getCubie().charAt(0) == 'F') {
				if (i != 2)
					addMoves("R");
				else
					addMoves(moveMap.get("CM1F"));
			} else if (cubieToUFR.getCubie().charAt(0) == 'R')
				addMoves(moveMap.get("CM1R"));
			else if (cubieToUFR.getCubie().charAt(0) == 'D') {
				if (i == 2)
					addMoves(moveMap.get("CM1R") + moveMap.get("CT1-"));
				else
					addMoves("R" + moveMap.get("CT1"));
			}

			if (i != 2)
				addMoves("U");
		}
	}

	private void solveDownCorners() {
		addMoves(moveMap.get("B4"));
		int colorL = Integer.valueOf(mgr.createCubie("LBD", Mode.CUBIE_MODE)
				.getColor().substring(0, 1));
		Cubie cubieToRBD = mgr.createCubie((5 - colorL)
				+ getColor(faceCols, "LBD").substring(1), Mode.COLOR_MODE);
		Cubie cubieToRFD = mgr.createCubie((5 - colorL)
				+ getColor(faceCols, "LFD").substring(1), Mode.COLOR_MODE);
		Cubie cubieToRFU = mgr.createCubie((5 - colorL)
				+ getColor(faceCols, "LFU").substring(1), Mode.COLOR_MODE);
		Cubie cubieToRBU = mgr.createCubie((5 - colorL)
				+ getColor(faceCols, "LBU").substring(1), Mode.COLOR_MODE);

		// move corners
		if (checkContains(cubieToRBD, "URB"))
			addMoves("R");
		else if (checkContains(cubieToRBD, "URF"))
			addMoves("R2");
		else if (checkContains(cubieToRBD, "DRF"))
			addMoves("R-");

		if (checkContains(cubieToRFU, "RFU")
				&& checkContains(cubieToRBU, "RBU"))
			;
		else if (checkContains(cubieToRFU, "RFU")
				&& !checkContains(cubieToRBU, "RBU")) {
			addMoves(moveMap.get("CM3R") + "R" + moveMap.get("CM3R") + "R2");
		} else if (checkContains(cubieToRFD, "RFD"))
			addMoves("R" + moveMap.get("CM3R") + "R2");
		else if (checkContains(cubieToRBU, "RBU"))
			addMoves("R-" + moveMap.get("CM3R-") + "R2");
		else {
			if (checkContains(cubieToRFU, "RBU"))
				addMoves(moveMap.get("CM3R-"));
			else
				addMoves(moveMap.get("CM3R"));
		}

		// twist corners
		addMoves(moveMap.get("B4"));
		String colorU = String.valueOf(5 - colorL);
		Cubie cubieAtUFR = mgr.createCubie("UFR", Mode.CUBIE_MODE);
		Cubie cubieAtURB = mgr.createCubie("URB", Mode.CUBIE_MODE);
		Cubie cubieAtUBL = mgr.createCubie("UBL", Mode.CUBIE_MODE);
		Cubie cubieAtULF = mgr.createCubie("ULF", Mode.CUBIE_MODE);
		Cubie[] cubieArray = { cubieAtUFR, cubieAtURB, cubieAtUBL, cubieAtULF };
		int[] flip = new int[4]; // 0-no twist, 1-clockwise twist,
		// 2-counter-clockwise twist

		for (int i = 0; i < 3; i++) {
			if (cubieArray[i].getColor().substring(1, 2).equals(colorU))
				flip[i] = 1;
			else if (cubieArray[i].getColor().substring(2, 3).equals(colorU))
				flip[i] = 2;
			else
				flip[i] = 0;
		}

		if (flip[0] == flip[1] && flip[2] == flip[3])
			addMoves("U");
		for (int i = 0; i < 3; i++) {
			if (cubieAtUFR.getColor().substring(1, 2).equals(colorU))
				addMoves(moveMap.get("CT2"));
			else if (cubieAtUFR.getColor().substring(2, 3).equals(colorU))
				addMoves(moveMap.get("CT2-"));
			addMoves("U");
		}
	}

	private void solveEdges() {
		System.out.println("\nStart solveUp3Edges");
		solveUp3Edges();

		System.out.println("\nStart solveDown3Edges");
		solveDown3Edges();

		System.out.println("\nStart solve2LeftEdges");
		solve2LeftEdges();

		System.out.println("\nStart solveMidEdges");
		solveMidEdges();

		System.out.println("\nStart flipEdges");
		flipEdges();
	}

	private void solveUp3Edges() {
		for (int i = 0; i < 3; i++) {
			Cubie cubieAtUFL = mgr.createCubie("UFL", Mode.CUBIE_MODE);
			String colorU = cubieAtUFL.getColor().substring(0, 1);
			String colorF = cubieAtUFL.getColor().substring(1, 2);
			Cubie cubieToUF = mgr.createCubie(colorU + colorF, Mode.COLOR_MODE);
			if (checkContains(cubieToUF, "U")) {
				if (checkContains(cubieToUF, "F")) {
					if (cubieToUF.getCubie().substring(0, 1).equals("F"))
						addMoves(moveMap.get("EF1B"));
					addMoves("U");
					continue;
				} else if (checkContains(cubieToUF, "R"))
					addMoves("UFM-F-U-");
				else if (checkContains(cubieToUF, "B"))
					addMoves("U2FM-F-U2");
				else
					addMoves("U-FM-F-U");
			}

			if (checkContains(cubieToUF, "D")) {
				if (cubieToUF.getCubie().substring(0, 1).equals("D")) {
					String temp = cubieToUF.getCubie().substring(1, 2);
					if (temp.equals("L"))
						addMoves("D");
					else if (temp.equals("B"))
						addMoves("D2");
					else if (temp.equals("R"))
						addMoves("D-");
					addMoves(moveMap.get("EM1F"));
				} else {
					String temp = cubieToUF.getCubie().substring(0, 1);
					if (temp.equals("F"))
						addMoves("D");
					else if (temp.equals("L"))
						addMoves("D2");
					else if (temp.equals("B"))
						addMoves("D-");
					addMoves(moveMap.get("EM1RD"));
				}
				addMoves("U");
				continue;
			}

			if (checkContains(cubieToUF, "FR"))
				addMoves("M-");
			else if (checkContains(cubieToUF, "LB"))
				addMoves("M");
			else if (checkContains(cubieToUF, "FL"))
				addMoves("M2");
			if (cubieToUF.getCubie().substring(0, 1).equals("R"))
				addMoves(moveMap.get("EM1BL"));
			else
				addMoves(moveMap.get("EM1BR"));
			addMoves("U");
		}
	}

	private void solveDown3Edges() {
		addMoves(moveMap.get("B4") + moveMap.get("B4"));
		String c = mgr.createCubie("FDL", Mode.CUBIE_MODE).getColor()
				.substring(0, 1);

		for (int i = 0; i < 3; i++) {
			Cubie cubieAtUFL = mgr.createCubie("UFL", Mode.CUBIE_MODE);
			String colorU = cubieAtUFL.getColor().substring(0, 1);
			String colorF = cubieAtUFL.getColor().substring(1, 2);
			Cubie cubieToUF = mgr.createCubie(colorU + colorF, Mode.COLOR_MODE);

			if (colorF.equals(c)) {
				addMoves("U");
				colorU = cubieAtUFL.getColor().substring(0, 1);
				colorF = cubieAtUFL.getColor().substring(1, 2);
				cubieToUF = mgr.createCubie(colorU + colorF, Mode.COLOR_MODE);
			}

			if (checkContains(cubieToUF, "FD")) {
				if (cubieToUF.getCubie().substring(0, 1).equals("F"))
					addMoves("FM-F-");
				else
					addMoves("FM2F-" + moveMap.get("EM1BL"));
				addMoves("U");
				continue;
			}

			if (checkContains(cubieToUF, "U")) {
				if (checkContains(cubieToUF, "F")) {
					if (cubieToUF.getCubie().substring(0, 1).equals("F"))
						addMoves(moveMap.get("EF1B"));
					addMoves("U");
					continue;
				} else if (checkContains(cubieToUF, "R"))
					addMoves("UFM-F-U-");
				else if (checkContains(cubieToUF, "B"))
					addMoves("U2FM-F-U2");
				else
					addMoves("U-FM-F-U");
			}

			if (checkContains(cubieToUF, "FR"))
				addMoves("M-");
			else if (checkContains(cubieToUF, "LB"))
				addMoves("M");
			else if (checkContains(cubieToUF, "FL"))
				addMoves("M2");

			if (cubieToUF.getCubie().substring(0, 1).equals("R"))
				addMoves(moveMap.get("EM1BL"));
			else if (cubieToUF.getCubie().substring(0, 1).equals("B"))
				addMoves(moveMap.get("EM1BR"));
			addMoves("U");
		}
	}

	private void solve2LeftEdges() {
		Cubie cubieAtDFL = mgr.createCubie("DFL", Mode.CUBIE_MODE);
		String colorD = cubieAtDFL.getColor().substring(0, 1);
		String colorF = cubieAtDFL.getColor().substring(1, 2);
		Cubie cubieToUFL = mgr.createCubie((5 - Integer.valueOf(colorD))
				+ cubieAtDFL.getColor().substring(1), Mode.COLOR_MODE);
		if (checkContains(cubieToUFL, "ULB"))
			addMoves("U-");
		else if (checkContains(cubieToUFL, "URB"))
			addMoves("U2");
		else if (checkContains(cubieToUFL, "UFR"))
			addMoves("U");
		Cubie cubieToDF = mgr.createCubie(colorD + colorF, Mode.COLOR_MODE);
		Cubie cubieToUF = mgr.createCubie((5 - Integer.valueOf(colorD))
				+ colorF, Mode.COLOR_MODE);

		if (checkContains(cubieToDF, "DF")) {
			if (checkContains(cubieToUF, "UF"))
				return;
			else
				addMoves("FM2F-");
		} else if (checkContains(cubieToDF, "FR"))
			addMoves("M-");
		else if (checkContains(cubieToDF, "LB"))
			addMoves("M");
		else if (checkContains(cubieToDF, "FL"))
			addMoves("M2");

		if (cubieToDF.getCubie().substring(0, 1).equals("B"))
			addMoves(moveMap.get("EM1BR"));
		else if (cubieToDF.getCubie().substring(0, 1).equals("R"))
			addMoves(moveMap.get("EM1BL"));

		if (checkContains(cubieToUF, "FD"))
			addMoves("FM2F-" + moveMap.get("EM1BL"));
		else if (checkContains(cubieToUF, "FR"))
			addMoves("M-" + moveMap.get("EM1BR"));
		else if (checkContains(cubieToUF, "LB"))
			addMoves("M" + moveMap.get("EM1BR"));
		else if (checkContains(cubieToUF, "FL"))
			addMoves("M2" + moveMap.get("EM1BR"));
		else if (checkContains(cubieToUF, "BR"))
			addMoves(moveMap.get("EM1BR"));
	}

	private void solveMidEdges() {
		addMoves(moveMap.get("B4"));
		int colorF = Integer.valueOf(mgr.createCubie("DFL", Mode.CUBIE_MODE)
				.getColor().substring(1, 2));
		int colorD = Integer.valueOf(mgr.createCubie("DFL", Mode.CUBIE_MODE)
				.getColor().substring(0, 1));
		Cubie cubieToUF = mgr.createCubie("" + (5 - colorD) + colorF,
				Mode.COLOR_MODE);
		Cubie cubieToFD = mgr
				.createCubie("" + colorF + colorD, Mode.COLOR_MODE);
		Cubie cubieToDB = mgr.createCubie("" + colorD + (5 - colorF),
				Mode.COLOR_MODE);
		Cubie cubieToBU = mgr.createCubie("" + (5 - colorF) + (5 - colorD),
				Mode.COLOR_MODE);

		if (checkContains(cubieToFD, "UF"))
			addMoves("C-");
		else if (checkContains(cubieToFD, "DB"))
			addMoves("C");
		else if (checkContains(cubieToFD, "BU"))
			addMoves("C2");

		if (checkContains(cubieToBU, "BU") && checkContains(cubieToDB, "DB"))
			;
		else if (checkContains(cubieToBU, "BU") && !checkContains(cubieToDB, "DB")) {
			addMoves(moveMap.get("EM3") + "C-" + moveMap.get("EM3") + "C2");
		} else if (checkContains(cubieToDB, "DB"))
			addMoves("C-" + moveMap.get("EM3") + "C2");
		else if (checkContains(cubieToUF, "UF"))
			addMoves("C" + moveMap.get("EM3-") + "C2");
		else {
			if (checkContains(cubieToBU, "DB"))
				addMoves(moveMap.get("EM3"));
			else
				addMoves(moveMap.get("EM3-"));
		}
	}

	/**
	 * Strategy for flipEdges is making these edges which need to flip in to pairs, which can be guaranteed.
	 * Then use EF2B do the job without affecting other cubies.
	 */
	private void flipEdges() {
		Cubie cubieToUFR = mgr.createCubie("UFR", Mode.CUBIE_MODE);
		int colorU = Integer.valueOf(cubieToUFR.getColor().substring(0, 1));
		int colorF = Integer.valueOf(cubieToUFR.getColor().substring(1, 2));
		int colorR = Integer.valueOf(cubieToUFR.getColor().substring(2, 3));
		int colorD = 5 - colorU;
		int colorB = 5 - colorF;
		int colorL = 5 - colorR;
		Map<String, String> colorMap = new TreeMap<String, String>();
		colorMap.put("U", "" + colorU);
		colorMap.put("F", "" + colorF);
		colorMap.put("R", "" + colorR);
		colorMap.put("D", "" + colorD);
		colorMap.put("B", "" + colorB);
		colorMap.put("L", "" + colorL);
		String[] edgesString = { "UF", "UR", "UB", "UL", "FR", "RB", "BL",
				"LF", "DF", "DR", "DB", "DL" };
		Queue<Cubie> opQueue = new LinkedList<Cubie>();

		for (String edge : edgesString) {
			String color = getColor(faceCols, edge);

			if (!color.substring(0, 1).equals(
					colorMap.get(edge.substring(0, 1))))
				opQueue.add(mgr.createCubie(color, Mode.COLOR_MODE));
		}

		while (!opQueue.isEmpty()) {
			Cubie edge1 = opQueue.poll();
			String move1 = "";
			Cubie edge2 = opQueue.poll();
			String move2 = "";

			System.out.println("Flip edge " + edge1.getCubie() + " and "
					+ edge2.getCubie());

			if (checkContains(edge1, "UR"))
				move1 += "U";
			else if (checkContains(edge1, "UB"))
				move1 += "U2";
			else if (checkContains(edge1, "UL"))
				move1 += "U-";
			else if (checkContains(edge1, "FR"))
				move1 += "F-";
			else if (checkContains(edge1, "FD"))
				move1 += "F2";
			else if (checkContains(edge1, "FL"))
				move1 += "F";
			else if (checkContains(edge1, "RD"))
				move1 += "D-F2";
			else if (checkContains(edge1, "BD"))
				move1 += "D2F2";
			else if (checkContains(edge1, "LD"))
				move1 += "DF2";
			else if (checkContains(edge1, "RB"))
				move1 += "R-U";
			else if (checkContains(edge1, "LB"))
				move1 += "LU-";

			addMoves(move1);

			if (checkContains(edge2, "UB"))
				move2 += "B-R-";
			else if (checkContains(edge2, "UL"))
				move2 += "L2B2R2";
			else if (checkContains(edge2, "FR"))
				move2 += "R";
			else if (checkContains(edge2, "FD"))
				move2 += "DR2";
			else if (checkContains(edge2, "FL"))
				move2 += "M-R";
			else if (checkContains(edge2, "RD"))
				move2 += "R2";
			else if (checkContains(edge2, "BD"))
				move2 += "D-R2";
			else if (checkContains(edge2, "LD"))
				move2 += "D2R2";
			else if (checkContains(edge2, "RB"))
				move2 += "R-";
			else if (checkContains(edge2, "LB"))
				move2 += "B2R-";

			addMoves(move2);
			addMoves(moveMap.get("EF2B"));
			addMoves(getInverse(move1 + move2));
		}
	}

	private void solveCenters() {
		Cubie cubieToUFR = mgr.createCubie("UFR", Mode.CUBIE_MODE);
		int colorU = Integer.valueOf(cubieToUFR.getColor().substring(0, 1));
		int colorF = Integer.valueOf(cubieToUFR.getColor().substring(1, 2));
		int colorR = Integer.valueOf(cubieToUFR.getColor().substring(2, 3));
		int colorD = 5 - colorU;
		int colorB = 5 - colorF;
		int colorL = 5 - colorR;
		ArrayList<Cubie> centers = new ArrayList<Cubie>();
		String[] centerArray = { "U", "D", "F", "B", "L", "R" };
		String[] colorArray = { "" + colorU, "" + colorD, "" + colorF,
				"" + colorB, "" + colorL, "" + colorR };

		for (int i = 0; i < centerArray.length; i++) {
			if (getColor(faceCols, centerArray[i]).equals(colorArray[i]))
				centers.add(mgr.createCubie(colorArray[i], Mode.COLOR_MODE));
		}

		if (centers.size() == 2) {
			Cubie rightCubie = centers.get(0);

			if (checkContains(rightCubie, "F"))
				addMoves(moveMap.get("U4"));
			else if (checkContains(rightCubie, "B"))
				addMoves(moveMap.get("U4"));
			else if (checkContains(rightCubie, "U"))
				addMoves(moveMap.get("B4"));
			else if (checkContains(rightCubie, "D"))
				addMoves(moveMap.get("B4"));
			addMoves(moveMap.get("NM4"));
		} else if (centers.size() == 0) {
			Cubie cubieAtFR = mgr.createCubie("FR", Mode.CUBIE_MODE);
			Cubie cubieAtFU = mgr.createCubie("FU", Mode.CUBIE_MODE);
			String move1 = "";
			String move2 = "";

			String cubieToF = getCubie(faceCols, cubieAtFR.getColor()
					.substring(0, 1));
			if (checkContains(cubieToF, "R")) {
				move1 += "M";
				addMoves(move1);
				cubieToF = getCubie(faceCols, cubieAtFR.getColor().substring(0,
						1));
			} else if (checkContains(cubieToF, "L")) {
				move1 += "M-";
				addMoves(move1);
				cubieToF = getCubie(faceCols, cubieAtFR.getColor().substring(0,
						1));
			} else if (checkContains(cubieToF, "D")) {
				move1 += "C";
				addMoves(move1);
				cubieToF = getCubie(faceCols, cubieAtFU.getColor().substring(0,
						1));
			} else if (checkContains(cubieToF, "U")) {
				move1 += "C-";
				addMoves(move1);
				cubieToF = getCubie(faceCols, cubieAtFU.getColor().substring(0,
						1));
			}

			if (checkContains(cubieToF, "R"))
				move2 += "M";
			else if (checkContains(cubieToF, "L"))
				move2 += "M-";
			else if (checkContains(cubieToF, "D"))
				move2 += "C";
			else if (checkContains(cubieToF, "U"))
				move2 += "C-";
			addMoves(move2);

			addMoves(getInverse(move1) + getInverse(move2));
		}
	}

	/**
	 * @see The override version
	 */
	private boolean checkContains(Cubie cubie, String sub) {
		return checkContains(cubie.getCubie(), sub);
	}

	/**
	 * check whether the cubie contain certain sub-face without bothering the order.
	 * 
	 * @param cubie
	 * @param sub
	 * @return True for yes, false otherwise
	 */
	private boolean checkContains(String cubie, String sub) {
		char[] subFaceArray = sub.toUpperCase().toCharArray();

		System.out.println(cubie + " compared to " + sub);

		if (subFaceArray.length > 3)
			throw new IllegalArgumentException("Too many faces");

		for (char ch : subFaceArray) {
			if (cubie.indexOf(ch) == -1)
				return false;
		}
		return true;
	}

	/**
	 * The interface for adding movements in class rubik which is previous work
	 * done by Neil Rashbrook.
	 * 
	 * @param param move sequences
	 */
	public void addMoves(String param) {
		System.out.println("add moves: " + param);
		addMoves(faceCols.colors(), param);
		faceCols.modifyTimes(faceCols.modifyTimes() + 1);
	}

	public FaceColors faceCols() {
		return faceCols;
	}
	
	@Override
	public boolean keyDown(Event evt, int key) {
		faceCols = new FaceColors(getCopyOfFaceCols());
		mgr = new CubieManager(faceCols);
		if (key == '0')
			solve();

		return super.keyDown(evt, key);
	}

	public static String getCubie(FaceColors faceCols, String color) {
		return getCubie(faceCols.colors(), color);
	}

	public static String getColor(FaceColors faceCols, String cubie) {
		return getColor(faceCols.colors(), cubie);
	}

	/**
	 * Get Inverse of move sequences
	 * @param movesToInverse the move sequences to be inverted.
	 * @return The inverse version
	 */
	public static String getInverse(String movesToInverse) {
		Stack<String> stack = new Stack<String>();
		char chars[] = movesToInverse.toCharArray();
		String inversedMoves = "";

		for (int i = 0; i < chars.length;) {
			String item = "";
			switch (chars[i]) {
			case 'U':
			case 'u':
			case 'D':
			case 'd':
			case 'F':
			case 'f':
			case 'B':
			case 'b':
			case 'R':
			case 'r':
			case 'L':
			case 'l':
			case 'M':
			case 'm':
			case 'C':
			case 'c':
				item += chars[i];
				break;
			default:
				throw new IllegalArgumentException("Bad char:" + chars[i]);
			}
			i++;
			if (i < chars.length) {
				switch (chars[i]) {
				case '-':
					i++;
					break;
				case '2':
					item += '2';
					i++;
					break;
				default:
					item += '-';
				}
			} else
				item += '-';
			stack.push(item);
		}

		while (!stack.isEmpty())
			inversedMoves += stack.pop();

		return inversedMoves;
	}
}
