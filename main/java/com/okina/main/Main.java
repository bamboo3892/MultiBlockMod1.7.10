package com.okina.main;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		//		System.out.println(Long.MAX_VALUE);
		List<Long> l3 = new ArrayList<Long>();
		List<Long> l4 = new ArrayList<Long>();
		l3.add(1L);
		l4.add(1L);
		for (long i = 1; i <= 55000; i++){
			l3.add(i * i * i);
			l4.add(i * i * i * i);
			System.out.println(l4.get((int) (i - 1)));
		}
		for (int k = 1; k < 55000; k++){
			for (int x = 1; x < 55000; x++){
				if(l4.get(k) - l3.get(x) <= 0) continue;
				for (int y = x + 1; y < 55000; y++){
					if(l3.get(x) + l3.get(y) - l4.get(k) == 0L){
						System.out.println("Solve!");
						System.out.println("x = " + x);
						System.out.println("y = " + y);
						System.out.println("k = " + k);
						break;
					}
				}
			}
		}
	}

}
