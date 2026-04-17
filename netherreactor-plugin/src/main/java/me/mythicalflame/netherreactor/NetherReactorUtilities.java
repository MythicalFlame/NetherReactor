package me.mythicalflame.netherreactor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class NetherReactorUtilities
{
    private NetherReactorUtilities() {}

    public static Component minimessage(String msg)
    {
        return MiniMessage.miniMessage().deserialize(msg);
    }

    public static final class RomanNumeral
    {
        private final int NUMBER;
        private final String LETTER;
        private static final RomanNumeral[] romanNumerals = {new RomanNumeral(100, "C"),
                                                             new RomanNumeral(90, "XC"),
                                                             new RomanNumeral(50, "L"),
                                                             new RomanNumeral(40, "XL"),
                                                             new RomanNumeral(10, "X"),
                                                             new RomanNumeral(9, "IX"),
                                                             new RomanNumeral(5, "V"),
                                                             new RomanNumeral(4, "IV"),
                                                             new RomanNumeral(1, "I")};

        private RomanNumeral(int num, String letter)
        {
            this.NUMBER = num;
            this.LETTER = letter;
        }

        public static String getRomanNumber(int number)
        {
            StringBuilder result = new StringBuilder();
            for (RomanNumeral romanNumeral : romanNumerals)
            {
                while (number >= romanNumeral.NUMBER)
                {
                    result.append(romanNumeral.LETTER);
                    number -= romanNumeral.NUMBER;
                }
            }
            return result.toString();
        }
    }
}
