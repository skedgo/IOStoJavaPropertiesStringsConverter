package com.skedgo.tripgo.tools.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();

		// arg[0] destination path
		// arg[1] translations path
		// arg[2] Languages to generate separated by "#" (for example, en#es#de#fi#zh-Hant#zh-Hans)
		// arg[3] ios file name (TripKit.strings)
		// arg[4] ios file name (Shared.strings)
		// arg[5] ios file name	(Localizable.strings)	
		// ...
		// arg[n] ios file name
		//
		// i+1 strings has more priority than i+2 strings (discarded, not overridden) 
		
		if(args != null && args.length > 3 ){
			String destinationStringPath = args[0] ;
			String translationsPath = args[1] ;
			String[] langsArray = args[2].split("#");
			
			List<String> langs = new ArrayList<>(Arrays.asList(langsArray));
			
			List<String> iosStringsList = new ArrayList<>((args.length-3));
			
			for (int i = 3; i < args.length; i++) {	
				iosStringsList.add(args[i]);
			}
			
			StringsGeneratorUtils.getInstance().transformAllStrings(destinationStringPath,translationsPath,
					iosStringsList, langs);			
			
		}else{
			throw new Error("Wrong parameters...");
		}
			
		System.out.println("Strings done! Time: " + (System.currentTimeMillis() - startTime) + "milisecs");
		
		
	}
	

}
