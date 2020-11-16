package poc.techath.kobildemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefStorage {


    public static SharedPreferences getSharedPreferences(Context context) {
        return  context.getSharedPreferences("Kobil", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static String readString(Context context, String key, String defaultVal){
        return getSharedPreferences(context).getString(key, defaultVal);
    }

    public static void writeString(Context context, String key, String value){
        getEditor(context).putString(key, value).apply();
    }



}
