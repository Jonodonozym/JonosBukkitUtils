package jdz.bukkitUtils.misc.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {
	
    private static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = '§';
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    public static String translate(String string) {
        return translateAlternateColorCodes('&', string);
    }

    public static List<String> translate(List<String> string) {
        return string.stream().map(ColorUtils::translate).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String[] translate(String[] strings) {
    	String[] retString = new String[strings.length];
    	int i=0;
    	for (String s: strings)
    		retString[i++] = translate(s);
    	return retString;
    }
}
