public class SlowDES{

	//K: main key
	private BitArrayKey mainKey = new BitArrayKey();
	//array of subKeys for rounds
	private BitArray subKeys[] = new BitArray[16];


	//generate subKeys from a provided 64bit value
	public void generateKeys(String string){
		
		//Generate mainKey
		BitArrayLong rawKey = new BitArrayLong(64);
		rawKey.setBlock(hashString(string));
		performPermutation(DESConstants.PC1, rawKey, mainKey);

		//use mainKey to generate subKeys
		for(int i = 0; i < 16; i++)
		{
			mainKey.leftShift(DESConstants.LEFT_SHIFTS[i]);
			subKeys[i] = new BitArray(48);
			performPermutation(DESConstants.PC2, mainKey, subKeys[i]);
		}
	}

	//Based on java.lang.String.hashCode()
	//adapted for use with a 64bit long
	//see http://en.wikipedia.org/wiki/Java_hashCode%28%29#hashCode.28.29_in_general
	private long hashString(String string){
		long hash = 1125899906842589L; //really big prime number

		for(int i = 0; i < string.length(); i++)
		{
			hash = 31*hash + string.charAt(i);
		}

		return hash;
	}

	//Encryption C = IP^-1(fK15(SW(fk14(...(IP(P)))))
	public BitArrayLong encrypt(BitArrayLong plaintext)
	{
		BitArray workingtext = new BitArray(64);
		BitArrayLong ciphertext = new BitArrayLong(64);

		performPermutation(DESConstants.IP, plaintext, workingtext);

		for(int i = 0; i < 15; i++)
		{
			workingtext = fK(workingtext, i);
			workingtext.switchHalves();
		}
		workingtext = fK(workingtext, 15);

		performPermutation(DESConstants.INVERSE_IP, workingtext, ciphertext);

		return ciphertext;
	}

	//Decription P' = IP(fK1(SW(fK2(...(IP^-1(C))))))
	public BitArrayLong decrypt(BitArrayLong ciphertext)
	{
		BitArray workingtext = new BitArray(64);
		BitArrayLong plaintext = new BitArrayLong(64);

		performPermutation(DESConstants.IP, ciphertext, workingtext);

		for(int i = 15; i > 0; i--)
		{
			workingtext = fK(workingtext, i);
			workingtext.switchHalves();
		}
		workingtext = fK(workingtext, 0);

		performPermutation(DESConstants.INVERSE_IP, workingtext, plaintext);

		return plaintext;
	}

	//Pass in table from DESConstants and permutate BitArray
	private void performPermutation(int[] table, BitArray input, BitArray output){

		for(int i = 0; i < table.length; i++)
		{
			output.setBit(i, input.getBit(table[i]));
		}

	}

	//fK, main function of round
	//L ^ feistel(R,Kx), R
	private BitArray fK(BitArray data, int iteration)
	{
		BitArray left = data.getLeftHalf();
		
		BitArray newLeft = left.exclusiveOR(
			feistel(data.getRightHalf(), subKeys[iteration])
		);
 		
 		return new BitArray(newLeft, data.getRightHalf());
	}


	//helper method of fK
	//expand 32bit half, xor with subKey,
	//input into substitution boxes
	//permutate sbox output through P
	private BitArray feistel(BitArray right, BitArray subKey){
		
		BitArray expandedRight = new BitArray(48);
		BitArray chunks[] = new BitArray[8];
		BitArray rawResult = null;
		BitArray permutedResult = new BitArray(32);
		
		performPermutation(DESConstants.EP, right, expandedRight);
		expandedRight = expandedRight.exclusiveOR(subKey);


		//split into 8 seperate 6bit parts
		//do sbox look up (4 bit number returned)
		//concat sbox values
		for(int i = 0; i < 8; i++)
		{
			BitArray chunk = new BitArray(expandedRight, i*6, (i*6)+5);
			BitArray sboxResult = sBoxLookup(chunk, i);
			rawResult =  new BitArray(rawResult, sboxResult);
		}
		
		performPermutation(DESConstants.P, rawResult, permutedResult);
		//return value

		return permutedResult;	
	} 

	//pass in 6bit chunk, return 4bit chunk
	private BitArray sBoxLookup(BitArray chunk, int sBox)
	{
		//there's probably a nicer way to do this but it's 2am
		int row = 0;
		row += (chunk.getBit(0)) ? 2 : 0;
		row += (chunk.getBit(3)) ? 1 : 0;
		BitArray returnVal = new BitArray(4);
		BitArray collumn = new BitArray(chunk, 1, 4);

		returnVal.setInt(DESConstants.SBOXES[sBox][row][collumn.toInt()]);

		return returnVal;
	}

}