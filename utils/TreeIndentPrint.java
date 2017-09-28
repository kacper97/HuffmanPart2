class TreeIndentPrint
{
	//This class holds an int data value
    // Root of the Binary Tree

    TreeNode root;
    String child;

    public TreeIndentPrint()
    {
        root = null;
    }

void printPreOrder(TreeNode root, String indent,String child)
    {
        if(root == null)
           return;
        System.out.println(""+indent+child+":"+root.getString()+" : "+root.getFrequency());
        if(root.getLeftTree() != null){
            printPreOrder(root.getLeftTree(),indent+"   ","L");   
        }
        if(root.getRightTree() != null){
            printPreOrder(root.getRightTree(),indent+"   ","R");
        }
    }

}