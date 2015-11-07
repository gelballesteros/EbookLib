package geodapps.com.ebooklib.login;

import android.content.Context;

/**
 * Interfaces de Vista y Presentador de la Activity de login
 */
public interface ILoginContract
{
    interface View
    {
        void onLogIn(boolean result,String msg);
    }

    interface Presenter
    {
        boolean needToLog();
        void logIn();//En caso de no ser necesario logueo, devolver true
        void onCreate(View view);
        void detachView();
    }
}
