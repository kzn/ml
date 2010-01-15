import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
import java.io.*;

public class HelloWorld {
	public static void main(String[] args){
		
		if(args.length > 0)
			System.out.println(args[0]);
		else{
			System.out.println("Usage: program [filename]");
			return;
		}
		
		
		List<Sample> dataset = new ArrayList<Sample>();
		
		String filename = args[0];
		System.out.println("Reading:" + filename);
		
		try{
			BufferedReader fp = new BufferedReader(new FileReader(filename));
			
			StreamTokenizer ft = new StreamTokenizer(fp);
			ft.commentChar('#');
			ft.ordinaryChar(':');
			ft.eolIsSignificant(true);

		
		
		int sz = 0;
		
		while(true){
			int ttype = ft.nextToken(); // token type
			
			if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
				break;
		
					
			assert(ttype == StreamTokenizer.TT_NUMBER);
			double target = ft.nval;
			
			List<Integer> idxs = new ArrayList<Integer>();
			List<Double> vals = new ArrayList<Double>();
			
			
			while(true){
				ttype = ft.nextToken();
				if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
					break;
				
				
				
				int idx = 0;
				if(ttype == StreamTokenizer.TT_NUMBER)
					idx = (int)ft.nval;
				
				int idx_type = ttype;
				String field;
				if(ft.sval != null)
					field = ft.sval.intern();
				
								
				ttype = ft.nextToken();
				
				assert(ttype == ':');
				
				ttype = ft.nextToken();
				assert(ttype == StreamTokenizer.TT_NUMBER);
				
				if(idx_type == StreamTokenizer.TT_NUMBER){
					idxs.add(idx);
					vals.add(ft.nval);
				}		
				
				
			}
			dataset.add(new Sample(target, idxs, vals));
			
				
				
				
				
				
				
			
				
			
			
		/*	String s = fp.readLine();
			if(s == null)
				break;
			
			// strip comments
			int comment_begin = s.indexOf('#');
			if(comment_begin != -1)
				s = s.substring(0, comment_begin);
			
			// skip empty lines 
			s = s.trim();
			if(s.length() == 0)
				continue;
			
			sz += s.length();
			//System.out.println(s);
			StringTokenizer st = new StringTokenizer(s, " \t:", false);
			double target = Double.parseDouble(st.nextToken());
			
			List<Integer> idxs = new ArrayList<Integer>();
			List<Double> vals = new ArrayList<Double>();
			
			while(st.hasMoreTokens()){
				int idx = Integer.parseInt(st.nextToken());
				double val = 0;
				if(st.hasMoreTokens())
					val = Double.parseDouble(st.nextToken());
				else
					throw new IOException();
				
				idxs.add(idx);
				vals.add(val);
			}
*/			
			//Sample sample = new Sample(target, idxs, vals);
			//dataset.add(sample);
			
			
		}
		
		System.out.println("Filesize:" + Integer.toString(sz) + " bytes");
		}catch (IOException e) {
			e.printStackTrace();
		}
	
		
				
	
		System.out.println("Dataset size:" + Integer.toString(dataset.size()));
//		String input = "5 3:1.0 2:6.0 # comment\n3 3:1.0\n";
//		
//		StringReader sr = new StringReader(input);
//		StreamTokenizer st = new StreamTokenizer(sr);
//		
//		
//		int iType = 0;
//		
//		 try {
//			while ((iType = st.nextToken()) != StreamTokenizer.TT_EOF) {
//				 //System.out.println("iType = " + iType);
//				 
//			
//				 switch(iType) {
//			        case StreamTokenizer.TT_NUMBER: // Found a number, push value to stack
//			            System.out.println(st.nval);
//			            break;
//			        case StreamTokenizer.TT_WORD:
//			            // Found a variable, save its name. Not used here.
//			        	System.out.println(st.sval);
//            
//			            break;
//			        case ':':
//			        	System.out.println("feature pair separator");
//			        	break;
//			 
//			        default:
//			            System.out.println("What's this? iType = " + iType);
//			        }
//			    }
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
		
		
		
		
		
		
		
	}

}
