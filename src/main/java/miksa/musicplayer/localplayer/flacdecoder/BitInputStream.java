package miksa.musicplayer.localplayer.flacdecoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class BitInputStream implements AutoCloseable {
	
	private InputStream in;
	private long bitBuffer;
	private int bitBufferLen;
	
	
	public BitInputStream(InputStream in) {
		this.in = in;
	}
	
	
	public void alignToByte() {
		bitBufferLen -= bitBufferLen % 8;
	}
	
	
	public int readByte() throws IOException {
		if (bitBufferLen >= 8)
			return readUint(8);
		else
			return in.read();
	}
	
	
	public int readUint(int n) throws IOException {
		while (bitBufferLen < n) {
			int temp = in.read();
			if (temp == -1)
				throw new EOFException();
			bitBuffer = (bitBuffer << 8) | temp;
			bitBufferLen += 8;
		}
		bitBufferLen -= n;
		int result = (int)(bitBuffer >>> bitBufferLen);
		if (n < 32)
			result &= (1 << n) - 1;
		return result;
	}
	
	
	public int readSignedInt(int n) throws IOException {
		return (readUint(n) << (32 - n)) >> (32 - n);
	}
	
	
	public long readRiceSignedInt(int param) throws IOException {
		long val = 0;
		while (readUint(1) == 0)
			val++;
		val = (val << param) | readUint(param);
		return (val >>> 1) ^ -(val & 1);
	}
	
	
	public void close() throws IOException {
		in.close();
	}
}
