package geodapps.com.ebooklib.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import geodapps.com.ebooklib.EBLApplication;
import geodapps.com.ebooklib.R;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Implementación de repositorio de ebooks mediante conexión a DropBox
 */
public class DropBoxEbookRepository implements IEbookRepository
{

   //Identificador y clave secreta de la app en la plataforma Dropbox
    private static final String APP_KEY = "znqmeaxiry80r7i";
    private static final String APP_SECRET = "h6z6leni441xjy5";

    // Constantes de login en Dropbox
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private final static String EPUB_EXTENSION =".epub";
    private final static int EBOOK_CREATION_DATE_INDX = 0;   //La fecha de creación es la primera del array de fechas

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
    public void ordenaLista(final int modo)
    {
            Collections.sort(mEbooks, new Comparator<Ebook>() {
                @Override
                public int compare(Ebook e1, Ebook e2)
                {
                    if (modo == IEbookRepository.MODO_ORDENA_LISTA_FECHA)
                        return e1.creationDate.compareTo(e2.creationDate);
                    if (modo == IEbookRepository.MODO_ORDENA_LISTA_TITULO)
                        return e1.title.compareTo(e2.title);
                    else
                        return 0;
                }
            });
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
        new AsyncGetDBEbooks().execute();
    }

    class AsyncGetDBEbooks extends  AsyncTask<String, String, Boolean>
    {
        //Objetos para parsear los ebooks.

        DropboxAPI.Entry entries = null;
        Ebook ebook;//Clase ebook propia
        Date date;
        SimpleDateFormat dbFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ", Locale.US);//Formato de la fecha en los metadatos
        String error = "";//Para guardar errores
        long timeInicio;

        @Override
        protected Boolean doInBackground(String... params)
        {
            mEbooks = new ArrayList<>();   //Reinicia el array de libros.
            //Pide ebooks empezando en la carpeta raíz de DB
            //Sólo logueará error si no puede acceder a la carpeta raíz
           timeInicio=System.currentTimeMillis();
            return downloadEbooks("");
        }

        @Override
        protected void onPostExecute(Boolean res) {
            Log.i("Fin dowload", String.valueOf(System.currentTimeMillis()- timeInicio));
            if (res)
                mCallBack.onBooksLoaded();
            else
                mCallBack.onErrorLoading(error);
            super.onPostExecute(res);
        }

       /* *//**
         * Descarga los ebooks de Dropbox, almacenando título, fecha de creación y path del archivo en objetos Ebook
         * Se llama recursivamente para buscar dentro de las carpetas
         *  Extremadamente lento
         *
         *//*
        public boolean downloadWholeEbooks(String path)
        {
             try
            {

                //Pide todos los archivos de la ruta indicada sin límite de archivos
                //Como la app está registrada para obtener únicamente ebooks, sólo obtendrá ese tipo de archivos junto con directorios
                entries = mApi.metadata(path, 0, null, true, null);
            }
            catch (DropboxException e)
            {
                error = cnt.getString(R.string.db_error_downloading) + " :" + e.getMessage();
                return false;
            }

            for (DropboxAPI.Entry e : entries.contents)
            {
                if (!e.isDeleted)
                {
                    if (e.isDir)
                        downloadEbooks(e.path);//Busca ebooks en su interior
                    else if (e.fileName().contains(EPUB_EXTENSION)) //Sólo obtiene ebooks de tipo .epub
                    {
                        try {
                            stream = mApi.getFileStream(e.path, null);//Obtiene la última versión
                            book = epubReader.readEpub(stream);
                            ebook = new Ebook();
                            ebook.path=e.path;
                            //Convierte la fecha a milisegundos para ordenar más rápido
                            try {
                                date=format.parse(book.getMetadata().getDates().get(EBOOK_CREATION_DATE_INDX).getValue());
                                ebook.creationDate = date.getTime();
                            } catch (ParseException e1) {
                                ebook.creationDate= Long.valueOf(0);//Fecha en formato diferente
                            }
                            ebook.title=book.getTitle();
                            book.getCoverImage().getInputStream();
                            mEbooks.add(ebook);
                        } catch (Exception e1) {//Recoge la excepción genérica pero no la transmite para no mostrar demasiados mensajes de error
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return true;
        }*/



        /**
         * Descarga los ebooks de Dropbox, almacenando únicamente el nombre del archivo, mucho más rápido
         * Se llama recursivamente para buscar dentro de las carpetas
         */
        public boolean downloadEbooks(String path)
        {
            try
            {

                //Pide todos los archivos de la ruta indicada sin límite de archivos
                //Como la app está registrada para obtener únicamente ebooks, sólo obtendrá ese tipo de archivos junto con directorios
                entries = mApi.metadata(path, 0, null, true, null);
            }
            catch (DropboxException e)
            {
                error = cnt.getString(R.string.db_error_downloading) + " :" + e.getMessage();
                return false;
            }

            for (DropboxAPI.Entry e : entries.contents)
            {
                if (!e.isDeleted)
                {
                    if (e.isDir)
                        downloadEbooks(e.path);//Busca ebooks en su interior
                    else if (e.fileName().contains(EPUB_EXTENSION)) //Sólo obtiene ebooks de tipo .epub
                    {
                        try {
                            ebook = new Ebook();
                            ebook.title=e.fileName();
                            ebook.path=e.path;
                            //Obtenemos la fecha de modificación del archivo
                            //Convierte la fecha a milisegundos para ordenar más rápido
                            try {
                                date=dbFormat.parse(e.modified);
                                ebook.creationDate = date.getTime();
                            } catch (ParseException e1) {
                                ebook.creationDate= (long) 0;//Fecha en formato diferente
                            }
                            mEbooks.add(ebook);
                        } catch (Exception e1) {//Recoge la excepción genérica para no mostrar demasiados mensajes de error
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return true;
        }


    }


    @Override
    /**
     * Descarga los metadatos de Dropbox para ese ebook
     */
    public void getEbookData(int indx,EbookRepositoryCallBack callBack)
    {
        mCallBack = callBack;
        new AsyncGetDBEbookMetadata().execute(indx);
    }

    class AsyncGetDBEbookMetadata extends  AsyncTask<Integer, String, Boolean>
    {
        //Objetos para parsear los ebooks.

        DropboxAPI.Entry entries = null;
        DropboxAPI.DropboxInputStream stream;
        EpubReader epubReader = new EpubReader();
        Book book;  //Clase ebook de la librería EpubReader
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//Formato de la fecha en los metadatos
        String error = "";//Para guardar errores
        long timeInicio;
        int ebookIndx;



        @Override
        protected Boolean doInBackground(Integer... params) {
            ebookIndx = params[0];
            timeInicio=System.currentTimeMillis();
            return downloadEbookMetadata(ebookIndx);
        }

        @Override
        protected void onPostExecute(Boolean res) {
            Log.i("Fin download detail", String.valueOf(System.currentTimeMillis()- timeInicio));
            if (res)
                mCallBack.onBookMetadata(ebookIndx);
            else
                mCallBack.onErrorLoading(error);
            super.onPostExecute(res);
        }



        public boolean downloadEbookMetadata(int indx)
        {
            try
            {

                //Pide todos los archivos de la ruta indicada sin límite de archivos
                //Como la app está registrada para obtener únicamente ebooks, sólo obtendrá ese tipo de archivos junto con directorios
                entries = mApi.metadata(mEbooks.get(indx).path, 0, null, false, null);
            }
            catch (DropboxException e)
            {
                error = cnt.getString(R.string.db_error_downloading) + " :" + e.getMessage();
                return false;
            }

            try {
                stream = mApi.getFileStream(entries.path, null);//Obtiene la última versión
                book = epubReader.readEpub(stream);
                mEbooks.get(indx).frontPage = book.getCoverImage().getData();
                mEbooks.get(indx).title = book.getTitle();
            } catch (Exception e1) {//Recoge la excepción genérica para no mostrar demasiados mensajes de error
                e1.printStackTrace();
            }
            return true;
        }
    }
}
