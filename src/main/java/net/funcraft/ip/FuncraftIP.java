package net.funcraft.ip;

import com.google.common.base.Charsets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Base64;
import java.util.Random;
import java.util.zip.CRC32;

public class FuncraftIP {

	private static byte[] convertDigest(String md5) {
		byte[] jarFileDigest = new byte[16];
		for (int i = 0; i < 32; i += 2) {
			jarFileDigest[i / 2] = (Integer.valueOf(md5.substring(i, i + 2), 16)).byteValue();
		}
		return jarFileDigest;
	}
	
	public static String createIP(String username) {
		// This is their launcher's jar file MD5, the launcher did not get any update in years so this is stable
		String md5 = "78d9917ba7e3710c8a1136fefbd8d028";
		int timestamp = (int) (System.currentTimeMillis() / 1000 / 60);
		
		ByteArrayDataOutput output = ByteStreams.newDataOutput();
		output.write(convertDigest(md5), 0, 16);
		output.writeInt(timestamp);

		byte[] bytes = username.getBytes(Charsets.US_ASCII);
		output.writeByte(bytes.length);
		
		for (byte b : bytes) {
			output.writeByte(b);			
		}
		
		output.write(LauncherID.getLauncherID(), 0, 24);
		
		byte[] currentOutput = output.toByteArray();
		CRC32 crc = new CRC32();
		crc.update(currentOutput, 0, currentOutput.length);
		long crcVa = crc.getValue();
		crc.reset();
		output.writeLong(crcVa);
		
		String ip = Base64.getEncoder().encodeToString(output.toByteArray()).replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", "");
		
		Random random = new Random();
		
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b = 0; b < ip.length(); b += 60) {
			if (b != 0) {
				stringBuilder.append(".");
			}
			stringBuilder.append(Character.toChars(65 + random.nextInt(26)));
			stringBuilder.append(ip, b, Math.min(b + 60, ip.length()));
			stringBuilder.append(Character.toChars(65 + random.nextInt(26)));
		}
		
		return stringBuilder + ".offline.funcraft.net";
	}
	
}
