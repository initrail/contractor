package project_representations;

public class ProjectFlags {
	private int exteriorSkills;
	private int interiorSkills;
	public ProjectFlags(int e, int i){
		setInteriorFlag(i);
		setExteriorFlag(e);
	}
	public int getInteriorFlag() {
		return interiorSkills;
	}
	public void setInteriorFlag(int interiorFlag) {
		this.interiorSkills = interiorFlag;
	}
	public int getExteriorFlag() {
		return exteriorSkills;
	}
	public void setExteriorFlag(int exteriorFlag) {
		this.exteriorSkills = exteriorFlag;
	}
}
