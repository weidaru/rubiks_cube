package cube;

public class FaceColors {
	private int[][][] colors;
	//This variable and related method is used to reduce duplication of calculations.
	private int modifyTimes = 0;
	
	public FaceColors(int[][][] colors) {
		this.colors = colors;
	}
	
	public int[][][] colors() {
		return colors;
	}

	public int modifyTimes() {
		return modifyTimes;
	}
	
	public FaceColors modifyTimes(int modifyTimes) {
		this.modifyTimes = modifyTimes;
		
		return this;
	}

}
