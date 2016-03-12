//An extension of BitArray for 56bit key
public class BitArrayKey extends BitArray{
	
	public BitArrayKey(){
		super(56);
	}
	
	//shift each half of key by given distance
	public void leftShift(int shiftBy)
	{
		shiftBy %= size()/2;

		leftShiftRange(0, (size()/2), shiftBy);
		leftShiftRange(size()/2, size(), shiftBy);
	}

	//shift range of array by given distance
	private void leftShiftRange(int start, int end, int shiftBy)
	{
		boolean temp[] = new boolean[shiftBy];

		//store bits that wrap around in a temp array
		for(int i = 0; i < shiftBy; i++)
		{
			temp[i] = bitArray[i+start];
		}
		//shuffle bits down
		for(int i = start; i < end-shiftBy; i++)
		{
			bitArray[i] = bitArray[i+shiftBy];
		}
		//wrap temp bits around to other end of array
		for(int i = 0; i < shiftBy; i++)
		{
			bitArray[i+end-shiftBy] = temp[i];
		}


	}
	
}