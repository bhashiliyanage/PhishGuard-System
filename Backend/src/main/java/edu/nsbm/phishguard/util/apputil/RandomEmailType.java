package edu.nsbm.phishguard.util.apputil;

import edu.nsbm.phishguard.enums.Type;

import java.util.concurrent.ThreadLocalRandom;

public class RandomEmailType {
    public static Type getRandomEmailType() {
        return ThreadLocalRandom.current().nextBoolean() ? Type.NORMAL : Type.PHISHING;
    }
}
