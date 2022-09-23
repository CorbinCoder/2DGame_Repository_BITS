import java.util.Scanner;

public class Test1 {
	
	public static int NumCalculator(int accountno) {
		
		int results = -1;
		
		int numZeros = 0;
		
		String[] split = Integer.toString(accountno).split("");
		
		results = 0;
		
		for (int i = 0; i < split.length; i++) {
			
			numZeros = i;
			results += numZeros;
			
		}
		
		return results;
		
	}

	public static void main(String[] args) {
		
		int accountno = 0;

		Scanner in = new Scanner(System.in);
		
		accountno = Integer.parseInt(in.nextLine());
		
		System.out.println(NumCalculator(accountno));

	}

}
