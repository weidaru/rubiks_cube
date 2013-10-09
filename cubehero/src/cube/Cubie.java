package cube;

public class Cubie {
	private String id;
	private String info;
	private CubieManager manager;
	private boolean traceColor = false;
	private int modifyTimes = 0;
	
	public Cubie(CubieManager mgr, String id, boolean traceColor) {
		this.id = id;
		this.manager = mgr;
		this.traceColor = traceColor;
		if(traceColor)
			info = CubeHero.getCubie(manager.faceCols(), id);
		else
			info = CubeHero.getColor(manager.faceCols(), id);
	}
	
	public boolean traceColor() {
		return traceColor;
	}
	
	
	public CubieManager manager() {
		return manager;
	}
	
	public String getCubie() {
		if(traceColor) {
			if(modifyTimes != manager.faceCols().modifyTimes()) {
				info =  CubeHero.getCubie(manager.faceCols(), id);
				modifyTimes = manager.faceCols().modifyTimes();
			}
			return info;
		}
		else
			return id;
	}
	
	public String getColor() {
		if(traceColor)
			return id;
		else {
			if(modifyTimes != manager.faceCols().modifyTimes()) {
				info = CubeHero.getColor(manager.faceCols(), id);
				modifyTimes = manager.faceCols().modifyTimes();
			}
			return info;
		}
	}
	
	@Override
	public String toString() {
		return getCubie() + ":" + getColor();
	}
}
