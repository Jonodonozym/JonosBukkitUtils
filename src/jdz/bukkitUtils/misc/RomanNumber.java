/**
 * RomanNumber.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.misc;

import java.util.TreeMap;

/**
 * Turns an int into a roman numeral string using recursive magic
 *
 * @author Jonodonozym
 */
public final class RomanNumber {


	private static final TreeMap<String, Integer> StringToInt = new TreeMap<String, Integer>();
	private static final TreeMap<Integer, String> IntToString = new TreeMap<Integer, String>();
    
   static {
	    StringToInt.put("IV", 4);
	    StringToInt.put("IX", 9);
	    StringToInt.put("XL", 40);
	    StringToInt.put("CD", 400);
	    StringToInt.put("CM", 900);
	    StringToInt.put("C", 100);
	    StringToInt.put("M", 1000);
	    StringToInt.put("I", 1);
	    StringToInt.put("V", 5); 
	    StringToInt.put("X", 10);
	    StringToInt.put("L", 50);
	    StringToInt.put("D", 500);

	    IntToString.put(1000, "M");
	    IntToString.put(900, "CM");
        IntToString.put(500, "D");
        IntToString.put(400, "CD");
        IntToString.put(100, "C");
        IntToString.put(90, "XC");
        IntToString.put(50, "L");
        IntToString.put(40, "XL");
        IntToString.put(10, "X");
        IntToString.put(9, "IX");
        IntToString.put(5, "V");
        IntToString.put(4, "IV");
        IntToString.put(1, "I");
    }

    /**
     * 
     * @param number
     * @return
     */
    public static String of(int number) {
    	if (number < 1)
    		return "";
    	
        int l =  IntToString.floorKey(number);
        if ( number == l ) {
            return IntToString.get(number);
        }
        return IntToString.get(l) + of(number-l);
    }
    
    public static int decode(String roman) {
        int result = 0;
        for (String s : StringToInt.keySet()) {
         result += countOccurrences(roman, s) * StringToInt.get(s);
         roman = roman.replaceAll(s, "");
        }

        return result;
    }
    
    private  static int countOccurrences(String main, String sub) {
    	   return (main.length() - main.replace(sub, "").length()) / sub.length();
    } 
}