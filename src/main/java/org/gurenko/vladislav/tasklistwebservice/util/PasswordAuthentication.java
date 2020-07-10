package org.gurenko.vladislav.tasklistwebservice.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple class for hashing and checking passwords.
 * Uses PBKDF2 algorithm from Sun.
 *
 * Adapted from stackoverflow.com
 */
public final class PasswordAuthentication {

    private static final int KEY_LENGTH = 128;

    private static final int ITERATIONS = 2048;

    private static final Pattern LAYOUT = Pattern.compile("(.{22})\\$(.{22})");

    /**
     * Hashes with randomly generated salt
     *
     * @param password password you want to hash
     * @return salted and hashed password which can be stored in database
     */

    public static String getHashSaltedPassword(String password)  {
        try {
            byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(KEY_LENGTH / 8);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(salt) + "$" + hash(password, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No algorithm or invalid SecretKeyFac", e);
        }
    }

    /**
     * Authentication of user using entered password and stored password in database
     *
     * @param inputPassword user's input password
     * @param storedPassword stored hashed password in database
     * @return true, if passwords are equal
     */

    public static boolean checkPasswords(String inputPassword, String storedPassword) {
        Matcher matcher = LAYOUT.matcher(storedPassword);
        if (!matcher.matches()) {
            throw new IllegalStateException("The stored password must have the form 'salt$hash'");
        }
        byte[] salt = Base64.getUrlDecoder().decode(matcher.group(1));
        String inputPasswordHash = null;
        try {
            inputPasswordHash = Base64.getUrlEncoder().withoutPadding().encodeToString(salt) + "$" + hash(inputPassword, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No algorithm or invalid SecretKeyFac", e);
        }
        return checkHashesEqual(inputPasswordHash, storedPassword);
    }

    /**
     * Utility method for hashing password and a given salt with PBKDF2WithHmacSHA512 algorithm
     *
     * @param password password to be hashed
     * @param salt salt for password
     * @return hashed and salted password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static String hash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key.getEncoded());
    }

    /**
     * Utility method against timing attack on passwords
     *
     * @param inputHash input hashed password
     * @param storedHash password stored in database
     * @return true, if hashes are equal
     */

    private static boolean checkHashesEqual(String inputHash, String storedHash) {
        byte[] inputPasswordBytes = inputHash.getBytes(StandardCharsets.US_ASCII);
        byte[] storedPasswordBytes = storedHash.getBytes(StandardCharsets.US_ASCII);
        //Checking the equality of passwords bytes array length
        int result = inputPasswordBytes.length ^ storedPasswordBytes.length;
        //Selecting the length of loop iteration, normally lengths should be equal
        int len = Math.min(inputPasswordBytes.length, storedPasswordBytes.length);
        //Byte by byte checking of hashes
        for (int i = 0; i < len; i++) {
            result |= inputPasswordBytes[i] ^ storedPasswordBytes[i];
        }
        return result == 0;
    }
}
