package net.funcraft.ip;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class LauncherID {

	private static final byte[] expectedStart = new byte[] { -29, 46, 114, -97 };
	private static final Random random = new Random();

	public static byte[] getLauncherID() {
		int selected = 0;
		byte[] id = null;
		
		for (Entry<File, Integer> entry : LauncherID.getFilesAndCorrection().entrySet()) {
			byte[] entryID = LauncherID.getIDFromFile(entry.getKey(), entry.getValue());
			if (entryID != null && LauncherID.isValid(entryID)) {
				id = entryID;
				selected = entry.getValue();
				break;
			}
		}
		
		if (id != null) {
			for (Entry<File, Integer> entry : LauncherID.getFilesAndCorrection().entrySet()) {
				if (entry.getValue() == selected) continue;
				
				LauncherID.writeIDtoFile(entry.getKey(), id, entry.getValue());
			}
		} else {
			id = LauncherID.getRandomID();
		}
		
		return id;
	}
	
	private static HashMap<File, Integer> getFilesAndCorrection() {
		HashMap<File, Integer> files = new HashMap<>();
		
		String home = System.getProperty("user.home");
		
		switch (OperatingSystem.getCurrentPlatform()) {
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				String localappdata = System.getenv("LOCALAPPDATA");

				files.put(new File(home, ".java/wbview.lock"), 25);
				
				if (appdata != null) files.put(new File(appdata, "AdobeWLCMR2Cache.dat"), -101);
				else files.put(new File(home, "AdobeWLCMR2Cache.dat"), -101);
				
				if (localappdata != null) files.put(new File(localappdata, "Microsoft/PackageIndex/segments_.prx"), -11);
				break;
				
			case LINUX:
			case UNKNOWN:
				files.put(new File(System.getProperty("user.home"), ".java/wbview.lock"), 25);
				files.put(new File(OperatingSystem.getGameDirectory("azlauncher"), "launcher_id.dat"), 47);
				break;
				
			case OSX:
				files.put(new File(System.getProperty("user.home"), "Library/Application Support/.java/wbview.lock"), 25);
				files.put(new File(OperatingSystem.getGameDirectory("azlauncher"), "launcher_id.dat"), 47);
				break;
		}
		
		return files;
	}
	
	private static byte[] getIDFromFile(File file, int correction) {
		if (file == null || !file.exists()) {
			return null;
		}

		try (FileInputStream is = new FileInputStream(file)) {
			byte[] fileStart = new byte[LauncherID.expectedStart.length];

			if (is.read(fileStart) != fileStart.length) {
				throw new EOFException();
			}

			decode(fileStart, correction);
			if (Arrays.equals(fileStart, LauncherID.expectedStart)) {
				byte[] id = new byte[24];

				if (is.read(id) != id.length) {
					throw new EOFException();
				}

				decode(id, correction);
				return id;
			}
		} catch (IOException ignored) {
		}
		return null;
	}

	private static void encode(byte[] bytes, int correction) {
		for (byte b = 0; b < bytes.length; b++) {
			bytes[b] = (byte) (bytes[b] + correction);
		}
	}
	
	private static void decode(byte[] bytes, int correction) {
		for (byte b = 0; b < bytes.length; b++) {
			bytes[b] = (byte) (bytes[b] - correction);
		}
	}

	private static byte[] getRandomID() {
		byte[] bytes = new byte[24];
		while (true) {
			new SecureRandom().nextBytes(bytes);
			if (isValid(bytes)) {
				byte success = 0;
				for (Entry<File, Integer> entry : LauncherID.getFilesAndCorrection().entrySet()) {
					if (LauncherID.writeIDtoFile(entry.getKey(), bytes, entry.getValue())) {
						success++;
					}
				}
		        return success <= 0 ? null : bytes;
			}
		}
	}
	
	private static boolean isValid(byte[] bytes) {
		if (bytes.length != 24) {
			return false;
		}

		for (byte i = 0; i < bytes.length - 7; i++) {
			byte b = bytes[i];
			byte occurence = 0;
			for (byte b2 : bytes) {
				if (b == b2 && ++occurence >= 8) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private static boolean writeIDtoFile(File file, byte[] bytes, int correction) {
		if (file == null) {
			return false;
		}
		
		try {
			file.getParentFile().mkdirs();
		} catch (Exception ignored) {
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(file); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			byteArrayOutputStream.write(LauncherID.expectedStart);
			byteArrayOutputStream.write(bytes);

			byte[] byteArray = byteArrayOutputStream.toByteArray();
			encode(byteArray, correction);
			fileOutputStream.write(byteArray);

			byteArray = new byte[256 + random.nextInt(1024)];
			random.nextBytes(byteArray);
			fileOutputStream.write(byteArray);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
