package cube;

import java.awt.*;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Rubik's Cube 3D simulator, Last modified May 19 1998
 * 
 * 
 * @author Neil Rashbrook August 18 1997
 * @author Yan Yufei (modified)
 * @author Shen Zhiwen (modified)
 */
@SuppressWarnings("serial")
public class rubik extends java.applet.Applet implements Runnable {
	private Thread rotator = null;
	private int planes, av;
	private Rectangle bounds;
	private int lastX, lastY;
	private double speed, cube;
	private static final Map<Character, Integer> faceMap = new TreeMap<Character, Integer>();
	static {
		faceMap.put('r', 0);
		faceMap.put('R', 0);
		faceMap.put('u', 1);
		faceMap.put('U', 1);
		faceMap.put('f', 2);
		faceMap.put('F', 2);
		faceMap.put('b', 3);
		faceMap.put('B', 3);
		faceMap.put('d', 4);
		faceMap.put('D', 4);
		faceMap.put('l', 5);
		faceMap.put('L', 5);
	}
	private static final char faceArray[] = { 'R', 'U', 'F', 'B', 'D', 'L' };
	private static final vector faceVec[] = { new vector(1, 0, 0),
			new vector(0, 1, 0), new vector(0, 0, 1), new vector(0, 0, -1),
			new vector(0, -1, 0), new vector(-1, 0, 0) }; // Normal vectors
	private static final vector corners[] = { new vector(1, 1, 1),
			new vector(-1, 1, 1), new vector(1, -1, 1), new vector(1, 1, -1),
			new vector(-1, -1, 1), new vector(-1, 1, -1),
			new vector(1, -1, -1), new vector(-1, -1, -1) }; // Vertex
	// co-ordinates
	private static final corn faceCorn[] = { new corn(0, 3, 2, 1),
			new corn(0, 1, 3, 2), new corn(0, 2, 1, 3), new corn(7, 6, 5, 4),
			new corn(7, 4, 6, 5), new corn(7, 5, 4, 6) }; // Map faces to
	// corners
	private static final int sideFace[][] = { { 1, 3, 4, 2 }, { 2, 5, 3, 0 },
			{ 0, 4, 5, 1 }, { 4, 0, 1, 5 }, { 5, 2, 0, 3 }, { 3, 1, 2, 4 } }; // Which
	// face
	// is
	// Up,
	// Right,
	// Down,
	// Left
	private static final int faceSide[][] = { { 5, 0, 3, 1, 2, 4 },
			{ 3, 5, 0, 2, 4, 1 }, { 0, 3, 5, 4, 1, 2 }, { 1, 2, 4, 5, 0, 3 },
			{ 2, 4, 1, 3, 5, 0 }, { 4, 1, 2, 0, 3, 5 } }; // Which direction a
	// face is in
	private rect sideBlock[][];
	private static final rect sideBlock1[][] = { { null, null, null, null,
			null, new rect(0, 0, 1, 1) } };
	private static final rect sideBlock2[][] = { // Partial faces to draw
			// twisted cube
			{ new rect(0, 0, 2, 1), new rect(1, 0, 2, 2), new rect(0, 1, 2, 2),
					new rect(0, 0, 1, 2), null, new rect(0, 0, 2, 2) },
			{ new rect(0, 1, 2, 2), new rect(0, 0, 1, 2), new rect(0, 0, 2, 1),
					new rect(1, 0, 2, 2), new rect(0, 0, 2, 2), null } };
	private static final rect sideBlock3[][] = { // Partial faces to draw
			// twisted cube
			{ new rect(0, 0, 3, 1), new rect(2, 0, 3, 3), new rect(0, 2, 3, 3),
					new rect(0, 0, 1, 3), null, new rect(0, 0, 3, 3) },
			{ new rect(0, 1, 3, 2), new rect(1, 0, 2, 3), new rect(0, 1, 3, 2),
					new rect(1, 0, 2, 3), null, null },
			{ new rect(0, 2, 3, 3), new rect(0, 0, 1, 3), new rect(0, 0, 3, 1),
					new rect(2, 0, 3, 3), new rect(0, 0, 3, 3), null } };
	private static final rect sideBlock4[][] = { // Partial faces to draw
			// twisted cube
			{ new rect(0, 0, 4, 1), new rect(3, 0, 4, 4), new rect(0, 3, 4, 4),
					new rect(0, 0, 1, 4), null, new rect(0, 0, 4, 4) },
			{ new rect(0, 1, 4, 2), new rect(2, 0, 3, 4), new rect(0, 2, 4, 3),
					new rect(1, 0, 2, 4), null, null },
			{ new rect(0, 2, 4, 3), new rect(1, 0, 2, 4), new rect(0, 1, 4, 2),
					new rect(2, 0, 3, 4), null, null },
			{ new rect(0, 3, 4, 4), new rect(0, 0, 1, 4), new rect(0, 0, 4, 1),
					new rect(3, 0, 4, 4), new rect(0, 0, 4, 4), null } };
	private static final rect sideBlocks[][][] = { null, sideBlock1,
			sideBlock2, sideBlock3, sideBlock4 };
	private static final int circleOrder3[] = { 0, 0, 0, 1, 2, 2, 2, 1, 0, 0 }; // Used
	// to
	// rotate
	// colours
	// on
	// sides
	private static final int circleOrder4[] = { 0, 0, 0, 0, 1, 2, 3, 3, 3, 3,
			2, 1, 0, 0, 0 }; // Used to rotate colours on sides
	private int cornerOrder[];
	private int faceCols[][][]; // Colours on faces [face][row][col]
	// 0-white, 1-red, 2-yellow, 3-green, 4-orange, 5-blue
	private Color colList[][]; // Colour gradients for light calculation
	private Color bgColor, textColor, disabledColor, enabledColor, activeColor;
	private int colMap[];
	private boolean colUsed[];
	private drag dragInfo; // List of drag regions
	private facelet faceInfo; // List of facelets
	private int twistFace = 0, twistMode = 2; // Currently twisting block
	private vector eyeX, eyeY, eyeZ;
	private double phi, phibase = 0, currDragX, currDragY;
	private boolean naturalState = true, twisting = false, calcDrag = true,
			doubleClick = false, OKtoTwist = false;
	private boolean sticky = false, grabrect = false, graball = false,
			plain = false, copyright = false;
	private Graphics offGraphics, tiledGraphics; // For double-buffering
	private Image offImage, tiledImage, bkImage = null;
	private String select, facelets;
	private int backX = 0, backY = 0, curCol = 0, faceCol = 6, iconSize = 0;
	private static final int ctlX[][] = { { 1, 3, 3, 1 }, { -1, -3, -3, -1 },
			{ 1, 2, 2, 3, 3, 2, 2, 1 }, { -1, -2, -2, -3, -3, -2, -2, -1 } };
	private static final int ctlY[][] = { { 2, 1, 3, 2 }, { 2, 1, 3, 2 },
			{ -2, -1, -2, -1, -3, -2, -3, -2 },
			{ -2, -1, -2, -1, -3, -2, -3, -2 } };
	private Polygon ctlP[];
	private boolean ctlM[];
	protected queue moves, undo, redo;
	private Frame cursorFrame;
	private static final float two = 2;
	public static final String colors[] = { "white", "red", "yellow", "green",
			"orange", "blue", "cyan", "magenta", "pink" };
	private static final float hue[] = { 0, 0, two / 12, two / 6, two / 24,
			two / 3, two / 4, two * 5 / 12, 0 };

	public boolean addMove(int face, int quads, int mode) {
		if (face >= 0 && face < 6 && quads > 0 && quads < 4 && mode >= 0
				&& mode < planes) {
			moves.add(new queue(face, quads, mode, planes));
			return true;
		}
		return false;
	}

	private void addMoves(String param, queue redo) {
		char chars[] = param.toCharArray();
		for (int i = 0; i < chars.length;) {
			int face = 0, quads = 3, mode = 0;
			switch (chars[i++]) {
			default:
				continue;
			case 'R':
			case 'r':
				face = 0;
				break;
			case 'U':
			case 'u':
			case 'T':
			case 't':
				face = 1;
				break;
			case 'F':
			case 'f':
				face = 2;
				break;
			case 'B':
			case 'b':
				face = 3;
				break;
			case 'D':
			case 'd':
				face = 4;
				break;
			case 'L':
			case 'l':
				face = 5;
				break;
			case 'C':
			case 'c':
				face = 0;
				mode = 1;
				break;
			case 'M':
			case 'm':
				face = 1;
				mode = 1;
				break;
			}
			if (i < chars.length)
				switch (chars[i]) {
				case '1':
				case 'o':
				case '+':
				case '>':
				case ']':
				case '}':
				//case '�':
					quads = 3;
					i++;
					break;
				case '2':
				case 'i':
				case '.':
				case '=':
				case '\\':
				case '|':
				//case '�':
					quads = 2;
					i++;
					break;
				case '3':
				case 'a':
				case '-':
				case '<':
				case '[':
				case '{':
				//case '�':
					quads = 1;
					i++;
					break;
				}
			if (planes > 2 && i < chars.length)
				switch (chars[i]) {
				case '^':
				case '~':
				case '_':
				case '?':
				case '%':
					mode = 1;
					i++;
				}
			if (redo == null)
				colorTwist(faceCols, face, quads == 0 ? 3 : quads, mode);
			else if (quads == 0)
				redo.add(face, 3, mode, planes);
			else
				redo.add(new queue(face, quads, mode, planes));
			mode = 0;
		}
	}

	public void addMoves(int[][][] faceCols, String param) {
		char chars[] = param.toCharArray();
		for (int i = 0; i < chars.length;) {
			int face = 0, quads = 3, mode = 0;
			switch (chars[i++]) {
			default:
				continue;
			case 'R':
			case 'r':
				face = 0;
				break;
			case 'U':
			case 'u':
			case 'T':
			case 't':
				face = 1;
				break;
			case 'F':
			case 'f':
				face = 2;
				break;
			case 'B':
			case 'b':
				face = 3;
				break;
			case 'D':
			case 'd':
				face = 4;
				break;
			case 'L':
			case 'l':
				face = 5;
				break;
			case 'C':
			case 'c':
				face = 0;
				mode = 1;
				break;
			case 'M':
			case 'm':
				face = 1;
				mode = 1;
				break;
			}
			if (i < chars.length)
				switch (chars[i]) {
				case '1':
				case 'o':
				case '+':
				case '>':
				case ']':
				case '}':
				//case '�':
					quads = 3;
					i++;
					break;
				case '2':
				case 'i':
				case '.':
				case '=':
				case '\\':
				case '|':
				//case '�':
					quads = 2;
					i++;
					break;
				case '3':
				case 'a':
				case '-':
				case '<':
				case '[':
				case '{':
				//case '�':
					quads = 1;
					i++;
					break;
				}
			if (planes > 2 && i < chars.length)
				switch (chars[i]) {
				case '^':
				case '~':
				case '_':
				case '?':
				case '%':
					mode = 1;
					i++;
				}
			moves.add(face, quads, mode, planes);
			colorTwist(faceCols, face, quads == 0 ? 3 : quads, mode);
		}
	}

	private void colorTwist(int faceCols[][][], int faceNum, int quads, int mode) { // Shift
		// colored
		// squares
		int i, j, k, l;
		int buffer[] = new int[planes * 4];
		if (mode == 0) { // Mode is 1 (middle) or 0 (face)
			switch (planes) {
			case 4:
				for (i = 0; i < 12; i++)
					// Read existing colours
					buffer[i] = faceCols[faceNum][circleOrder4[i]][circleOrder4[i + 3]];
				j = quads * 3; // Write colours offset
				for (i = 0; i < 12; i++) {
					faceCols[faceNum][circleOrder4[i]][circleOrder4[i + 3]] = buffer[j];
					j = (j + 1) % 12;
				}
				buffer[0] = faceCols[faceNum][1][1];
				buffer[1] = faceCols[faceNum][1][2];
				buffer[2] = faceCols[faceNum][2][2];
				buffer[3] = faceCols[faceNum][2][1];
				faceCols[faceNum][1][1] = buffer[quads];
				faceCols[faceNum][1][2] = buffer[(quads + 1) & 3];
				faceCols[faceNum][2][2] = buffer[(quads + 2) & 3];
				faceCols[faceNum][2][1] = buffer[(quads + 3) & 3];
				break;
			case 3:
				for (i = 0; i < 8; i++)
					// Read existing colours
					buffer[i] = faceCols[faceNum][circleOrder3[i]][circleOrder3[i + 2]];
				j = quads * 2; // Write colours offset
				for (i = 0; i < 8; i++) {
					faceCols[faceNum][circleOrder3[i]][circleOrder3[i + 2]] = buffer[j];
					j = (j + 1) & 7;
				}
				break;
			case 2:
				buffer[0] = faceCols[faceNum][0][0];
				buffer[1] = faceCols[faceNum][0][1];
				buffer[2] = faceCols[faceNum][1][1];
				buffer[3] = faceCols[faceNum][1][0];
				faceCols[faceNum][0][0] = buffer[quads];
				faceCols[faceNum][0][1] = buffer[(quads + 1) & 3];
				faceCols[faceNum][1][1] = buffer[(quads + 2) & 3];
				faceCols[faceNum][1][0] = buffer[(quads + 3) & 3];
				break;
			}
		}
		j = 0;
		for (i = 0; i < 4; i++) { // Read existing colours
			l = sideFace[faceNum][i];
			switch (faceSide[l][faceNum]) {
			case 0:
				for (k = 0; k < planes; k++)
					buffer[j++] = faceCols[l][mode][k];
				break;
			case 1:
				for (k = 0; k < planes; k++)
					buffer[j++] = faceCols[l][k][planes - 1 - mode];
				break;
			case 2:
				for (k = 0; k < planes; k++)
					buffer[j++] = faceCols[l][planes - 1 - mode][planes - 1 - k];
				break;
			case 3:
				for (k = 0; k < planes; k++)
					buffer[j++] = faceCols[l][planes - 1 - k][mode];
				break;
			}
		}
		j = quads * planes; // Write colours offset
		for (i = 0; i < 4; i++) {
			l = sideFace[faceNum][i];
			switch (faceSide[l][faceNum]) {
			case 0:
				for (k = 0; k < planes; k++)
					faceCols[l][mode][k] = buffer[j++];
				break;
			case 1:
				for (k = 0; k < planes; k++)
					faceCols[l][k][planes - 1 - mode] = buffer[j++];
				break;
			case 2:
				for (k = 0; k < planes; k++)
					faceCols[l][planes - 1 - mode][planes - 1 - k] = buffer[j++];
				break;
			case 3:
				for (k = 0; k < planes; k++)
					faceCols[l][planes - 1 - k][mode] = buffer[j++];
				break;
			}
			j %= planes * 4;
		}
	}

	public void destroy() {
		rotator.interrupt();
	}

	private void drawPolygon(int i) {
		offGraphics.setColor(ctlM[i] ? activeColor : disabledColor);
		offGraphics.fillPolygon(ctlP[i]);
		offGraphics.setColor(textColor);
		offGraphics.drawPolygon(ctlP[i]);
	}

	private void findCol(int face) {
		int color = colMap[face];
		if (color >= 0)
			colUsed[color] = false;
		do
			if (++color == 9)
				color = 0;
		while (colUsed[color]);
		colUsed[color] = true;
		colMap[face] = color;
	}

	private Color findColor(String name, Color value) { // Convert hexadecimal
		// RGB parameter to
		// color
		try {
			return new Color(Integer.parseInt(getParameter(name), 16));
		} catch (Exception e) {
			return value;
		}
	}

	private vector findEye(String param) {
		try {
			return new vector(getDouble(param + "x"), getDouble(param + "y"),
					getDouble(param + "z"));
		} catch (Exception e) {
			return null;
		}
	}

	private void fixBlock(vector beyeZ, vector beyeX, vector beyeY, int mode) { // Draw
		// cube
		// or
		// sub-cube
		double xCoord[] = new double[8], yCoord[] = new double[8]; // Projected
		// co-ordinates
		// (on
		// screen)
		vector light = new vector(beyeX);
		light.sub(beyeY);
		light.addmult(beyeZ, -3);
		for (int i = 0; i < 8; i++) { // Project 3D co-ordinates into 2D screen
			// ones
			xCoord[i] = (bounds.width * 0.5 + cube * beyeX.mult(corners[i]));
			yCoord[i] = (bounds.height * 0.5 - cube * beyeY.mult(corners[i]));
		}
		for (int i = 0; i < 6; i++)
			if (beyeZ.mult(faceVec[i]) > 0.001) { // Face towards us? Draw it.
				double sx = xCoord[faceCorn[i].nw];
				double sy = yCoord[faceCorn[i].nw];
				double sdxh = (xCoord[faceCorn[i].ne] - sx) / planes;
				double sdxv = (xCoord[faceCorn[i].sw] - sx) / planes;
				double sdyh = (yCoord[faceCorn[i].ne] - sy) / planes;
				double sdyv = (yCoord[faceCorn[i].sw] - sy) / planes;
				int j = faceSide[i][twistFace];
				rect block = mode < 0 ? sideBlock[0][5] : sideBlock[mode][j];
				if (block == null) { // Just draw blank
					int k = i == twistFace ? mode : planes - 1 - mode; // Move
					// face
					// away
					sx = (sx * (planes - k) + xCoord[faceCorn[i].bk] * k)
							/ planes;
					sy = (sy * (planes - k) + yCoord[faceCorn[i].bk] * k)
							/ planes;
					offGraphics.setColor(Color.black);
					offGraphics.fillPolygon(new paral(sx, sy, sdxh, sdyh, sdxv,
							sdyv, sideBlock[0][5]));
					offGraphics.drawPolygon(new paral(sx, sy, sdxh, sdyh, sdxv,
							sdyv, sideBlock[0][5]));
				} else { // First draw blank
					paral p = new paral(sx, sy, sdxh, sdyh, sdxv, sdyv, block);
					offGraphics.setColor(Color.black);
					offGraphics.fillPolygon(p);
					offGraphics.drawPolygon(p);
					int thisFaceCols[][] = faceCols[i];
					Color thisList[] = colList[plain ? 19
							: (int) (13 * (0.5 - light.cosAng(faceVec[i])))];
					// Color thisList[] = colList[(int)(9.6 * (1 -
					// light.cosAng(faceVec[i])))];
					// Try also (int)(13 * (0.5 - light.cosAng(faceVec[i])))
					for (int k = block.top; k < block.bottom; k++) { // Then
						// draw
						// colored
						// squares
						int thisRowCols[] = thisFaceCols[k];
						for (int l = block.left; l < block.right; l++) {
							paral pp = new paral(sx, sy, sdxh, sdyh, sdxv,
									sdyv, l, k);
							Color c = thisList[colMap[thisRowCols[l]]];
							offGraphics.setColor(c);
							offGraphics.fillPolygon(pp);
							offGraphics.setColor(thisList[10]);
							offGraphics.drawPolygon(pp);
							if (calcDrag && !copyright)
								faceInfo = new facelet(faceInfo, i, k, l, pp);
						}
					}
					if (calcDrag) { // Determine allowed drag regions and
						// directions
						if (mode < 0) {
							if (!grabrect)
								switch (planes) {
								case 4: // Eight drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											1, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][3], planes,
													graball, false));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											1, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][2], planes,
													graball, true));
								case 3: // Six drag regions
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											1, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][1], planes,
													graball, false));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											1, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][0], planes,
													graball, true));
								case 2: // Four drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											0, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][3], planes,
													graball, false));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											0, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][2], planes,
													graball, true));
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											0, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][1], planes,
													graball, false));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											0, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][0], planes,
													graball, true));
								}
							else if (!graball)
								switch (planes) {
								case 4: // Eight drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											1, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][3], false));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											1, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][2], true));
								case 3: // Six drag regions
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											1, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][1], false));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											1, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][0], true));
								case 2: // Four drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											0, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][3], false));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											0, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][2], true));
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											0, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][1], false));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											0, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][0], true));
								}
							else
								switch (planes) {
								case 4: // Eight drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											1, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][3]));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											1, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][2]));
								case 3: // Six drag regions
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											1, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][1]));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											1, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[1][0]));
								case 2: // Four drag regions
									dragInfo = new drag(dragInfo, -sdxv, -sdyv,
											0, sideFace[i][3], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][3]));
									dragInfo = new drag(dragInfo, -sdxh, -sdyh,
											0, sideFace[i][2], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][2]));
									dragInfo = new drag(dragInfo, sdxv, sdyv,
											0, sideFace[i][1], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][1]));
									dragInfo = new drag(dragInfo, sdxh, sdyh,
											0, sideFace[i][0], new paral(sx,
													sy, sdxh, sdyh, sdxv, sdyv,
													sideBlock[0][0]));
								}
						} else if (mode == twistMode && i != twistFace) { // The
							// small
							// sub-cube
							// (twistable
							// part)
							switch (j) { // Determine drag direction
							case 0:
								dragInfo = new drag(dragInfo, sdxh, sdyh,
										twistMode, twistFace, p);
								break;
							case 1:
								dragInfo = new drag(dragInfo, sdxv, sdyv,
										twistMode, twistFace, p);
								break;
							case 2:
								dragInfo = new drag(dragInfo, -sdxh, -sdyh,
										twistMode, twistFace, p);
								break;
							case 3:
								dragInfo = new drag(dragInfo, -sdxv, -sdyv,
										twistMode, twistFace, p);
								break;
							}
						}
					}
				}
			}
	}

	public String getAppletInfo() { // for appletviewer 1.1
		return "Rubik's Cube� Applet � 1997-8 Neil Rashbrook";
	}

	private double getDouble(String param) throws Exception {
		return Double.valueOf(getParameter(param)).doubleValue();
	}

	public int getFacelet(int face, int row, int col) {
		if (face >= 0 && face < 6 && row >= 0 && row < planes && col >= 0
				&& col < planes)
			return faceCols[face][row][col];
		else
			return -1;
	}

	private char[] getFacelets() {
		char facechars[] = new char[planes * planes * 6];
		int l = 0;
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < planes; j++)
				for (int k = 0; k < planes; k++)
					facechars[l++] = (char) (faceCols[i][j][k] + '0');
		return facechars;
	}

	public String[][] getParameterInfo() { // for appletviewer 1.1
		return new String[][] {
				{ "alink", "000000-FFFFFF", "Arrow highlight color" },
				{ "background", "url", "Background image" },
				{ "backx", "int", "Background width adjustment" },
				{ "backy", "int", "Background height adjustment" },
				{ "bgcolor", "000000-FFFFFF", "Background color" },
				{ "eyeXx", "double", "EyeX vector x component" },
				{ "eyeXy", "double", "EyeX vector y component" },
				{ "eyeXz", "double", "EyeX vector z component" },
				{ "eyeYx", "double", "EyeY vector x component" },
				{ "eyeYy", "double", "EyeY vector y component" },
				{ "eyeYz", "double", "EyeY vector z component" },
				{ "eyeZx", "double", "EyeZ vector x component" },
				{ "eyeZy", "double", "EyeZ vector y component" },
				{ "eyeZz", "double", "EyeZ vector z component" },
				{ "face0", "String", "Face 0 [default white]" },
				{ "face1", "String", "Face 1 [default red]" },
				{ "face2", "String", "Face 2 [default yellow]" },
				{ "face3", "String", "Face 3 [default green]" },
				{ "face4", "String", "Face 4 [default orange]" },
				{ "face5", "String", "Face 5 [default blue]" },
				{ "facelets1", "String", "1x1x1 facelets" },
				{ "facelets2", "String", "2x2x2 facelets" },
				{ "facelets3", "String", "3x3x3 facelets" },
				{ "facelets4", "String", "4x4x4 facelets" },
				{ "focus", "boolean", "Request focus" },
				{ "graball", "boolean", "Grab on all of face" },
				{ "grabrect", "boolean", "Grab rectangles" },
				{ "link", "000000-FFFFFF", "Arrow body color" },
				{ "moves2", "String", "2x2x2 initial moves" },
				{ "moves3", "String", "3x3x3 initial moves" },
				{ "moves4", "String", "4x4x4 initial moves" },
				{ "planes", "int", "Initial cube size (default 3)" },
				{ "rotation", "int",
						"1-3 radians per second, 0 jumps [default 2]" },
				{ "select", "String", "Selectable planes [default 1, 2, 3, 4]" },
				{ "steps2", "String", "2x2x2 script steps" },
				{ "steps3", "String", "3x3x3 script steps" },
				{ "steps4", "String", "4x4x4 script steps" },
				{ "sticky", "boolean", "Allow sticky dragging" },
				{ "text", "000000-FFFFFF", "Arrow border color" },
				{ "vlink", "000000-FFFFFF", "Arrow unused color" } };
	}

	private int getPlanes() {
		String s = getParameter("planes");
		if ("1".equals(s))
			return 1;
		if ("2".equals(s))
			return 2;
		if ("4".equals(s))
			return 4;
		return 3;
	}

	private int getRotation() {
		String s = getParameter("rotation");
		if ("0".equals(s))
			return 0;
		if ("1".equals(s))
			return 1;
		if ("3".equals(s))
			return 3;
		return 2;
	}

	private void home() {
		try {
			getAppletContext().showDocument(
					new java.net.URL("http://www.neil.parkwaycc.co.uk/rubik/"),
					"_blank");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public boolean imageUpdate(Image img, int flags, int x, int y, int width,
			int height) {
		if ((flags & ALLBITS) != 0) {
			tiledImage = null;
			repaint(1);
		}
		return (flags & (ALLBITS | ABORT)) == 0;
	}

	public void init() {
		Container comp = this;
		while (comp != null && !(comp instanceof Frame))
			comp = comp.getParent();
		cursorFrame = (Frame) comp;
		moves = new queue();
		undo = new queue();
		// redo = new queue();
		Polygon p = new Polygon();
		ctlP = new Polygon[] { p, p, p, p };
		ctlM = new boolean[4];
		cornerOrder = new int[6];
		faceCols = new int[6][4][4];
		colList = new Color[20][11];
		for (int i = 0; i < 20; i++) { // Generate colour gradients
			float s = (85 - i) / 85f, b = (i + i + 19) / 57f;
			colList[i][0] = Color.getHSBColor(0, 0, b);
			for (int j = 1; j < 8; j++)
				colList[i][j] = Color.getHSBColor(hue[j], s, b);
			colList[i][8] = Color.getHSBColor(0, (85 - i) / 115f, b);
			colList[i][9] = Color.getHSBColor(0, 0, b / 2);
			colList[i][10] = Color.black;
		}
		// Load parmeters
		av = getRotation();
		select = getParameter("select");
		if (select == null)
			select = "1234";
		copyright = "Neil Rashbrook"
				.equalsIgnoreCase(getParameter("copyright"));
		sticky = "true".equalsIgnoreCase(getParameter("sticky"));
		grabrect = "true".equalsIgnoreCase(getParameter("grabrect"));
		graball = "true".equalsIgnoreCase(getParameter("graball"));
		bgColor = findColor("bgcolor", Color.lightGray);
		textColor = findColor("text", Color.black);
		disabledColor = findColor("link", Color.blue);
		enabledColor = findColor("vlink", Color.magenta);
		activeColor = findColor("alink", Color.red);
		eyeX = findEye("eyeX");
		eyeY = findEye("eyeY");
		eyeZ = findEye("eyeZ");
		if (eyeY == null && (eyeX == null || eyeZ == null) || eyeX == null
				&& eyeZ == null) {
			eyeZ = new vector(0.8560, 0.1344, 0.4992); // Initial observer
			// co-ordinate axes
			// (view)
			eyeX = new vector(0.4992, 0.0656, -0.8640); // (sideways)
			eyeY = new vector(eyeZ, eyeX);
		}
		if (eyeY == null)
			eyeY = new vector(eyeZ, eyeX);
		else if (eyeX == null)
			eyeX = new vector(eyeY, eyeZ);
		else if (eyeZ == null)
			eyeZ = new vector(eyeX, eyeY);
		eyeX.addmultnorm(eyeZ, 0.2);
		eyeZ.multnorm(eyeX, eyeY);
		eyeZ.addmultnorm(eyeY, 0.2);
		eyeY.multnorm(eyeZ, eyeX);
		colMap = new int[8];
		colUsed = new boolean[9];
		for (int i = 0; i < 6; i++) {
			String s = getParameter("face" + i);
			int j = 8;
			while (j >= 0 && (colUsed[j] || !colors[j].equalsIgnoreCase(s)))
				j--;
			if (j >= 0)
				colUsed[colMap[i] = j] = true;
			else
				colMap[i] = -1;
		}
		for (int i = 0; i < 6; i++)
			if (colMap[i] < 0)
				findCol(i);
		colMap[6] = 9;
		colMap[7] = 10;
		rotator = new Thread(this, "Rotator");
		rotator.setDaemon(true);
		rotator.start();
		setPlanes(getPlanes());
		if ("true".equals(getParameter("focus")))
			try {
				requestFocus(); // for appletviewer 1.1
			} catch (Exception e) {
			}
	}

	public boolean isFocusTraversable() { // for appletviewer 1.1
		return true;
	}

	public boolean keyDown(Event evt, int key) {
		if ((evt.modifiers & Event.ALT_MASK) != 0)
			return false;
		if (key >= 1002 && key <= 1007) {
			switch (key) {
			case 1002: // PgUp
				eyeY.addmultnorm(eyeX, 2 / speed);
				eyeX.multnorm(eyeY, eyeZ);
				break;
			case 1003: // PgDn
				eyeY.addmultnorm(eyeX, -2 / speed);
				eyeX.multnorm(eyeY, eyeZ);
				break;
			case 1004: // Up
				eyeZ.addmultnorm(eyeY, -2 / speed);
				eyeY.multnorm(eyeZ, eyeX);
				break;
			case 1005: // Down
				eyeZ.addmultnorm(eyeY, 2 / speed);
				eyeY.multnorm(eyeZ, eyeX);
				break;
			case 1006: // Left
				eyeX.addmultnorm(eyeZ, -2 / speed);
				eyeZ.multnorm(eyeX, eyeY);
				break;
			case 1007: // Right
				eyeX.addmultnorm(eyeZ, 2 / speed);
				eyeZ.multnorm(eyeX, eyeY);
				break;
			}
			calcDrag = true;
			repaint(1);
			return true;
		}
		if (key >= 64 && key < 128)
			key &= 31;
		switch (key) {
		case ',':
			moves.append(undo, true);
			break;
		case '.':
			moves.append(redo, true);
			break;
		case '<':
			moves.append(undo, false);
			break;
		case '>':
			moves.append(redo, false);
			break;
		case 49:
		case 50:
		case 51:
		case 52: // 1, 2, 3, 4
			if (select.indexOf(key) >= 0 && moves.iswaiting())
				setPlanes(key - 48);
			return true;
		case 1: // a
			if (evt.controlDown())
				av = evt.shiftDown() ? 0 : 1;
			else
				av = evt.shiftDown() ? 3 : 2;
			return true;
		case 2: // b
			rotate(eyeX, eyeY, eyeZ, 1, evt);
			return true;
		case 3: // c
			moves.add(0, 3, 1, planes);
			return true;
		case 4: // d
			rotate(eyeY, eyeZ, eyeX, -1, evt);
			return true;
		case 5:
			if (evt.controlDown())
				curCol = (curCol + 1) % 6;
			else if (evt.shiftDown()) {
				findCol(curCol);
				repaint(1);
			} else if (moves.iswaiting())
				setPlanes(planes); // Clear
			return true;
		case 6: // f
			rotate(eyeX, eyeY, eyeZ, -1, evt);
			return true;
		case 7: // g
			grabrect = evt.shiftDown();
			graball = evt.controlDown();
			calcDrag = true;
			repaint(1);
			return true;
		case 8: // h
			home();
			return true;
		case 12: // l
			rotate(eyeZ, eyeX, eyeY, -1, evt);
			return true;
		case 13: // m
			moves.add(1, 3, 1, planes);
			return true;
		case 16: // p
			plain = evt.shiftDown();
			repaint(1);
			return true;
		case 18: // r
			rotate(eyeZ, eyeX, eyeY, 1, evt);
			return true;
		case 19: // s
			if (evt.controlDown())
				sticky = evt.shiftDown();
			else if (facelets == null && planes > 1) {
				calcDrag = false;
				dragInfo = null;
				if (cursorFrame != null)
					cursorFrame.setCursor(Cursor.getDefaultCursor());
				int old = -1;
				for (int i = 0; i < 10; i++) {
					int face;
					do
						face = (int) (Math.random() * 3);
					while (face == old);
					moves.add(face, (int) (Math.random() * 3) + 1, (int) (Math
							.random() * planes), planes);
					old = face;
				}
				return true;
			}
			break;
		case 20:
		case 21: // t, u
			rotate(eyeY, eyeZ, eyeX, 1, evt);
			return true;
		case 23: // w
			writeParameters();
			return true;
		}
		return false;
	}

	public boolean mouseDown(Event evt, int x, int y) {
		if (evt.metaDown()) {
			doubleClick = false;
			if (moves.iswaiting()) {
				for (facelet info = faceInfo; info != null; info = info.next) {
					if (info.poly.contains(x, y)) {
						if (evt.controlDown()) {
							if (evt.shiftDown())
								faceCols[info.face][info.row][info.col] = (faceCols[info.face][info.row][info.col] + 1) & 7;
							else
								faceCols[info.face][info.row][info.col] = faceCols[info.face][info.row][info.col] >= 6 ? info.face
										: 6;
						} else {
							if (evt.shiftDown())
								faceCols[info.face][info.row][info.col] = faceCol;
							else {
								faceCol = faceCols[info.face][info.row][info.col];
								break;
							}
						}
						facelets = "";
						repaint(1);
						break;
					}
				}
			}
		} else if (evt.shiftDown()) {
			if (moves.iswaiting()) {
				for (drag info = dragInfo; info != null; info = info.next) { // Check
																				// if
																				// inside
																				// a
																				// drag
																				// region
					if (info.poly.contains(x, y)) {
						moves.add(new queue(info.face, evt.controlDown() ? 2
								: 1, info.mode, planes));
						break;
					}
				}
			}
		} else if (evt.controlDown()) {
			if (moves.iswaiting()) {
				for (drag info = dragInfo; info != null; info = info.next) { // Check
																				// if
																				// inside
																				// a
																				// drag
																				// region
					if (info.poly.contains(x, y)) {
						moves.add(new queue(info.face, 3, info.mode, planes));
						break;
					}
				}
			}
		} else {
			// for (int i = 0; i < 4; i++) if (ctlM[i] && moves.append((i & 1)
			// != 0 ? redo : undo, (i & 2) != 0) && !rotator.isAlive())
			// rotator.start();
			for (int i = 0; i < 4; i++)
				if (ctlM[i]
						&& moves.append((i & 1) != 0 ? redo : undo,
								(i & 2) != 0))
					doubleClick = false;
			if (doubleClick && evt.clickCount > 1)
				home();
			else {
				doubleClick = true;
				if (!twisting) {
					lastX = x;
					lastY = y;
					OKtoTwist = moves.iswaiting();
				}
				if (!moves.iswaiting() && cursorFrame != null)
					cursorFrame.setCursor(Cursor.getDefaultCursor());
			}
		}
		return true;
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		doubleClick = false;
		if (!evt.metaDown()) {
			int dx = x - lastX, dy = y - lastY;
			if (OKtoTwist) {
				for (drag info = dragInfo; info != null; info = info.next) { // Check
																				// if
																				// inside
																				// a
																				// drag
																				// region
					if (info.poly.contains(lastX, lastY)) {
						currDragX = info.dirX;
						currDragY = info.dirY;
						double d = currDragX * dx + currDragY * dy;
						d = d
								* d
								/ ((currDragX * currDragX + currDragY
										* currDragY) * (dx * dx + dy * dy));
						if (d > 0.7) { // Dragging the right way
							twistMode = info.mode;
							twistFace = info.face;
							naturalState = false;
							twisting = true;
							d = Math.sqrt(currDragX * currDragX + currDragY
									* currDragY)
									* speed;
							currDragX /= d;
							currDragY /= d;
							if (planes == 3 && twistMode == 1 && twistFace >= 3) {
								twistFace = 5 - twistFace;
								currDragX = -currDragX;
								currDragY = -currDragY;
							}
							break;
						}
					}
				}
			}
			OKtoTwist = false;
			if (twisting) // Twist, compute twisting angle phi
				phi = currDragX * (x - lastX) + currDragY * (y - lastY);
			else { // Normal rotation
			// Vertical shift
				eyeZ.addmultnorm(eyeX, -dx / speed);
				eyeX.multnorm(eyeY, eyeZ);
				// Horizontal shift
				eyeZ.addmultnorm(eyeY, dy / speed);
				eyeY.multnorm(eyeZ, eyeX);
				lastX = x;
				lastY = y;
			}
			repaint(1);
		}
		return true;
	}

	public boolean mouseEnter(Event evt, int x, int y) {
		return true;
	}

	public boolean mouseExit(Event evt, int x, int y) {
		for (int i = 0; i < 4; i++) {
			if (ctlM[i] && moves.iswaiting())
				repaint(1);
			ctlM[i] = false;
		}
		return false;
	}

	public boolean mouseMove(Event evt, int x, int y) {
		int nCursor = Cursor.DEFAULT_CURSOR;
		for (int i = 0; i < 4; i++) {
			boolean b = ctlP[i].contains(x, y);
			if (ctlM[i] != b && moves.iswaiting())
				repaint(1);
			ctlM[i] = b;
			if (b && naturalState && moves.isempty())
				switch (i) {
				case 0:
					if (!undo.isempty())
						nCursor = Cursor.HAND_CURSOR;
					break;
				case 1:
					if (redo != null && !redo.isempty()
							&& !redo.startsWith(undo))
						nCursor = Cursor.HAND_CURSOR;
					break;
				case 2:
					if (!undo.isnearempty())
						nCursor = Cursor.HAND_CURSOR;
					break;
				case 3:
					if (redo != null && !redo.isnearempty())
						nCursor = Cursor.HAND_CURSOR;
					break;
				}
		}
		if (cursorFrame != null) {
			for (drag info = dragInfo; info != null; info = info.next) { // Check
																			// if
																			// inside
																			// a
																			// drag
																			// region
				if (info.poly.contains(x, y)) {
					if (nCursor != Cursor.DEFAULT_CURSOR)
						nCursor = Cursor.MOVE_CURSOR;
					else {
						currDragX = info.dirX;
						currDragY = info.dirY;
						double d0 = currDragY
								* currDragY
								/ ((currDragX * currDragX + currDragY
										* currDragY));
						nCursor = Cursor.N_RESIZE_CURSOR;
						for (int dy = -1; dy <= 1; dy++) {
							double d = currDragX + currDragY * dy;
							d = d
									* d
									/ ((currDragX * currDragX + currDragY
											* currDragY) * (1 + dy * dy));
							if (d > d0) {
								d0 = d;
								if (dy < 0)
									nCursor = Cursor.NE_RESIZE_CURSOR;
								else if (dy > 0)
									nCursor = Cursor.SE_RESIZE_CURSOR;
								else
									nCursor = Cursor.E_RESIZE_CURSOR;
							}
						}
					}
				}
			}
			cursorFrame.setCursor(new Cursor(nCursor));
		}
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y) {
		if (twisting) { // We have let go of the mouse when twisting
			twisting = false;
			phibase += phi; // Save twist angle
			phi = 0;
			if (!sticky)
				moves.add(new queue(twistFace,
						phibase < 0 ? -(int) (0.5 - phibase * 2 / Math.PI)
								: (int) (0.5 + phibase * 2 / Math.PI),
						twistMode, planes));
			else {
				double qu = phibase;
				while (qu < 0)
					qu += 40 * Math.PI;
				int quads = ((int) (qu * 10 / Math.PI));
				if (quads % 5 == 0 || quads % 5 == 4) { // Close enough to a
														// corner?
					quads = ((quads + 1) / 5) & 3;
					if (quads != 0) {
						colorTwist(faceCols, twistFace, quads, twistMode); // And
																			// shift
																			// the
																			// colored
																			// squares
						redo.sub(twistFace, 4 - quads, twistMode, planes);
						undo.sub(twistFace, 4 - quads, twistMode, planes);
					}
					phibase = 0;
					naturalState = true; // Return the cube to its natural state
				}
			}
		}
		if (moves.iswaiting()) {
			calcDrag = true;
			paint(getGraphics());
			mouseMove(evt, x, y);
		}
		return true;
	}

	public synchronized void paint(Graphics g) {
		Color bgColor = this.bgColor == null ? getBackground() : this.bgColor;
		if (!getBounds().equals(bounds)) { // for appletviewer 1.1
			bounds = getBounds();
			speed = Math.min(bounds.width, bounds.height) / 3.0;
			cube = speed * Math.sqrt(0.75);
			offImage = createImage(bounds.width, bounds.height); // Double
			// buffer
			offGraphics = offImage.getGraphics();
			iconSize = Math.min(bounds.width, bounds.height) / 20;
			int tmpX[] = new int[8];
			int tmpY[] = new int[8];
			for (int i = 0; i < 4; i++)
				ctlP[i] = new Polygon(resize(tmpX, ctlX[i], iconSize,
						bounds.width), resize(tmpY, ctlY[i], iconSize,
						bounds.height), ctlX[i].length);
		}
		if (bkImage == null || (checkImage(bkImage, this) & ALLBITS) == 0) {
			offGraphics.setColor(bgColor); // Clear drawing buffer
			offGraphics.fillRect(0, 0, bounds.width, bounds.height);
		} else { // Fill background
			if (tiledImage == null) {
				tiledImage = createImage(bounds.width, bounds.height);
				tiledGraphics = tiledImage.getGraphics();
				int imgWidth = bkImage.getWidth(this);
				int imgHeight = bkImage.getHeight(this);
				for (int i = backX; i < bounds.width; i += imgWidth) {
					for (int j = backY; j < bounds.height; j += imgHeight) {
						tiledGraphics.drawImage(bkImage, i, j, bgColor, this);
					}
				}
			}
			offGraphics.drawImage(tiledImage, 0, 0, this);
		}
		if (calcDrag) {
			dragInfo = null;
			faceInfo = null;
		}
		if (naturalState)
			fixBlock(eyeZ, eyeX, eyeY, -1); // Draw cube
		else {
			vector TeyeZ = new vector(eyeZ); // In twisted state? Compute top
			// observer
			vector TeyeX = new vector(eyeX);
			double Cphi = Math.cos(phi + phibase);
			double Sphi = -Math.sin(phi + phibase);
			switch (twistFace) { // Twist around which axis?
			case 0: // -x
				TeyeZ.rotateX(Cphi, -Sphi);
				TeyeX.rotateX(Cphi, -Sphi);
				break;
			case 1: // y
				TeyeZ.rotateY(Cphi, Sphi);
				TeyeX.rotateY(Cphi, Sphi);
				break;
			case 2: // -z
				TeyeZ.rotateZ(Cphi, -Sphi);
				TeyeX.rotateZ(Cphi, -Sphi);
				break;
			case 3: // z
				TeyeZ.rotateZ(Cphi, Sphi);
				TeyeX.rotateZ(Cphi, Sphi);
				break;
			case 4: // -y
				TeyeZ.rotateY(Cphi, -Sphi);
				TeyeX.rotateY(Cphi, -Sphi);
				break;
			case 5: // x
				TeyeZ.rotateX(Cphi, Sphi);
				TeyeX.rotateX(Cphi, Sphi);
				break;
			}
			vector TeyeY = new vector(TeyeZ, TeyeX);
			if (eyeZ.mult(faceVec[twistFace]) < 0) { // Top facing away? Draw it
				// first
				for (int i = 0; i < planes; i++) {
					if (twistMode == i)
						fixBlock(TeyeZ, TeyeX, TeyeY, i);
					else
						fixBlock(eyeZ, eyeX, eyeY, i);
				}
			} else {
				for (int i = planes; i-- > 0;) {
					if (twistMode == i)
						fixBlock(TeyeZ, TeyeX, TeyeY, i);
					else
						fixBlock(eyeZ, eyeX, eyeY, i);
				}
			}
		}
		calcDrag = false;
		if (naturalState && moves.isempty()) {
			if (!undo.isempty())
				drawPolygon(0);
			if (redo != null && !redo.isempty() && !redo.startsWith(undo))
				drawPolygon(1);
			if (!undo.isnearempty())
				drawPolygon(2);
			if (redo != null && !redo.isnearempty())
				drawPolygon(3);
		}
		g.drawImage(offImage, 0, 0, this);
		notifyAll();
	}

	private void perform(queue move) throws InterruptedException {
		showStatus("Applet rubik running..." + moves.length());
		
		rotate(move);
		if (moves.isempty()) {
			showStatus("Applet rubik waiting");
			phibase = 0;
			phi = 0;
			calcDrag = true;
			repaint(1);
		}
	}

	private int[] resize(int[] result, int[] points, int iconSize, int bound) {
		for (int i = 0; i < points.length; i++) {
			result[i] = points[i] * iconSize;
			if (points[i] < 0)
				result[i] += bound;
		}
		return result;
	}

	protected synchronized void rotate(queue move) throws InterruptedException {
		twisting = false;
		double phistop;
		int quads = move.quads;
		if (!naturalState && move.face == twistFace && move.mode == twistMode) {
			phistop = Math.PI / 2 * quads - phibase;
			quads &= 3;
		} else {
			phi = phibase = 0;
			if (move.quads == 1)
				phistop = Math.PI / 2;
			else if (move.quads == 3)
				phistop = -Math.PI / 2;
			else if (Math.random() < 0.5)
				phistop = Math.PI;
			else
				phistop = -Math.PI;
			twistFace = move.face;
			twistMode = move.mode;
		}
		if (av != 0) {
			naturalState = false;
			double av = this.av * 0.001;
			long time = System.currentTimeMillis();
			if (phistop > 0)
				while (phi < phistop) {
					repaint(1);
					wait();
					phi = (System.currentTimeMillis() - time) * av;
				}
			else
				while (phi > phistop) {
					repaint(1);
					wait();
					phi = (time - System.currentTimeMillis()) * av;
				}
		}
		naturalState = true;
		if (quads != 0) {
			colorTwist(faceCols, twistFace, quads, twistMode);
			if (redo != null)
				redo.sub(twistFace, 4 - quads, twistMode, planes);
			undo.sub(twistFace, 4 - quads, twistMode, planes);
		}
	}

	private void rotate(vector eye, vector eye1, vector eye2, int dir, Event evt) {
		double best = 0;
		int face = -1;
		for (int i = 0; i < 6; i++) {
			double temp = eye.mult(faceVec[i]) * dir;
			if (temp > best && temp > eye1.mult(faceVec[i]) * dir
					&& temp > eye2.mult(faceVec[i]) * dir) {
				best = temp;
				face = i;
			}
		}
		moves.add(new queue(face, evt.shiftDown() ? 1 : 3, planes > 2
				&& evt.controlDown() ? 1 : 0, planes));
	}

	public boolean rotateSide(int side, int degrees) {
		if (side < 0 || side >= 6)
			return false;
		double thetastop = Math.PI * degrees / 180;
		if (this.av != 0) {
			vector TeyeX = eyeX, TeyeY = eyeY, TeyeZ = eyeZ;
			long time = System.currentTimeMillis();
			Graphics g = getGraphics();
			double av = this.av * 0.001;
			for (;;) {
				double theta;
				if (thetastop < 0) {
					theta = (time - System.currentTimeMillis()) * av;
					if (theta <= thetastop)
						break;
				} else {
					theta = (System.currentTimeMillis() - time) * av;
					if (theta >= thetastop)
						break;
				}
				eyeX = new vector(TeyeX);
				eyeY = new vector(TeyeY);
				eyeZ = new vector(TeyeZ);
				rotateSide(eyeX, eyeY, eyeZ, side, theta);
				paint(g);
			}
			g.dispose();
			eyeX = TeyeX;
			eyeY = TeyeY;
			eyeZ = TeyeZ;
		}
		rotateSide(eyeX, eyeY, eyeZ, side, thetastop);
		calcDrag = true;
		repaint(1);
		return true;
	}

	private void rotateSide(vector TeyeX, vector TeyeY, vector TeyeZ, int side,
			double theta) {
		double Cphi = Math.cos(theta), Sphi = Math.sin(theta);
		switch (side) {
		case 0: // -x
			TeyeZ.rotateX(Cphi, -Sphi);
			TeyeY.rotateX(Cphi, -Sphi);
			TeyeX.rotateX(Cphi, -Sphi);
			break;
		case 1: // y
			TeyeZ.rotateY(Cphi, Sphi);
			TeyeY.rotateY(Cphi, Sphi);
			TeyeX.rotateY(Cphi, Sphi);
			break;
		case 2: // -z
			TeyeZ.rotateZ(Cphi, -Sphi);
			TeyeY.rotateZ(Cphi, -Sphi);
			TeyeX.rotateZ(Cphi, -Sphi);
			break;
		case 3: // z
			TeyeZ.rotateZ(Cphi, Sphi);
			TeyeY.rotateZ(Cphi, Sphi);
			TeyeX.rotateZ(Cphi, Sphi);
			break;
		case 4: // -y
			TeyeZ.rotateY(Cphi, -Sphi);
			TeyeY.rotateY(Cphi, -Sphi);
			TeyeX.rotateY(Cphi, -Sphi);
			break;
		case 5: // x
			TeyeZ.rotateX(Cphi, Sphi);
			TeyeY.rotateX(Cphi, Sphi);
			TeyeX.rotateX(Cphi, Sphi);
			break;
		}
	}

	public boolean rotateX(int degrees) {
		if (degrees <= -90 || degrees >= 90)
			return false;
		double thetastop = Math.PI * degrees / 180;
		if (this.av != 0) {
			vector TeyeY = eyeY, TeyeZ = eyeZ;
			long time = System.currentTimeMillis();
			Graphics g = getGraphics();
			double av = this.av * 0.001;
			for (;;) {
				double theta;
				if (thetastop < 0) {
					theta = (time - System.currentTimeMillis()) * av;
					if (theta <= thetastop)
						break;
				} else {
					theta = (System.currentTimeMillis() - time) * av;
					if (theta >= thetastop)
						break;
				}
				eyeZ = new vector(TeyeZ);
				eyeY = new vector(TeyeY);
				eyeZ.addmultnorm(eyeY, Math.tan(theta));
				eyeY.multnorm(eyeZ, eyeX);
				paint(g);
			}
			eyeZ = TeyeZ;
			eyeY = TeyeY;
		}
		eyeZ.addmultnorm(eyeY, Math.tan(thetastop));
		eyeY.multnorm(eyeZ, eyeX);
		calcDrag = true;
		repaint(1);
		return true;
	}

	public boolean rotateY(int degrees) {
		if (degrees <= -90 || degrees >= 90)
			return false;
		double thetastop = Math.PI * degrees / 180;
		if (this.av != 0) {
			vector TeyeZ = eyeZ, TeyeX = eyeX;
			long time = System.currentTimeMillis();
			Graphics g = getGraphics();
			double av = this.av * 0.001;
			for (;;) {
				double theta;
				if (thetastop < 0) {
					theta = (time - System.currentTimeMillis()) * av;
					if (theta <= thetastop)
						break;
				} else {
					theta = (System.currentTimeMillis() - time) * av;
					if (theta >= thetastop)
						break;
				}
				eyeX = new vector(TeyeX);
				eyeZ = new vector(TeyeZ);
				eyeX.addmultnorm(eyeZ, Math.tan(theta));
				eyeZ.multnorm(eyeX, eyeY);
				paint(g);
			}
			eyeX = TeyeZ;
			eyeZ = TeyeZ;
		}
		eyeX.addmultnorm(eyeZ, Math.tan(thetastop));
		eyeZ.multnorm(eyeX, eyeY);
		calcDrag = true;
		repaint(1);
		return true;
	}

	public boolean rotateZ(int degrees) {
		if (degrees <= -90 || degrees >= 90)
			return false;
		double thetastop = Math.PI * degrees / 180;
		if (this.av != 0) {
			vector TeyeX = eyeX, TeyeY = eyeY;
			long time = System.currentTimeMillis();
			Graphics g = getGraphics();
			double av = this.av * 0.001;
			for (;;) {
				double theta;
				if (thetastop < 0) {
					theta = (time - System.currentTimeMillis()) * av;
					if (theta <= thetastop)
						break;
				} else {
					theta = (System.currentTimeMillis() - time) * av;
					if (theta >= thetastop)
						break;
				}
				eyeY = new vector(TeyeY);
				eyeX = new vector(TeyeX);
				eyeY.addmultnorm(eyeX, Math.tan(theta));
				eyeX.multnorm(eyeY, eyeZ);
				paint(g);
			}
			eyeY = TeyeY;
			eyeX = TeyeX;
		}
		eyeY.addmultnorm(eyeX, Math.tan(thetastop));
		eyeX.multnorm(eyeY, eyeZ);
		calcDrag = true;
		repaint(1);
		return true;
	}

	@SuppressWarnings("finally")
	public void run() {
		try {
			backX = -Integer.parseInt(getParameter("backx"));
			backY = -Integer.parseInt(getParameter("backy"));
			bkImage = getImage(getCodeBase(), getParameter("background"));
			prepareImage(bkImage, this);
		} catch (Exception e) {
		}
		showStatus(getCodeBase().getHost().endsWith("parkwaycc.co.uk") ? "Applet rubik waiting"
				: "Applet rubik � 1997-8 Neil Rashbrook");
		try {
			for (;;)
				perform(moves.remove());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			phibase = phi = 0; // Finished
			naturalState = true;
			// rotator = null;
			calcDrag = true;
			repaint(1);
			showStatus("Applet rubik stopped");
			return;
		}
	}

	public void setEye(double eyeXx, double eyeXy, double eyeXz, double eyeYx,
			double eyeYy, double eyeYz, double eyeZx, double eyeZy, double eyeZz) {
		eyeX = new vector(eyeXx, eyeXy, eyeXz);
		eyeY = new vector(eyeYx, eyeYy, eyeYz);
		eyeZ = new vector(eyeZx, eyeZy, eyeZz);
		repaint(1);
	}

	public void setEyeXY(double eyeXx, double eyeXy, double eyeXz,
			double eyeYx, double eyeYy, double eyeYz) {
		eyeX = new vector(eyeXx, eyeXy, eyeXz);
		eyeY = new vector(eyeYx, eyeYy, eyeYz);
		eyeZ = new vector(eyeX, eyeY);
		repaint(1);
	}

	public void setEyeYZ(double eyeXz, double eyeYx, double eyeYy,
			double eyeYz, double eyeZx, double eyeZy, double eyeZz) {
		eyeY = new vector(eyeYx, eyeYy, eyeYz);
		eyeZ = new vector(eyeZx, eyeZy, eyeZz);
		eyeX = new vector(eyeY, eyeZ);
		repaint(1);
	}

	public void setEyeZX(double eyeZx, double eyeZy, double eyeZz,
			double eyeXx, double eyeXy, double eyeXz) {
		eyeZ = new vector(eyeZx, eyeZy, eyeZz);
		eyeX = new vector(eyeXx, eyeXy, eyeXz);
		eyeY = new vector(eyeZ, eyeX);
		repaint(1);
	}

	public boolean setFacelet(int face, int row, int col, int colour) {
		if (face >= 0 && face < 6 && row >= 0 && row < planes && col >= 0
				&& col < planes && colour >= 0 && colour < 8) {
			faceCols[face][row][col] = colour;
			repaint(1);
			return true;
		}
		return false;
	}

	/**
	 * Get the colors for a particular cubie
	 * @author Shen Zhiwen
	 * 
	 * @param faceCols the data
	 */
	public static String getColor(int[][][] faceCols, String cubie) {
		if (cubie.length() == 1) {
			return getCenterColor(faceCols, faceMap.get(cubie.charAt(0)));
		}
		if (cubie.length() == 2) {
			return getEdgeColor(faceCols, faceMap.get(cubie.charAt(0)), faceMap
					.get(cubie.charAt(1)));
		}
		if (cubie.length() == 3) {
			return getCornerColor(faceCols, faceMap.get(cubie.charAt(0)),
					faceMap.get(cubie.charAt(1)), faceMap.get(cubie.charAt(2)));
		}
		throw new IllegalArgumentException("Wrong input length");
	}

	private static String getCenterColor(int[][][] faceCols, int faceIndex) {
		return "" + faceCols[faceIndex][1][1];
	}

	private static String getEdgeColor(int[][][] faceCols, int faceIndex1,
			int faceIndex2) {
		String result = "";
		// Get color on side faceIndex1
		switch (faceSide[faceIndex1][faceIndex2]) {
		case 0:
			result += faceCols[faceIndex1][0][1];
			break;
		case 1:
			result += faceCols[faceIndex1][1][2];
			break;
		case 2:
			result += faceCols[faceIndex1][2][1];
			break;
		case 3:
			result += faceCols[faceIndex1][1][0];
			break;
		}
		// Get color on side faceIndex2
		switch (faceSide[faceIndex2][faceIndex1]) {
		case 0:
			result += faceCols[faceIndex2][0][1];
			break;
		case 1:
			result += faceCols[faceIndex2][1][2];
			break;
		case 2:
			result += faceCols[faceIndex2][2][1];
			break;
		case 3:
			result += faceCols[faceIndex2][1][0];
			break;
		}
		return result;
	}

	private static String getCornerColor(int[][][] faceCols, int faceIndex1,
			int faceIndex2, int faceIndex3) {
		String result = "";
		// Get color on faceIndex1
		int m = faceSide[faceIndex1][faceIndex2];
		int n = faceSide[faceIndex1][faceIndex3];
		if ((m == 0 && n == 3) || (m == 3 && n == 0))
			result += faceCols[faceIndex1][0][0];
		else if ((m == 0 && n == 1) || (m == 1 && n == 0))
			result += faceCols[faceIndex1][0][2];
		else if ((m == 2 && n == 1) || (m == 1 && n == 2))
			result += faceCols[faceIndex1][2][2];
		else
			result += faceCols[faceIndex1][2][0];
		// Get color on faceIndex2
		m = faceSide[faceIndex2][faceIndex3];
		n = faceSide[faceIndex2][faceIndex1];
		if ((m == 0 && n == 3) || (m == 3 && n == 0))
			result += faceCols[faceIndex2][0][0];
		else if ((m == 0 && n == 1) || (m == 1 && n == 0))
			result += faceCols[faceIndex2][0][2];
		else if ((m == 2 && n == 1) || (m == 1 && n == 2))
			result += faceCols[faceIndex2][2][2];
		else
			result += faceCols[faceIndex2][2][0];
		// Get color on faceIndex3
		m = faceSide[faceIndex3][faceIndex1];
		n = faceSide[faceIndex3][faceIndex2];
		if ((m == 0 && n == 3) || (m == 3 && n == 0))
			result += faceCols[faceIndex3][0][0];
		else if ((m == 0 && n == 1) || (m == 1 && n == 0))
			result += faceCols[faceIndex3][0][2];
		else if ((m == 2 && n == 1) || (m == 1 && n == 2))
			result += faceCols[faceIndex3][2][2];
		else
			result += faceCols[faceIndex3][2][0];

		return result;
	}

	/**
	 * Get the cubie, given particular colors with order
	 * 
	 * @author Shen Zhiwen
	 * 
	 * @param faceCols the data
	 */
	public static String getCubie(int[][][] faceCols, String color) {
		if (color.length() == 1)
			return getCenterCubie(faceCols, color);
		if (color.length() == 2)
			return getEdgeCubie(faceCols, color);
		if (color.length() == 3)
			return getCornerCubie(faceCols, color);
		throw new IllegalArgumentException("Wrong input length");
	}

	private static String getCornerCubie(int[][][] faceCols, String color) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				int faceIndex1 = sideFace[i][j];
				int faceIndex2 = sideFace[i][(j + 1) % 4];
				int faceIndex3 = sideFace[i][(j + 3) % 4];
				if (getCornerColor(faceCols, i, faceIndex1, faceIndex2).equals(
						color))
					return "" + faceArray[i] + faceArray[faceIndex1]
							+ faceArray[faceIndex2];
				if (getCornerColor(faceCols, i, faceIndex1, faceIndex3).equals(
						color))
					return "" + faceArray[i] + faceArray[faceIndex1]
							+ faceArray[faceIndex3];
			}
		}
		throw new IllegalArgumentException("Corner with color " + color
				+ " does not exit");
	}

	private static String getEdgeCubie(int[][][] faceCols, String color) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				int faceIndex = sideFace[i][j];
				if (getEdgeColor(faceCols, i, faceIndex).equals(color))
					return "" + faceArray[i] + faceArray[faceIndex];
			}
		}
		throw new IllegalArgumentException("Edge does not exit");
	}

	private static String getCenterCubie(int[][][] faceCols, String color) {
		for (int i = 0; i < 6; i++)
			if (getCenterColor(faceCols, i).equals(color)) {
				return "" + faceArray[i];
			}
		throw new IllegalArgumentException("Center does not exit");
	}

	public int[][][] getCopyOfFaceCols() {
		int[][][] result = new int[faceCols.length][faceCols[0].length][faceCols[0][0].length];

		for (int i = 0; i < faceCols.length; i++)
			for (int j = 0; j < faceCols[0].length; j++)
				for (int k = 0; k < faceCols[0][0].length; k++)
					result[i][j][k] = faceCols[i][j][k];

		return result;
	}

	private void setPlanes(int planes) {
		this.planes = planes;
		sideBlock = sideBlocks[planes];
		cornerOrder[2] = planes - 1;
		cornerOrder[3] = planes - 1;
		facelets = getParameter("facelets" + planes);
		if (facelets != null && facelets.length() == planes * planes * 6) {
			int l = 0;
			char facechars[] = facelets.toCharArray();
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < planes; j++)
					for (int k = 0; k < planes; k++) {
						faceCols[i][j][k] = facechars[l++] - '0';
						if (faceCols[i][j][k] < 0 || faceCols[i][j][k] > 7)
							faceCols[i][j][k] = i;
					}
		} else {
			facelets = null;
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 4; j++)
					for (int k = 0; k < 4; k++)
						faceCols[i][j][k] = i;
		}
		undo.reset();
		redo = null;
		if (planes > 1) {
			String s;
			s = getParameter("moves" + planes);
			if (s != null)
				addMoves(s, null);
			s = getParameter("steps" + planes);
			if (s != null)
				addMoves(s, redo = new queue());
		}
		twisting = false;
		naturalState = true;
		calcDrag = true;
		repaint(1);
	}

	public void update(Graphics g) {
		paint(g);
	}

	private void writeParameters() {
		System.err
				.println("<applet code="
						+ getClass().getName()
						+ ".class codebase="
						+ getCodeBase()
						+ " width="
						+ bounds.width
						+ " height="
						+ bounds.height
						+ " alt=\"Your browser understands the &lt;applet&gt; tag but isn't running the applet, for some reason.\">");
		eyeX.writeParameters("eyeX");
		eyeY.writeParameters("eyeY");
		eyeZ.writeParameters("eyeZ");
		System.err.println("<param name=\"planes\" value=\"" + planes + "\">");
		System.err.println("<param name=\"select\" value=\"" + select + "\">");
		System.err.println("<param name=\"sticky\" value=\"" + sticky + "\">");
		System.err.println("<param name=\"grabrect\" value=\"" + grabrect
				+ "\">");
		System.err
				.println("<param name=\"graball\" value=\"" + graball + "\">");
		System.err.println("<param name=\"rotation\" value=\"" + av + "\">");
		System.err.println("<param name=\"bgcolor\" value=\""
				+ Integer.toHexString(bgColor.getRGB()).substring(2) + "\">");
		System.err.println("<param name=\"text\" value=\""
				+ Integer.toHexString(textColor.getRGB()).substring(2) + "\">");
		System.err.println("<param name=\"link\" value=\""
				+ Integer.toHexString(disabledColor.getRGB()).substring(2)
				+ "\">");
		System.err.println("<param name=\"vlink\" value=\""
				+ Integer.toHexString(enabledColor.getRGB()).substring(2)
				+ "\">");
		System.err.println("<param name=\"alink\" value=\""
				+ Integer.toHexString(activeColor.getRGB()).substring(2)
				+ "\">");
		System.err.println("<param name=\"planes\" value=\"" + planes + "\">");
		System.err.println("<param name=\"facelets" + planes + "\" value=\""
				+ String.valueOf(getFacelets()) + "\">");
		if (!undo.isempty())
			System.err.println("<param name=\"moves" + planes + "\" value=\""
					+ undo.moves(true) + "\">");
		if (redo != null && !redo.isempty())
			System.err.println("<param name=\"steps" + planes + "\" value=\""
					+ redo.moves(false) + "\">");
		if (redo == null && !undo.isempty())
			System.err.println("<param name=\"steps" + planes + "\" value=\""
					+ undo.moves(false) + "\">");
		if ("true".equals(getParameter("focus")))
			System.err.println("<param name=\"focus\" value=\"true\">");
		if (bkImage != null) {
			System.err.println("<param name=\"background\" value=\""
					+ getParameter("background") + "\">");
			System.err.println("<param name=\"backx\" value=\"" + -backX
					+ "\">");
			System.err.println("<param name=\"backy\" value=\"" + -backY
					+ "\">");
		}
		for (int i = 0; i < 6; i++)
			System.err.println("<param name=\"face" + i + "\" value=\""
					+ colors[colMap[i]] + "\">");
		System.err
				.println("Your browser doesn't understand the &lt;applet&gt; tag.</applet>");
	}
}

final class queue { // Class to describe a queue of moves
	public final int face, quads, mode;
	private queue next, prev;
	private boolean waiting;

	public queue() {
		face = -1;
		quads = -1;
		mode = -1;
		next = this;
		prev = this;
		waiting = true;
	}

	public queue(int face, int quads, int mode, int planes) {
		if (mode + mode + face / 3 >= planes) {
			face = 5 - face;
			quads = 4 - quads;
			mode = planes - mode - 1;
		}
		this.face = face;
		this.quads = quads;
		this.mode = mode;
		next = null;
		prev = null;
		waiting = false;
	}

	private queue(queue src) {
		// this(src.face, src.quads, src.mode);
		this.face = src.face;
		this.quads = src.quads;
		this.mode = src.mode;
		next = null;
		prev = null;
		waiting = false;
	}

	public synchronized void add(queue next) {
		next.prev = this;
		next.next = this.next;
		this.next.prev = next;
		this.next = next;
		waiting = false;
		notifyAll();
	}

	public synchronized void add(int face, int quads, int mode, int planes) {
		if (mode + mode + face / 3 >= planes) {
			face = 5 - face;
			quads = 4 - quads;
			mode = planes - mode - 1;
		}
		queue temp = next;
		while (temp != this
				&& (temp.face + face == 5 || temp.face == face
						&& temp.mode != mode))
			temp = temp.next;
		if (temp.face == face && temp.mode == mode) {
			quads = (quads + temp.quads) & 3;
			temp.prev.next = temp.next;
			temp.next.prev = temp.prev;
			temp.next = null;
			temp.prev = null;
		}
		if (quads != 0)
			add(new queue(face, quads, mode, planes));
	}

	public synchronized void sub(queue prev) {
		prev.next = this;
		prev.prev = this.prev;
		this.prev.next = prev;
		this.prev = prev;
		waiting = false;
		notifyAll();
	}

	public synchronized void sub(int face, int quads, int mode, int planes) {
		if (mode + mode + face / 3 >= planes) {
			face = 5 - face;
			quads = 4 - quads;
			mode = planes - mode - 1;
		}
		queue temp = prev;
		while (temp != this
				&& (temp.face + face == 5 || temp.face == face
						&& temp.mode != mode))
			temp = temp.prev;
		if (temp.face == face && temp.mode == mode) {
			quads = (quads + temp.quads) & 3;
			temp.next.prev = temp.prev;
			temp.prev.next = temp.next;
			temp.prev = null;
			temp.next = null;
		}
		if (quads != 0)
			sub(new queue(face, quads, mode, planes));
	}

	public synchronized int length() {
		int count = 0;
		for (queue temp = this.prev; temp != this; temp = temp.prev)
			count++;
		return count;
	}

	private synchronized boolean copy2(queue dest, boolean all) {
		queue temp = this.prev;
		if (temp == this)
			return false;
		do
			dest.add(new queue(temp));
		while (all && (temp = temp.prev) != this);
		return true;
	}

	public synchronized boolean append(queue src, boolean all) {
		if (waiting && src != null && src.copy2(this, all)) {
			notifyAll();
			return true;
		}
		return false;
	}

	private synchronized boolean send2(queue dest) {
		if (prev == this)
			return false;
		next.prev = dest.prev;
		dest.prev.next = next;
		next = null;
		prev.next = dest;
		dest.prev = prev;
		prev = null;
		return true;
	}

	public synchronized boolean insert(queue src) {
		return src.send2(this);
	}

	public synchronized queue remove() throws InterruptedException {
		while (prev == this) {
			waiting = true;
			wait();
		}
		queue result = prev;
		this.prev = prev.prev;
		this.prev.next = this;
		result.next = null;
		result.prev = null;
		notifyAll();
		return result;
	}

	private synchronized boolean matches(queue src) {
		return prev.face == src.face && prev.quads == src.quads
				&& prev.mode == src.mode;
	}

	public synchronized boolean startsWith(queue src) {
		return src.matches(prev);
	}

	public synchronized boolean iswaiting() {
		return waiting;
	}

	public synchronized boolean isempty() {
		return prev == this;
	}

	public synchronized boolean isnearempty() {
		return prev == next;
	}

	private static char faces[] = { 'F', 'U', 'L', 'R', 'D', 'B' };
	private static char quad[] = { '\0', '+', '|', '-' };
	private static char quadr[] = { '\0', '-', '|', '+' };

	public synchronized String moves(boolean reversed) {
		StringBuffer sb = new StringBuffer();
		if (reversed)
			for (queue temp = next; temp != this; temp = temp.next) {
				sb.append(faces[temp.face]);
				sb.append(quadr[temp.quads]);
				if (temp.mode != 0)
					sb.append('%');
			}
		else
			for (queue temp = prev; temp != this; temp = temp.prev) {
				sb.append(faces[temp.face]);
				sb.append(quad[temp.quads]);
				if (temp.mode != 0)
					sb.append('%');
			}
		return sb.toString();
	}

	public synchronized void reset() {
		next = this;
		prev = this;
		waiting = true;
	}
}

final class vector { // Class to manipulate a 3D vector
	private double x, y, z;

	public vector() { // Empty vector
		x = 0;
		y = 0;
		z = 0;
	}

	public vector(double x, double y, double z) { // Create a vector
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public vector(vector v) { // Create a copy of a vector
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public vector(vector v1, vector v2) { // Create a vector by vector product
		mult(v1, v2);
		norm();
	}

	public double mult(vector v) { // Scalar product
		return x * v.x + y * v.y + z * v.z;
	}

	private double norm2() { // Square of length
		return x * x + y * y + z * z;
	}

	public double cosAng(vector v) { // Cosine of angle between vectors
		return mult(v) / Math.sqrt(norm2() * v.norm2());
	}

	private void norm() { // Normalize vector
		double d = Math.sqrt(norm2());
		x /= d;
		y /= d;
		z /= d;
	}

	public void add(vector v) { // Add vector
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public void addmult(vector v, double d) { // Add scaled vector
		x += v.x * d;
		y += v.y * d;
		z += v.z * d;
	}

	public void addmultnorm(vector v, double d) { // With normalization
		addmult(v, d);
		norm();
	}

	public void sub(vector v) { // Subtract vector
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	public void copy(vector v) { // Copy a vector
		x = v.x;
		y = v.y;
		z = v.z;
	}

	private void mult(vector v1, vector v2) { // Vector product
		x = v1.y * v2.z - v1.z * v2.y;
		y = v1.z * v2.x - v1.x * v2.z;
		z = v1.x * v2.y - v1.y * v2.x;
	}

	public void multnorm(vector v1, vector v2) { // With normalization
		mult(v1, v2);
		norm();
	}

	public void rotateX(double cos, double sin) { // Rotation about X axis
		double y = this.y * cos + z * sin;
		z = z * cos - this.y * sin;
		this.y = y;
	}

	public void rotateY(double cos, double sin) { // Rotation about Y axis
		double x = this.x * cos + z * sin;
		z = z * cos - this.x * sin;
		this.x = x;
	}

	public void rotateZ(double cos, double sin) { // Rotation about Z axis
		double x = this.x * cos + y * sin;
		y = y * cos - this.x * sin;
		this.x = x;
	}

	private static String parameter(String param, char suffix, double d) {
		if (d <= -1 || d >= 1 || d == 0)
			return "<param name=\"" + param + suffix + "\" value=\"" + d
					+ "\">";
		else if (d < 0)
			return "<param name=\"" + param + suffix + "\" value=\"-"
					+ String.valueOf(10 - d).substring(1) + "\">";
		else
			return "<param name=\"" + param + suffix + "\" value=\""
					+ String.valueOf(10 + d).substring(1) + "\">";
	}

	public void writeParameters(String param) {
		System.err.println(parameter(param, 'x', x));
		System.err.println(parameter(param, 'y', y));
		System.err.println(parameter(param, 'z', z));
	}
}

@SuppressWarnings("serial")
final class paral extends java.awt.Polygon { // Class to describe a
	// parallelogram
	private static int[] coords(double d, double dh, double dv, double left,
			double top) {
		// Convert vectors to corner coordinates for single coloured square
		return new int[] { (int) (d + dh * (left + 0.1) + dv * (top + 0.1)),
				(int) (d + dh * (left + 0.9) + dv * (top + 0.1)),
				(int) (d + dh * (left + 0.9) + dv * (top + 0.9)),
				(int) (d + dh * (left + 0.1) + dv * (top + 0.9)),
				(int) (d + dh * (left + 0.1) + dv * (top + 0.1)) };
	}

	private static int[] coords(double d, double dh, double dv, rect r) {
		// Convert vectors to corner coordinates for black face given by rect
		return new int[] { (int) (d + dh * r.left + dv * r.top),
				(int) (d + dh * r.right + dv * r.top),
				(int) (d + dh * r.right + dv * r.bottom),
				(int) (d + dh * r.left + dv * r.bottom) };
	}

	private static int[] coords(double d, double dh, double dv, rect r,
			boolean b) {
		// Convert vectors to corner coordinates for drag region given by rect
		int[] result = new int[4];
		if (b) {
			result[0] = (int) (d + dh * r.left + dv * (r.top + 0.1));
			result[1] = (int) (d + dh * r.right + dv * (r.top + 0.1));
			result[2] = (int) (d + dh * r.right + dv * (r.bottom - 0.1));
			result[3] = (int) (d + dh * r.left + dv * (r.bottom - 0.1));
		} else {
			result[0] = (int) (d + dh * (r.left + 0.1) + dv * r.top);
			result[1] = (int) (d + dh * (r.right - 0.1) + dv * r.top);
			result[2] = (int) (d + dh * (r.right - 0.1) + dv * r.bottom);
			result[3] = (int) (d + dh * (r.left + 0.1) + dv * r.bottom);
		}
		return result;
	}

	private static double[][] l = { null, null, { 0, 1, 2, 2, 1, 0 },
			{ 0, 1, 2, 3, 3, 2, 1, 0 }, { 0, 1, 2, 3, 4, 4, 3, 2, 1, 0 } };
	private static double[] all = { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 };
	private static double[] trim = { 0.1, 0.9, 0.1, 0.9, 0.1, 0.9, 0.1, 0.9,
			0.1, 0.9 };

	private static int[] coords(double d, double dh, double dv, rect r, int p,
			boolean g, boolean b) {
		// Convert vectors to corner coordinates for drag region given by rect
		double[] s = g ? all : trim;
		double[] x = b ? l[p] : s;
		double[] y = b ? s : l[p];
		int[] result = new int[p + p + 2];
		for (int i = 0; i < result.length; i++)
			result[i] = (int) (d + dh * (r.left + x[i]) + dv * (r.top + y[i]));
		return result;
	}

	public paral(double x, double y, double dxh, double dyh, double dxv,
			double dyv, int left, int top) {
		super(coords(x, dxh, dxv, left, top), coords(y, dyh, dyv, left, top), 5); // coloured
		// square
	}

	public paral(double x, double y, double dxh, double dyh, double dxv,
			double dyv, rect r) {
		super(coords(x, dxh, dxv, r), coords(y, dyh, dyv, r), 4); // black face
	}

	public paral(double x, double y, double dxh, double dyh, double dxv,
			double dyv, rect r, boolean b) {
		super(coords(x, dxh, dxv, r, b), coords(y, dyh, dyv, r, b), 4); // drag
		// region
	}

	public paral(double x, double y, double dxh, double dyh, double dxv,
			double dyv, rect r, int p, boolean g, boolean b) {
		super(coords(x, dxh, dxv, r, p, g, b), coords(y, dyh, dyv, r, p, g, b),
				p + p + 2); // drag region
	}
}

final class facelet { // Class to store facelet positions
	public final facelet next;
	public final int face, row, col;
	public final paral poly;

	public facelet(facelet next, int face, int row, int col, paral poly) {
		this.next = next;
		this.face = face;
		this.row = row;
		this.col = col;
		this.poly = poly;
	}
}

final class drag { // Class to store drag data to see if a twist should occur
	public final drag next;
	public final double dirX, dirY;
	public final int mode, face;
	public final paral poly;

	public drag(drag next, double dirX, double dirY, int mode, int face,
			paral poly) {
		this.next = next; // Next drag data in chain
		this.dirX = dirX; // Drag direction for twist
		this.dirY = dirY; // (used to calculate angle of twist)
		this.mode = mode; // Whether face or middle slice
		this.face = face; // Which face to twist
		this.poly = poly; // Region in which dragging can start
	}
}

final class corn { // Class to store corners associated with a face of the cube
	public final int nw, ne, sw, bk;

	public corn(int nw, int ne, int sw, int bk) {
		this.nw = nw; // Northwest corner
		this.ne = ne; // Northeast corner, due east of nw corner
		this.sw = sw; // Southwest corner, due south of nw corner
		this.bk = bk; // Northwest corner, due behind nw corner
	}
}

final class rect { // Class to describe a rectangle � la Windows RECT
	public final int left, top, right, bottom;

	public rect(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
}
