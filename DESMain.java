//Created by Luke Mercuri
//08/04/2015
//Quick and Dirty DES implimentation

import java.io.*;
public class DESMain{
	public static void main(String args[]) throws IOException {

		//The actual DES encryption object
		SlowDES engine = new SlowDES();

		BufferedReader stdin = new BufferedReader(
					new InputStreamReader(System.in));
		String mode = new String();
		String inputFile = new String();
		String outputFile = new String();
		boolean validMode = false;
		

		//Grab some input from the user
		System.out.println("Enter a string to use as a key");
		engine.generateKeys(stdin.readLine());

		System.out.println("Enter an input filename");
		inputFile = stdin.readLine();

		System.out.println("Enter an output filename");
		outputFile = stdin.readLine();


		//Has user actually entered a valid option? {E,e,D,d}
		while(!validMode)
		{
			System.out.println("Do you want to [E]ncrypt or [D]ecrypt?");
			mode = stdin.readLine();
			if(mode.length() > 0){
				switch (mode.charAt(0)){
					case 'e':
					case 'E':
						encrypt(engine, inputFile, outputFile);
						validMode = true;
					break;
					case 'd':
					case 'D':
						decrypt(engine, inputFile, outputFile);
						validMode = true;
					break;
					default: break;
				}
			}
		}
		System.out.println("Done!");	
	}

	//Encrypt inputFileName and save as outputFileName
	public static void encrypt(SlowDES engine, 
				String inputFileName, String outputFileName){

		//Open FileHandler
		FileHandler io = new FileHandler(inputFileName, 
				outputFileName, FileHandler.Mode.ENCRYPT);
		BitArrayLong plaintext = new BitArrayLong(64);
		BitArrayLong ciphertext;

		//Loop through and encrypt each block
		while(!io.eof()){
			plaintext.setBlock(io.readNextBlock());
			ciphertext = engine.encrypt(plaintext);
			io.writeNextBlock(ciphertext.toBlock());
		}

		io.cleanupFiles();
	}

	//Decrypt inputFileName and save as outputFileName
	public static void decrypt(SlowDES engine, 
			String inputFileName, String outputFileName){

		//Open FileHandler
		FileHandler io = new FileHandler(inputFileName, 
			outputFileName, FileHandler.Mode.DECRYPT);
		BitArrayLong ciphertext = new BitArrayLong(64);
		BitArrayLong plaintext;

		//Loop through and encrypt each block
		while(!io.eof()){
			ciphertext.setBlock(io.readNextBlock());
			plaintext = engine.decrypt(ciphertext);
			io.writeNextBlock(plaintext.toBlock());
		}

		io.cleanupFiles();
	}

}