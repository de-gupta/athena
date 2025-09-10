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

	public static String md5HashWithCapitalLettersAndNumbersOnly(final String input, final int length)
	{
		try
		{
			// Generate MD5 hash of the input
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(input.getBytes());

			// Define the character set: 0-9 and A-Z (36 characters)
			final String charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			StringBuilder couponCode = new StringBuilder();

			// Convert each byte of the digest to 1-2 characters from our charset
			for (int i = 0; i < digest.length && couponCode.length() < length; i++)
			{
				int value = digest[i] & 0xFF; // Get unsigned byte value

				// Each byte gives us two characters from our 36-char set
				// First character represents the high 6 bits (0-35)
				couponCode.append(charset.charAt(value % 36));

				// If we still need more characters, use the next 6 bits
				if (couponCode.length() < length)
				{
					couponCode.append(charset.charAt((value / 36) % 36));
				}
			}

			return couponCode.substring(0, Math.min(length, couponCode.length()));
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