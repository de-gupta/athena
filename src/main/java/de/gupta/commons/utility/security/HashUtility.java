package de.gupta.commons.utility.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtility
{
	public static String md5Hash(String input)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest)
			{
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("MD5 algorithm not available", e);
		}
	}

	private HashUtility()
	{
	}
}