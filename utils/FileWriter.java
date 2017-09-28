import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {
	public static FileOutputStream out;
	
	public FileWriter(File file) throws FileNotFoundException{
		out = new FileOutputStream(file);
		}
	
	
	  // method takes binary string,writes it to text file
	 
	public void writeToFile(String binary) throws IOException {
		String eightBinaryString="";  
		if(binary.length()<8){    // if less than 8 string bits
			eightBinaryString= binary;   // add left bits
			while (eightBinaryString.length()<8){  // add 0 until the 0000xxxx is complete
				eightBinaryString+="0";
			}
		}
		else{
		eightBinaryString= binary.substring(0,8);    // take 8 digits of the string
		}
		
		int value= Integer.parseUnsignedInt(eightBinaryString,2); // converts binary string to int
		byte b=(byte) value;  // converts the int to a byte
		out.write(b); // write the byte to a file
		
		if(binary.length()>8){  // if there is more than the 8 digits 
		binary=binary.substring(8);  // set to be  from index 8 
		writeToFile(binary);  
		}
	}
	
	 // Method to write chars to a file as compressed file data is decompressed
	 
	public void writeToDecompressedFile(char letter) throws IOException {
		
		if(letter=='_'){ // _ used for space 
			out.write(' '); // write as ' ' to equal actual file
		}
		else out.write(letter); //if not space write asci
	}

	// method called from main  to end the file output stream
	
	public void end() throws IOException {
		out.close();
	}
}
