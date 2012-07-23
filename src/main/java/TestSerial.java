import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class TestSerial {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		FileOutputStream fos = new FileOutputStream("test.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(Integer.valueOf(1));
		oos.writeObject(Integer.valueOf(2));
		oos.writeObject(Integer.valueOf(3));
		oos.close();
		fos.close();
		
		FileInputStream fis = new FileInputStream("test.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		while(true){
			Object o = ois.readObject();
			
			if(o == null)
				break;
		}
		
		
		
	}

}
