public class TreeNode implements Comparable<TreeNode>{

	private String string="";
	private int frequency=0;
	private TreeNode leftTree=null;
	private TreeNode rightTree=null;
	/*
	 * Basic getters and setters for fields
	 */
	public String getString() {
		return string;
	}
	public void setString(String character) {
		this.string = character;
	}
	
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public TreeNode getLeftTree() {
		return leftTree;
	}
	
	public TreeNode getRightTree() {
		return rightTree;
	}
	
	public void setRightTree(TreeNode right) {
		this.rightTree = right;
	}
	
	public void setLeftTree(TreeNode left) {
		this.leftTree = left;
	}
	@Override
	public int compareTo(TreeNode treenode2) {
		// TODO Auto-generated method stub
		int returnVal;
		if (frequency<(treenode2).getFrequency()){
			 returnVal=-1;
		}
		else if (frequency>(treenode2).getFrequency()){
		 returnVal= 1;
		}
		else {
			 returnVal= 0;
		}
	return returnVal;
	}
}