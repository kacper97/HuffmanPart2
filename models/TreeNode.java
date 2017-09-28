public class TreeNode implements Comparable{
	
	private TreeNode leftTree=null;
	private TreeNode rightTree=null;
	private String string="";
	private int frequency=0;
	
//Getters and setters
	
	public TreeNode getLeftTree() {
		return leftTree;
	}
	
	public void setLeftTree(TreeNode left) {
		this.leftTree = left;
	}
	
	public TreeNode getRightTree() {
		return rightTree;
	}
	
	public void setRightTree(TreeNode right) {
		this.rightTree = right;
	}
	
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
	

	//Constructor for the treenode
	public TreeNode(String string, int frequency, TreeNode left, TreeNode right) {
		this.string = string;
		this.frequency = frequency;
		this.leftTree = left;
		this.rightTree = right;
	}
	
	public TreeNode(String string, TreeNode left, TreeNode right) {
		this.string = string;
		this.leftTree = left;
		this.rightTree = right;
	}
	
	
	//Compares treenodes based on frequency of it occuring
	@Override
	public int compareTo(Object TreeNode2) {
		int returnVal;
		if (frequency<((TreeNode) TreeNode2).getFrequency()){
			 returnVal=-1;
		}
		else if (frequency>((TreeNode) TreeNode2).getFrequency()){
		 returnVal= 1;
		}
		else {
			 returnVal= 0;
		}
	return returnVal;
	}
}

