package AWS;

import java.io.IOException;
import java.io.InputStream;

public class numFilteredStream extends InputStream {

	
	private InputStream in;
	private int buffer;
	private Boolean useBuffer;
	private Boolean checkDigit;
	private Boolean isDigit;
	private Boolean quoted;
	
	public numFilteredStream(InputStream i) {
		in = i;
		useBuffer=false;
		checkDigit=false;
		isDigit=false;
		quoted=false;
	}

	@Override
	public int read() throws IOException {
		if(useBuffer){
			useBuffer=false;
			return buffer;
		}
		buffer=in.read();
		if(quoted){
			if(buffer==34){
				quoted=false;
			}
			return buffer;
		}
		if(checkDigit){
			switch(buffer){
				case 9: case 10: case 11: case 12: case 13: case 32:{
					return buffer;
				}
				case 34:{
					quoted=true;
					checkDigit=false;
					return buffer;
				}
				case 91: case 123:{
					checkDigit=false;
					return buffer;
				}
				default:{
					checkDigit=false;
					isDigit=true;
					useBuffer=true;
					return 34;
				}
			}
		}
		if(isDigit){
			switch(buffer){
				case 125: case 44:{
					checkDigit=false;
					isDigit=false;
					useBuffer=true;
					return 34;
					
				}
				default:{
					return buffer;
				}
			}
		}
		if(buffer==58){
			checkDigit=true;
		}
		if(buffer==34){
			quoted=true;
		}
		return buffer;
	}

}
