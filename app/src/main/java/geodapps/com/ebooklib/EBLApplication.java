package geodapps.com.ebooklib;

import android.app.Application;

import geodapps.com.ebooklib.data.DropBoxEbookRepository;
import geodapps.com.ebooklib.data.IEbookRepository;

/**
 *  Aplicación. Define de qué repositorio obtener la lista de libros que tendrá que implementar la intefaz IEbookRepository
 */
public class EBLApplication extends Application
{
    public static IEbookRepository repository;
    public static boolean logingIn=false; //Indica si se debe terminar una operación de login

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new DropBoxEbookRepository();  //Obtiene los libros de un repositorio DropBox
        repository.onCreate(this);                  //Le pasa como contexto la aplicación
    }
}
