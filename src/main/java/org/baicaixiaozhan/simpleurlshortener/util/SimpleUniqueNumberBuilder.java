package org.baicaixiaozhan.simpleurlshortener.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
@Component
public class SimpleUniqueNumberBuilder implements UniqueNumberBuilder<String> {

    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public String build() {
        return decimal2Base62(atomicInteger.getAndIncrement());
    }

    private static final String[] code = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };


    private String decimal2Base62(Integer number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.insert(0, code[number % 62]);
            number /= 62;
        }
        return result.toString();
    }
}
