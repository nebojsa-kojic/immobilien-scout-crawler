package com.prodyna;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nebojsa Kojic
 *
 */
public class PropertiesUtil {
	
	private static List<String> PROPERTIES = new ArrayList<>();
	
	public static File getPropFile() {
		String home = System.getProperties().get("user.home").toString();

		return new File(home, "gmail.txt");
	}
	
	public static String getPropFromFile(int num) {
		if (0 == PROPERTIES.size()){
			try {
			PROPERTIES.addAll(Files.readAllLines(Paths.get(getPropFile().getAbsolutePath()), Charset.forName("UTF-8")));
		
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		if (0 != PROPERTIES.size()) {
			return PROPERTIES.get(num - 1).trim();
		} else {
			return "err";
		}
	}

}
