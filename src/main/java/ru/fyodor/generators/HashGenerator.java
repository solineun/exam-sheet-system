package ru.fyodor.generators;

import ru.fyodor.generators.MerkleTree.MerkleTree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HashGenerator {

    private final MessageDigest messageDigest;

    public HashGenerator() {
        try {
            this.messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateHash(byte[] input) {
        return messageDigest.digest(input);
    }

    public static byte[] calculateHashFromArgs(byte[]... args) {
        List<byte[]> dataList = new ArrayList<>();
        for (byte[] data : args) {
            dataList.add(data);
        }

        return MerkleTree.generateTree(dataList).getHash();
    }

    public static byte[] getRandomBytes() {
        byte[] randomBytes = new byte[32];

        ThreadLocalRandom
                .current()
                .nextBytes(randomBytes);

        return randomBytes;
    }
}
