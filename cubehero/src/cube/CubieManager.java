package cube;

public class CubieManager {
	private FaceColors faceCols;
	//The first mode means the position of the cubie is fixed
	//The second means the color is fixed, in other words, it presents the physical cubie of rubik's cube.
	public enum Mode {CUBIE_MODE, COLOR_MODE};
	
	public CubieManager(FaceColors faceCols) {
		this.faceCols = faceCols;
	}
	
	public FaceColors faceCols() {
		return faceCols;
	}
	
	public Cubie createCubie(String id, Mode mode) {
		if(mode == Mode.COLOR_MODE)
			return new Cubie(this, id, true);
		else
			return new Cubie(this, id, false);
	}
}
