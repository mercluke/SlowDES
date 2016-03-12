//An extension of BitArray for 64Bit long
public class BitArrayLong extends BitArray{

	public BitArrayLong(int size){
		super(size);
	}

	//return value as a long
	public long toBlock()
	{
		long retVal = 0;

		for(int i = size()-1; i >= 0; i--)
		{
			long bit = (bitArray[i]) ? 1 : 0;
			retVal ^= (bit << (size()-1-i));
		}

		return retVal;
	}

	//set value with given long
	public void setBlock(long inVal)
	{
		for(int i = size()-1; i >= 0; i--)
		{
			long tmp = inVal;
			tmp >>>= size()-1-i;
			tmp <<= size()-1;

			bitArray[i] = (tmp != 0);
		}
	}

	

}