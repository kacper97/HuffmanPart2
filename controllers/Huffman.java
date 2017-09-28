import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;

public class Huffman {


	private HashMap<String,Integer> charMap=new HashMap<>(); // used for frequency count
	private static HashMap<String,String> binaryMap=new HashMap<>();  // binary conversions
	private static Huffman huffman=new Huffman();
	private static FileReader fileReader;
	private static FileWriter fileWriter;
	private static JFileChooser filechoose;
	private static PriorityQueue<TreeNode> queue=new PriorityQueue<TreeNode>();
	private static TreeIndentPrint print=new TreeIndentPrint();
	private TreeNode root;  // the root of tree

	private static String binaryTreeString="";  // full huffman code tree in binary
	private static String datatoWrite="";  // string of binary digits to be written to a file
	private static String eof="*";  // end of file char needed for compression and decompression
	double newFileSize;  //newly created file size
	double originalFileSize;  // file opened size before compression/decompression
	private static String identifier="0CADD099"; // 4 byte identifier
	long taken;  //time taken for compression or decompression


	public static void main(String[] args) throws IOException{
		Shell shell = ShellFactory.createConsoleShell("lm", "Welcome to the Huffman Compression/Decompression Program - ?help for instructions",huffman);
		shell.commandLoop();
	}
	public Huffman() {
		filechoose= new JFileChooser(System.getProperty("user.dir")+"\\textFiles");  // starts at directory of textFiles folder in this project folder
		FileFilter filter = new FileNameExtensionFilter("Text File","txt");  // only shows text files in file browser
		filechoose.setFileFilter(filter);
		filechoose.setAcceptAllFileFilterUsed(false);  // dont have option in dropdown for all files to be shown

	}

	// Method acts on input to shell for compression
	 
	@Command (description="Select File For Compression")  
	public void fileSelectCompress () throws Exception
	{
		int returned=filechoose.showOpenDialog(null);  //get file to open
		if(JFileChooser.CANCEL_OPTION==returned){  //if cancel button ,cancel out of program
			JOptionPane.showMessageDialog(filechoose, "Program Cancelled"); // dialog
		}
		else if(filechoose.getSelectedFile().exists() && filechoose.getSelectedFile().length()>0)  // if the file exists and length >0, ie has stuff in it
			compressFile(filechoose.getSelectedFile()); //continue 
		else{  // doesnt exist or empty file
			JOptionPane.showMessageDialog(filechoose, "Please Choose an appropriate file, not empty"); // else message
			fileSelectCompress();  //goes back
		}
	}
	

	//  Method acts on input to display most recently generated tree
	
	@Command (description="Display Recently Generated Tree")  
	public void displayRecentlyGeneratedTree () throws Exception
	{
		if(root!=null) print.printPreOrder(root," ","B");   // if  a tree stored, root will have a value , then print tree
		else System.out.println("Please compress/decompress a file before hand to  display the tree");  // no root value yet -> no tree
	}
	
	//  Method to display text from text file
	 
	@Command (description="Display File Text")  
	public void displayFileText () throws Exception
	{
		int returned=filechoose.showOpenDialog(null);  //choose file
		if(JFileChooser.CANCEL_OPTION==returned){  //if cancel button , cancel out of program
			JOptionPane.showMessageDialog(filechoose, "Program Cancelled");
		}
		else if(filechoose.getSelectedFile().exists() && filechoose.getSelectedFile().length()>0){  // if the file exists and contains data
			fileReader=new FileReader(filechoose.getSelectedFile());
			String data=fileReader.readTextFile(filechoose.getSelectedFile()); //get data from file
			if(data.length()==0){  
				data=fileReader.readCompressedFile(filechoose.getSelectedFile()); // compressed file-> binary string instead
			}
			while(data.length()>0){  // while data left to print
				if(data.length()>=100){  // if gmore than 100 in length
				System.out.println(data.substring(0,100));  //print on line
				data=data.substring(100);  //cut off what was printed
				}
				else { //shorter than 100 
					System.out.println(data); //print whatever is left in string
					data=""; // empty
				}
			}
		}
		else{  // no file
			JOptionPane.showMessageDialog(filechoose, "Please Choose an appropriate file, not empty");
			displayFileText();  //go back to method again
		}
	}

	 // Method acts on input to display stats from most recent compression/decompression
	 
	@Command (description="Display Recent File Statistics") 
	public void displayRecentFileStatistics() throws Exception
	{
		if(root!=null){ // if there is a root stored, there is stats stored
			System.out.println("---------\nOriginal File Size: "+originalFileSize+
					" bytes\nNew File Size: "+newFileSize+" bytes\nTime taken: "+taken+"ms\nFile Size Improvement: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");
		}
		else System.out.println("Please compress/decompress a file first to get statistics");  //no info stored
	}

	 //Method acts on input to display the most recent freq table (after compression)
	
	@Command (description="Display Recent Character Frequency Table") 
	public void displayRecentCharFreq() throws Exception
	{
		if(!charMap.isEmpty()){ // if not an empty freq table
			displayFreqMap();
		}
		else System.out.println("Please compress a file first to get character frequencies");  // empty freq table
	}

	
	 // Method acts on input to display most recent binary mappings of characters
	 
	@Command (description="Display Recent Binary Mappings") 
	public void displayRecentBinMap() throws Exception
	{
		if(!binaryMap.isEmpty()){  // if not an empty map
			displayBinaryMap();
		}
		else System.out.println("Please compress a file first for binary mappings");  // empty map
	}

	
	 // Method acts on input to decompress a file
	 
	@Command (description="Select File For Decompression")  //enter file path
	public void fileSelectDecompress () throws Exception
	{
		int returned= filechoose.showOpenDialog(null);  // file chooser for open file, returns value for approve,cancel option pressed
		if(JFileChooser.CANCEL_OPTION==returned){  //cancel was pressed
			JOptionPane.showMessageDialog(filechoose, "Program Cancelled");   
		}
		else if(filechoose.getSelectedFile().exists()&& filechoose.getSelectedFile().length()>0)   // file exists and file is not empty
			decompressFile(filechoose.getSelectedFile());  //continue with decompression
		else{
			JOptionPane.showMessageDialog(filechoose, "Please Choose an appropriate file, not empty");
			fileSelectDecompress();  // go back
		}   
	}

	// Method to compress a file, with file as input param
	 
	public void compressFile(File file) throws Exception{
		reset();  // reset method called
		System.out.println("Compressing file");
		long start=System.currentTimeMillis();  //start timer

		fileReader=new FileReader(file);  // to read the file chosen for compression
		originalFileSize=file.length();    // original size in bytes
		String fileText=fileReader.readTextFile(file);  // create string from file text
		fileText+=eof;   // end of file character added to data 
		readString(fileText);  // parse  string
		createTreeNodes();   // create TreeNodes 
		createTree();  //create the tree
		String binary="";  //used in creating binary fof leaf
		createBinaryMappings(root,binary);  // goes through tree to create binary mappings
		convertTreeToString(root); // convert tree to binary string 
		int i = Integer.parseInt(identifier, 16);  // get the hex string as integer
		String HexToBinaryString = Integer.toBinaryString(i);  //change the int to binary string
		while(HexToBinaryString.length()<identifier.length()*4){   //if there is less than 4 bits 0s cut off
			HexToBinaryString= "0"+HexToBinaryString;  //add in 0
		}
		datatoWrite+=HexToBinaryString;   // "0CADD099" in binary as identifier
		
		String treeLength=Integer.toBinaryString(binaryTreeString.length());  // convert the length of the tree to binary string
		while(treeLength.length()<16){  // make the binary string for tree length be 16 bits (allows for large trees)
			treeLength= "0"+treeLength;
		}
		datatoWrite+=treeLength;  // add tree length after identifier
		datatoWrite+=binaryTreeString; //added on tree binary string
		datatoWrite+=convertDataToBinaryString(fileText); // text data from file in binary string
		long beforeSaveChoice=System.currentTimeMillis();
		int returned= filechoose.showSaveDialog(null);   // open dialog file chooser for choosing the save file
		long afterSaveChoice=System.currentTimeMillis();
		if(JFileChooser.CANCEL_OPTION==returned){
			JOptionPane.showMessageDialog(filechoose, "Program Cancelled");
		}
		else{
			File compressedFile= checkSaveFileType(filechoose.getSelectedFile().getName());  // checks the file name to check its text file, and to add .txt suffix if not 
			fileWriter=new FileWriter(compressedFile); // file writer for the chosen saved file
			fileWriter.writeToFile(datatoWrite);  // write the data 
			newFileSize=compressedFile.length(); // size of the new file created
			long end=System.currentTimeMillis();  //end time
			taken= end-start-(afterSaveChoice-beforeSaveChoice);  //taken time since start of compression
			System.out.println("---------\nOriginal File Size: "+originalFileSize+
					" bytes\nCompressed File Size: "+newFileSize+" bytes\nTime taken: "+taken+"ms\nSpace Saving: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");
			fileWriter.end();
		}
		fileReader.end(); 
	}
	
	 //Method to check the file type checked if file name is input as text to dialog
	 
	private File checkSaveFileType(String name) {
		File newFile;
		if(name.length()>=4){ // if the file name is greater than 4 i.e long enough to have .txt
			if(name.substring(name.length()-4).equals(".txt")){ // if the last 4 chars is ".txt" 
				newFile=filechoose.getSelectedFile();  // set the file as the name entered it is acceptable
			}
			else {
				newFile=new File("textFiles\\"+name+".txt");  // .txt isnt included, so add the path to go into textFiles folder, and add .txt on end
			}
		}
		else {
			newFile=new File("textFiles\\"+name+".txt"); //.txt isnt included, so add the path to go into textFiles folder, and add .txt on end
		}
		return newFile;
	}

	
	// method to reset global variables (to reset after each event
	
	public void reset() {
		charMap.clear();
		binaryMap.clear();
		queue.clear();
		binaryTreeString="";
		datatoWrite="";
        root=null;
	}
	// Method to decompress
	
	public void decompressFile(File file) throws IOException {
		reset();  //reset values 
		System.out.println("Decompressing the file");
		long start=System.currentTimeMillis(); //start timer
		fileReader=new FileReader(file);  //to read the file input as parameter to be opened
		originalFileSize=file.length(); // compressed file size in bytes
		String readInBinary=fileReader.readCompressedFile(file);  // returns the read in binary as string
		int decimal = Integer.parseInt(readInBinary.substring(0, 32),2);  // converts first 32 digits from base 2 to an integer
		String hexStr = Integer.toString(decimal,16).toUpperCase(); // converts binary string to base 16 HEX string uppercase
		while(hexStr.length()<8)  hexStr="0"+hexStr; // if shorter than 8 chars, leading 0's were cut off , so add back on 
		 

		if(hexStr.equals(identifier)){  // if the identifier is correct (file was compressed by this program)
			readInBinary=readInBinary.substring(32); // cut out the identifier ,checked
			readInBinary=removeAndParseTreeBinary(readInBinary);  // pass rest of data to method that works based on tree
			long beforeSaveChoice=System.currentTimeMillis();
			int returned=filechoose.showSaveDialog(null);   // opens file chooser to choose a file to save decompression to, returns if approve or cancel pressed as int
			long afterSaveChoice=System.currentTimeMillis();
			if(JFileChooser.CANCEL_OPTION==returned){  //cancel pressed
				JOptionPane.showMessageDialog(filechoose, "Program Cancelled");
			}else{
				File decompressedFile= checkSaveFileType(filechoose.getSelectedFile().getName()); // check the file has .txt suffix, add .txt if not
				fileWriter=new FileWriter(decompressedFile);
				decodeData(readInBinary,fileWriter); // decode the data of compressed

				long end=System.currentTimeMillis();  // end time
				taken=end-start-(afterSaveChoice-beforeSaveChoice);  //get time decompressing- time in filechooser

				newFileSize=decompressedFile.length(); //size in bytes of new file

				System.out.println("---------\nCompressed File Size: "+originalFileSize+
						" bytes\nDecompressed File Size: "+newFileSize+" bytes\nTime taken: "+taken+"ms\nFile Size Improvement: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");
				fileWriter.end();
			}
			fileReader.end();
		}
		else{
			System.out.println("Incorrect Identifier - Please Choose a file compressed by this program!");  // wrong identifier on compressed file read in, program decompression ends
		}
	}

	// Method to remove tree data in the string of file data + and use it to create tree
	 
	private String removeAndParseTreeBinary(String readInBinary) {
		short treeLength= Short.parseShort(readInBinary.substring(0,16),2); // treelength is the 16 digits, convert to short value
		
		binaryTreeString=readInBinary.substring(16,16+treeLength); // get substring from index 16 till 16 and add tree length
		
		createTreeFromBinary(root); // creates the tree from binary string , passes null as no root node yet
		
		return readInBinary.substring(16+treeLength);  // return the remaining binary string (without tree length and tree binary)
	}

	
	 // Method to create tree structure from binary string read from compressed file
	
	private void createTreeFromBinary(TreeNode node) {
		if(node==null){  // if a null value passed in, no tree nodes created 
			root=new TreeNode("",null,null);
			createTreeFromBinary(root); // pass the empty root node to same method - recursion
		}
		else{ 
			if(binaryTreeString.charAt(0)=='1' && node.getLeftTree()==null){  // if value is 1 and no left tree attached yet, next step is traverse left
				node.setString("parent");  // not a leaf so set string as parent for ease of reading tree display 
				node.setLeftTree(new TreeNode("",null,null));  // want to traverse left so create empty TreeNode for left tree
				binaryTreeString=binaryTreeString.substring(1); // subtract the digit just checked from string
				createTreeFromBinary(node.getLeftTree()); // recurse for left tree TreeNode
			}
			if(node.getLeftTree()!=null){   // left created for TreeNode--> must go right
				if( binaryTreeString.charAt(0)=='1'){  // if digit is 1 then non leaf TreeNode
					node.setString("parent");  //set to parent
					node.setRightTree(new TreeNode("",0, null,null)); //want to traverse right tree next
					binaryTreeString=binaryTreeString.substring(1); //cut off digit just checked
					createTreeFromBinary(node.getRightTree());  //recurse for right tree
				}
			}
			if (binaryTreeString.length()!=0){  // if there is data left to parse 
				if(binaryTreeString.charAt(0)=='0'){ // if digit is 0 then leaf
					node.setString(""+(char)Byte.parseByte (binaryTreeString.substring(1,9),2)); // sting for leaf character converted from byte to char (ascii used here)
					node.setLeftTree(null);  //leaves have no subtrees
					node.setRightTree(null);
					binaryTreeString=binaryTreeString.substring(9); // remove digits relating to leaf TreeNode
				}
			}
		}

	}
	  //Method to decode the message part of the binary string read in , and write to a file
	 
	private void decodeData(String readInBinary,FileWriter fileWriter) throws IOException{
		TreeNode parent=root;  // set the parent 
		boolean eofFound=false;  // eof not reached
	
		while(!eofFound){  //continue until eof found
			
			if(readInBinary.charAt(0)=='0'){   // 0 represents go left
				TreeNode child=parent.getLeftTree();  // left child tree
				if(child.getLeftTree()==null && child.getRightTree()==null){  //leaf node
					if(child.getString().equals(eof)){  // if the leaf is eof 
						eofFound=true;
						break;
					}
					else  // not eof in leaf
					fileWriter.writeToDecompressedFile((char)child.getString().charAt(0));  // write the character to file
					parent=root;  // go back up to the start for next sequence
				}
				else{
					parent=child;  // not a leaf ->  traverse down this path , go from the child node next time
				}
			}
			else if(readInBinary.charAt(0)=='1'){  // 1 represents go right
				TreeNode child=parent.getRightTree(); // right child
				if(child.getRightTree()==null && child.getLeftTree()==null){  //leaf 
					if(child.getString().equals(eof)){  //eof leaf found
						eofFound=true;

						break;
					}
					else{ // not eof 
					
					fileWriter.writeToDecompressedFile((char)child.getString().charAt(0)); //write character to file
					parent=root;  // start from root again for next sequence
					}
				}
				else{
					parent=child; // keep traversing down this path
				}
			}
			readInBinary=readInBinary.substring(1);  // take off digit just checked 
		}
	}

	 // Method to convert the data read from the file to a string of the corresponding binary digits from huffman code
	
	public String convertDataToBinaryString(String data) {
		String binaryData="";
		for(int i=0;i<data.length();i++){  //goes through string 
			if(data.charAt(i)==' '){    // if data = space used _
				binaryData+=binaryMap.get("_");
			}
			else{
				binaryData+=binaryMap.get(""+data.charAt(i));  //get binary code for character 
			}
		}
		return binaryData;
	}

	 //traversal down the tree from root , adds a "1" to string if a node is not a leaf, adds "0" and the character if leaf 
	
	private void convertTreeToString(TreeNode root) throws IOException {
		
		if (root.getLeftTree()==null && root.getRightTree()==null){  // leaf node
			binaryTreeString+="0";  // represents leaf
			String charAscii=Integer.toBinaryString((int)root.getString().charAt(0)); // get binary string value of the int value of char (ascii)
			while(charAscii.length()<8) charAscii="0"+charAscii;  // add back on leading 0's
			binaryTreeString+=charAscii;  //binary string for character added 

		}
		if(root.getLeftTree() != null){   // has a left tree
			binaryTreeString+="1";  //not a leaf
			convertTreeToString(root.getLeftTree());  //keep traversing down left side
		}
		if(root.getRightTree() != null){  // gets here after checking left tree (if any)
			binaryTreeString+="1";
			convertTreeToString(root.getRightTree()); // traverse right tree
		}
	}
	
	public HashMap<String, Integer> getCharMap() {
		return charMap;
	}

	public static HashMap<String, String> getBinaryMap() {
		return binaryMap;
	}

	public static String getBinaryTreeString() {
		return binaryTreeString;
	}

	public static String getdatatoWrite() {
		return datatoWrite;
	}

	// displays binary mappings of each char
	
	private void displayBinaryMap() {
		for(String character:binaryMap.keySet()){
			System.out.println(character+" : "+ binaryMap.get(character));
		}
	}

	// displays freq mappings of each char
	
	private void displayFreqMap() {
		for(String character:charMap.keySet()){
			System.out.println(character+" : "+ charMap.get(character));
		}
	}

	// when q is build, this method creates tree
	public void createTree() {
		while(queue.size()>1){ // more than 1 in queue
			
			TreeNode smallest= queue.remove();  //smallest in queue based on freq
			TreeNode secondSmallest=queue.remove(); // second smallest in queue based on freq
			String combinedString=smallest.getString()+secondSmallest.getString(); //combining strings
			int combinedFreq=smallest.getFrequency()+secondSmallest.getFrequency(); //combining frequencies
			TreeNode combination=new TreeNode(combinedString,combinedFreq,secondSmallest,smallest);  //create combination TreeNode with smallest and second smallest as left and right trees
			queue.add(combination);  // add the combo TreeNode back to queue
		}
		root=queue.remove();
	}

	public static PriorityQueue<TreeNode> getQueue() {
		return queue;
	}

	//for each string in freq table, create treenode with nullc hildren

	public void createTreeNodes() {
		for(String string:charMap.keySet()){
			queue.add(new TreeNode(string,charMap.get(string),null,null));  //null children until tree structure created
		}
	}

	//traverses pre order throught tree adding 1+0 depending on direction, binary put into map with treenode string as key

	private void createBinaryMappings(TreeNode TreeNode,String binary){
		if(TreeNode.getLeftTree() != null){
			createBinaryMappings(TreeNode.getLeftTree(),binary+"0");
		}

		if(TreeNode.getRightTree() != null){
			createBinaryMappings(TreeNode.getRightTree(),binary+"1");

		}
		if (TreeNode.getLeftTree()==null && TreeNode.getRightTree()==null){
			binaryMap.put(TreeNode.getString(),binary);  //leaf node , put the values into map
		}
	}

	// reads strings passed as par, for each char jump to check method for frequencies

	public void readString(String string){
		for (int i=0;i<string.length();i++){
			if(string.charAt(i)==' '){
				check('_');
			}
			else{
				check(string.charAt(i));
			}
		}
	}

	// if char in freq table, replace char by incrementing freq value too or else enter char with freq of 1
	public void check(char character) {
		if(charMap.isEmpty()){
			charMap.put(""+character,1);
		}
		else{
			if(charMap.containsKey(""+character)){
				charMap.replace(""+character,charMap.get(""+character)+1);
			}
			else{
				charMap.put(""+character,1);
			}
		}
	}
}
