package com.updater.utils.wildcard;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 04/09/2013
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class WildCard {

    /**
     * basic wildcarding system.
     *
     * @param pattern
     * @param text
     * @return
     */
    public static boolean wildcard(String pattern, String text) {
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        String [] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if(idx == -1) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }

        return true;
    }

}
