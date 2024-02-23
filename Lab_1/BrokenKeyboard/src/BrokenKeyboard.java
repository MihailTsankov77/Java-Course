import java.util.Arrays;

public class BrokenKeyboard {

    public static int calculateFullyTypedWords(String message, String brokenKeys){
        String[] words = message.split("\\s+");
        String[] brokenKey = brokenKeys.split("");

        int notPossibleWords = 0;

        for (String word:
                words) {
            if(word.isEmpty()){
                notPossibleWords++;
                continue;
            }

            for (String key:
                    brokenKey) {
                if(!key.isEmpty() && word.contains(key)){
                    notPossibleWords++;
                    break;
                }
            }
        }

        return  words.length-notPossibleWords;
    }
}