//Helper data structure to represent bits as arrays of bools
//saves on having bitwise operations everywhere
public class BitArray{

	//actual data, itself
	boolean bitArray[];

	//Constructor, create BitArray of a given size
	public BitArray(int size){
		
		bitArray = new boolean[size];
	}

	//concatenate two existing BitArrays
	public BitArray(BitArray left, BitArray right){

		//left array is null
		if(left == null)
		{
			bitArray = new boolean[right.size()];
			for(int i = 0; i < right.size(); i++)
			{
				bitArray[i] = right.getBit(i);
			}
		}
		//both filled
		else
		{
			bitArray = new boolean[left.size()+right.size()];

			for(int i = 0; i < left.size(); i++)
			{
				bitArray[i] = left.getBit(i);
			}
			for(int i = 0; i < right.size(); i++)
			{
				bitArray[left.size()+i] = right.getBit(i);
			}
		}
	}

	//constructor, create array from range within existing array
	public BitArray(BitArray in, int start, int end)
	{
		int size = end-start+1;
		bitArray = new boolean[size];

		for(int i = 0; i < size; i++)
		{
			bitArray[i] = in.getBit(i+start);
		}

	}

	//access a specific bit, zero based, most significant bit first
	public boolean getBit(int index){
		return bitArray[index];
	}

	//set a specific bit, zero based, most significant bit first
	public void setBit(int index, boolean inVal){
		bitArray[index] = inVal;
	}

	//switch around left and right halves of array 
	//(will not work correctly for uneven number of bits)
	public void switchHalves(){
		boolean temp[] = new boolean[size()/2];

		for(int i = 0; i < temp.length; i++)
		{
			temp[i] = bitArray[i];
			bitArray[i] = bitArray[i+temp.length];
			bitArray[i+temp.length] = temp[i];
		}
	}

	//get size of BitArray
	public int size()
	{
		return bitArray.length;
	}

	//XOR with another equal length BitArray
	public BitArray exclusiveOR(BitArray input)
	{
		BitArray output = new BitArray(size());

		if(input.size() != size())
		{
			throw new IllegalArgumentException();
		}

		for(int i = 0; i < size(); i++)
		{
			output.setBit(i, (bitArray[i] != input.getBit(i)));
		}

		return output;
	}

	//create new array from left half of existing
	public BitArray getLeftHalf()
	{
		return new BitArray(this, 0, (size()/2)-1);
	}

	//create new array from right half of existing
	public BitArray getRightHalf()
	{
		return new BitArray(this, (size()/2), size()-1);
	}

	//return value represented as an int
	public int toInt()
	{
		int retVal = 0;

		for(int i = size()-1; i >= 0; i--)
		{
			int bit = (bitArray[i]) ? 1 : 0;
			retVal ^= (bit << (size()-1-i));
		}

		return retVal;
	}

	//set value to provided int
	public void setInt(int inVal)
	{
		for(int i = 0; i < size(); i++)
		{
			bitArray[i] =  ((inVal / Math.pow(2, size()-1-i)) >= 1);
			inVal %= Math.pow(2, size()-1-i);
		}
	}


	//Debugging, remove from final
	public void printVal()
	{
		for(int i = 0; i < size(); i++)
		{
			System.out.print(bitArray[i] ? 1 : 0);
		}
		System.out.println("");
	}

}