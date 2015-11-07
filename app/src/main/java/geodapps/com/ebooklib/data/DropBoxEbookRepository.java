package geodapps.com.ebooklib.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.util.ArrayList;

import geodapps.com.ebooklib.EBLApplication;
import geodapps.com.ebooklib.R;

/**
 * Implementación de repositorio de ebooks mediante conexión a DropBox
 */
public class DropBoxEbookRepository implements IEbookRepository
{

   //Identificador y clave secreta de la app en la plataforma Dropbox
    private static final String APP_KEY = "znqmeaxiry80r7i";
    private static final String APP_SECRET = "h6z6leni441xjy5";

    // You don't need to change these, leave them alone.
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    EbookRepositoryCallBack mCallBack;
    private ArrayList<Ebook> mEbooks;
    DropboxAPI<AndroidAuthSession> mApi;
    Context cnt;


    @Override
    public ArrayList<Ebook> getBooks()
    {
        return mEbooks;
    }


    @Override
    public void onCreate(Context cnt)
    {
        this.cnt=cnt;
        // Crea una nueva AuthSession para usar la API de Dropbox.
        mApi = new DropboxAPI<>(buildSession(cnt));
    }

    @Override
    public boolean needToLog()
    {
        //Puede que ya esté logueado de sesiones anteriores
        return !mApi.getSession().isLinked();
    }



    /**
     * El login con la cuenta de Dropbox se realiza a través del protocolo oAuth, por el cual se redirige al usuario
     * a la página de autorización de Dropbox en la que el usuario dará permiso a esta aplicación para acceder a su cuenta,
     * devolviendo en su caso un token que se enviará en todas las peticiones
     */
    @Override
    public void logIn(LoginRepositoryCallBack callBack)
    {
        AndroidAuthSession session = mApi.getSession();

        //Comprueba si tiene autorización ya
        if (mApi.getSession().authenticationSuccessful()) {
            try {
                session.finishAuthentication();// Completa la autorización
                storeAuth(session);// Guarda el token de autorización para futuras entradas
                EBLApplication.logingIn = false;//se finaliza la operación de logIn
                callBack.onLogIn(true, null);
            } catch (IllegalStateException e) {
                EBLApplication.logingIn = false;//se finaliza la operación de logIn
                callBack.onLogIn(false, cnt.getString(R.string.db_login_error) + " :" + e.getLocalizedMessage());
            }
        } else {
            //Comprueba si se ha intentado loguear
            if (EBLApplication.logingIn) {
                //Si se ha intentado loguear y no ha funcionado, vuelve a la pantalla inicial indicando que no se ha logueado
                EBLApplication.logingIn = false;//se termina la operación de logIn
                callBack.onLogIn(false, cnt.getString(R.string.db_need_to_log));
            } else {
                //No se había intentado loguear, lo intenta
                EBLApplication.logingIn = true;//se comienza la operación de logIn
                session.startOAuth2Authentication(cnt);//Va a abandonar la aplicación para realizar la autorización
            }
        }
    }


    /**
     * Crea una sesión
     * @param cnt Contexto
     * @return Sesión de Dropbox
     */
    private AndroidAuthSession buildSession(Context cnt)
    {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session,cnt);
        return session;
    }

    /**
     * Obtiene el token de autorización guardado en preferencias
     */
    private void loadAuth(AndroidAuthSession session, Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;
        session.setOAuth2AccessToken(secret);
    }

    /**
     * Guarda el token de autorización en preferencias
     */
    private void storeAuth(AndroidAuthSession session)
    {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = cnt.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
        }
    }


    /**
     * Comienza una descarga en segundo plano de los ebooks de la cuenta de Dropbox
     */
    @Override
    public void adquireEbooks(EbookRepositoryCallBack callBack)
    {
        mCallBack = callBack;
        new GetAsyncDBEbooks().execute();
    }

    class GetAsyncDBEbooks extends  AsyncTask<String, String,String>
    {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }

}
