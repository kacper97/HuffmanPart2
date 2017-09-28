import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class FileReader {
	
	public static FileInputStream in;
	public FileReader(File file) throws FileNotFoundException{
	in = new FileInputStream(file);
	}
	
	
	 //method to read the text file and return data as string 
	 
	public String readTextFile(File inFile) throws Exception{   
		String data=""; 
		Scanner inLine = new Scanner(inFile);
		while (inLine.hasNextLine()) {     //while the text file not reached end
			String textLine = inLine.nextLine();
			data+=textLine;  // add line of text to string 
		}
		inLine.close(); // close scanner
		return data;
	}
	
	
	
	 // method called from main to end the file output stream
	 
	public void end() throws IOException {
		in.close();
	}

	 // method to read compressed file
	
	public String readCompressedFile(File inFile) throws IOException {
		String data=""; 
		byte byteArray[] = new byte[(int) inFile.length()];  // creates byte array for byte length of file
		in.read(byteArray);
		for(byte b:byteArray){ // loops for amount of bytes in array created already
			String byteString=Integer.toBinaryString(Byte.toUnsignedInt(b)); // converts byte to int, and int to binary string
			while(byteString.length()<8) 
				byteString="0"+byteString; //adds 0s that are needed for  binary number
			data+=byteString; // add to data 
		}
		return data;
	}
}
