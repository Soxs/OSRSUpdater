package com.updater.utils.multiplier;

import java.math.BigInteger;

/**
 * Created by Kevin on 26/09/13.
 */
public class MultiplierResolver {
    public static int modInverse(String integer) {
        BigInteger modulus = new BigInteger(String.valueOf(1L << 32));
        BigInteger m1 = new BigInteger(integer);
        return m1.modInverse(modulus).intValue();
    }
}
