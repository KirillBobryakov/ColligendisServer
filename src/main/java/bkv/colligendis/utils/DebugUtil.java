package bkv.colligendis.utils;

import bkv.colligendis.database.service.numista.IssuerService;
import bkv.colligendis.database.service.numista.RulerService;
import bkv.colligendis.services.AbstractService;

public class DebugUtil {

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public enum MESSAGE_LEVEL {
        ERROR, WARNING, INFO;
    };


    private static final boolean IS_NumistaEditPageUtil_TESTING = false;

    private static final boolean IS_Services_TESTING = true;
    private static final boolean IS_N4JEntities_TESTING = true;
    private static final boolean IS_AbstractService_TESTING = true;

    private static final boolean IS_NumistaServices_TESTING = true;
    private static final boolean IS_RulerService_TESTING = true;
    private static final boolean IS_IssuerService_TESTING = true;

    public static void showMessage(Object o, String message, MESSAGE_LEVEL messageLevel){
        switch (messageLevel){
            case ERROR : System.out.println(ANSI_RED + o.getClass().getSimpleName() + " : " + message + ANSI_RESET); break;
            case WARNING: System.out.println(ANSI_YELLOW + o.getClass().getSimpleName() + " : " + message + ANSI_RESET); break;
            case INFO: System.out.println(ANSI_GREEN + o.getClass().getSimpleName() + " : " + message + ANSI_RESET); break;
//            default: System.out.println(ANSI_BLACK + o.getClass().getSimpleName() + " : " + message + ANSI_RESET);
        }
    }

    public static void showError(Object o, String message){
        showMessage(o, message, MESSAGE_LEVEL.ERROR);
    }

    public static void showWarning(Object o, String message){
        showMessage(o, message, MESSAGE_LEVEL.WARNING);
    }

    public static void showInfo(Object o, String message){
        showMessage(o, message, MESSAGE_LEVEL.INFO);
    }



    public static void showServiceMessage(Object o, String message, MESSAGE_LEVEL messageLevel){
        if(IS_Services_TESTING){
            if(IS_NumistaServices_TESTING){
                if(o instanceof IssuerService && IS_IssuerService_TESTING){
                    showMessage(o, message, messageLevel);
                } else if(o instanceof RulerService && IS_RulerService_TESTING){
                    showMessage(o, message, messageLevel);
                }
            } else if (IS_N4JEntities_TESTING){
                if(o instanceof AbstractService && IS_AbstractService_TESTING){
                    showMessage(o, message, messageLevel);
                }
            }
        }
    }


    public static void printProperty(String name, String value, boolean isTestedOnCoin, boolean isTestedOnBanknote, boolean isTestedOnExonumia) {
        if (value != null && IS_NumistaEditPageUtil_TESTING) {
            StringBuffer stringBuffer = new StringBuffer();
            if (isTestedOnCoin || isTestedOnBanknote || isTestedOnExonumia) stringBuffer.append("tested on ");
            if (isTestedOnCoin) stringBuffer.append("coins ");
            if (isTestedOnBanknote) stringBuffer.append("banknotes ");
            if (isTestedOnExonumia) stringBuffer.append("exonumias ");
            stringBuffer.append("| ").append(name).append(": ").append(value);
            System.out.println(stringBuffer);
        }
    }






}
