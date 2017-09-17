package me.kingofdanether.survivalgames.util;

import java.io.File;

public class FileUtils {
	
	public static final String fs = File.separator;
	
	public static void createFolder(String path) {
		File f = new File(path);
		try {
			if (!f.exists()) {
				f.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static File getAndCreateFolder(String path) {
		File f = new File(path);
		try {
			if (!f.exists()) {
				f.mkdir();
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void createFile(String path) {
		File f = new File(path);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static File getAndCreateFile(String path) {
		File f = new File(path);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
