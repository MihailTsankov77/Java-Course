public class IPValidator {
    private static boolean validateMask(String mask){
        if(!mask.matches("-?\\d+") || (mask.charAt(0) =='0' && mask.length()>1)){
            return false;
        }

        int numberMask = Integer.parseInt(mask);

        return numberMask>=0 && numberMask<=255;

    }
    public static boolean validateIPv4Address(String str){
        String[] masks = str.split("\\.");

        if(masks.length!=4){
            return false;
        }

        for (String mask :
                masks) {
            if(!validateMask(mask)){
                return false;
            }
        }

        return true;
    }
}