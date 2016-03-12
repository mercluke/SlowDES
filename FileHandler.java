import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


//A quick and dirty FileHandler for SlowDES
public class FileHandler{

	//Private attributes
	private boolean endOfFile;
	private FileInputStream in = null;
	private FileOutputStream out = null;
	private Mode mode;
	private long fileSize;

	//we need to distinguish between encryted text and decrypted text
	public enum Mode{
		ENCRYPT, DECRYPT
	}


	//Open files and choose mode
	FileHandler(String inputFName, String outputFName, Mode inMode){
		mode = inMode;
		endOfFile = false;

		try {
			in = new FileInputStream(inputFName);
			fileSize = in.getChannel().size();
		}
		catch(Exception e){
			//errors, handle these
			System.out.println("Unable to open file for reading: " + inputFName);
			endOfFile = true;
		}
		try {
			out = new FileOutputStream(outputFName);
		}
		catch(Exception e){
			System.out.println("Unable to open file for writing: " + outputFName);
			endOfFile = true;
		}
	}

	//make sure files closed correctly when you're done
	public void cleanupFiles(){
		if(in != null){
			try{
				in.close();
			}
			catch(Exception e){
				System.out.println("Error closing input file");
				endOfFile = true;
			}
		}
		if(out != null){
			try{
				out.close();
			}
			catch(Exception e){
				System.out.println("Error closing output file");
				endOfFile = true;
			}
		}
	}


	//Read in a block from file
	//pad to 64bit block (if last block is too small)
	public long readNextBlock(){
		//keep reading until eof
		//at eof, pad with 1 followed by however many zeroes
		long block = 0;

		//read in 8byte (64bit) blocks at a time
		for(int i = 0; i < 8 && !eof(); i++)
		{
			int c;

			try{
				c = in.read();

				//EOF
				if(c == -1){
					endOfFile = true;
					if(mode == Mode.ENCRYPT){
						block = padBlock(block, i);
					}
				}
				//Not EOF
				else{
					block ^= (long)c << (7-i)*8;
				}	
			}
			catch(Exception e){
					System.out.println("Error reading file");	
					endOfFile = true;
			}
		}

		//I can't seek backwards so i need to keep 
		//track of when i'm at the EOF
		fileSize-=8;

		if(mode == Mode.ENCRYPT)
		{
			if(fileSize < 0){
					endOfFile = true;
			}
		}
		else{
			if(fileSize <= 0){
					endOfFile = true;
			}
		}

		return block;
	}

	//add 1 + a bunch of 0s to end of block to pad
	private long padBlock(long block, int padSize)
	{
		long pad = 1;
		pad <<= ((8-padSize)*8)-1;
		block ^= pad;

		return block;
	}

	//write a block to file in 8 bytes
	public void writeNextBlock(long block){
		//if at EOF in decrypt mode, strip out padding
		if(eof() && mode == Mode.DECRYPT)
		{
			writeLastBlockStripPadding(block);
		}
		//else just write entire block
		else
		{
			for(int i = 0; i < 8; i++){
				long chunk = block;
				chunk <<= 8*i;
				chunk >>>= 56;
				try{
					out.write((int)chunk);
				}
				catch(Exception e){
					System.out.println("Could not write block");
					endOfFile = true;
				}
			}
		}
		
	}

	//Write last block with padding stripped
	private void writeLastBlockStripPadding(long block){

		int length = 0;

		//track backwards through block to find start of padding
		for(int i = 7; i >= 0; i--)
		{
			long chunk = block;
			chunk <<= i*8;
			chunk >>>= 56;	
			
			
			if((int)chunk == ((int)1 << 7))
			{
				length = i;
				break;
			}
		}

		//write blocks up until padding
		for(int i = 0; i < length; i++){
			long chunk = block;
			chunk <<= i*8;
			chunk >>>= 56;
			try{
				out.write((int)chunk);
			}
			catch(IOException e){
				endOfFile = true;
			}
		}
	}


	//At end of file?
	public boolean eof(){
		return endOfFile;
	}


}