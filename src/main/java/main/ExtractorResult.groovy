package main

class ExtractorResult {
	
	private String revisionFile
	
	private ArrayList<String> nonJavaFilesWithConflict
	
	private String[] shaFamily = ["", "", ""]
	
	public ExtractorResult(){
		this.revisionFile = ''
		this.nonJavaFilesWithConflict = new ArrayList<String>()
	}

	public void setSHAFamily(String child, String parent1, String parent2) {
		shaFamily[0] = child
		shaFamily[1] = parent1
		shaFamily[2] = parent2
	}
	
	public String[] getSHAFamily() {
		return shaFamily
	}
	
	public String getRevisionFile() {
		return revisionFile;
	}
	public void setRevisionFile(String revisionFile) {
		this.revisionFile = revisionFile;
	}
	public ArrayList<String> getNonJavaFilesWithConflict() {
		return nonJavaFilesWithConflict;
	}
	public void setNonJavaFilesWithConflict(ArrayList<String> nonJavaFilesWithConflict) {
		this.nonJavaFilesWithConflict = nonJavaFilesWithConflict;
	}
	
	
}
