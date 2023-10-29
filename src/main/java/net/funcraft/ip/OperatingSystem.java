package net.funcraft.ip;

import java.io.File;

public enum OperatingSystem {
	WINDOWS("windows", "win"),
	OSX("osx", "mac"),
	LINUX("linux", "linux", "unix"),
	UNKNOWN("unknown");

	private final String name;

	private final String[] aliases;

	OperatingSystem(String name, String... aliases) {
		this.name = name;
		this.aliases = aliases == null ? new String[0] : aliases;
	}

	public String getName() {
		return this.name;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public boolean isSupported() {
		return this != UNKNOWN;
	}

	public static OperatingSystem getCurrentPlatform() {
		String name = System.getProperty("os.name").toLowerCase();
		for (OperatingSystem os : values()) {
			for (String alias : os.getAliases()) {
				if (name.contains(alias)) {
					return os;
				}
			}
		}
		return UNKNOWN;
	}
	
	public static File getGameDirectory(String launcherName) {
		String home = System.getProperty("user.home", ".");
		
		switch (OperatingSystem.getCurrentPlatform()) {
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				String dir = (appdata != null) ? appdata : home;
				return new File(dir, "." + launcherName + "/");
	
			case OSX:
				return new File(home, "Library/Application Support/" + launcherName);
			
			case LINUX:
				return new File(home, "." + launcherName + "/");
				
			default:
				return new File(home, launcherName + "/");
		}
	}
}
