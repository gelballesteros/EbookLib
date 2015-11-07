package geodapps.com.ebooklib.login;

import geodapps.com.ebooklib.EBLApplication;
import geodapps.com.ebooklib.data.IEbookRepository;

/**
 * Clase presentadora de la pantalla de login
 */
public class LoginPresenter implements ILoginContract.Presenter, IEbookRepository.LoginRepositoryCallBack
{
    IEbookRepository mRepository;
    ILoginContract.View mView;

    @Override
    /**
     * Vincula con la vista y obtiene repositorio
     */
    public void onCreate(ILoginContract.View view)
    {
        mView = view;
        mRepository = EBLApplication.repository;
    }

    @Override
    /**
     * Desvincula de la vista
     */
    public void detachView()
    {
       mView = null;
    }

    @Override
    public boolean needToLog()
    {
        if (EBLApplication.logingIn)    //Si se estaba en medio de una operación de logIn, la continua
        {
            mRepository.logIn(this);
            return true;
        }
        return mRepository.needToLog(); //Si no, pregunta al repositorio
    }

    @Override
    /**
     * Redirige petición de login al repositorio
     */
    public void logIn()
    {
        mRepository.logIn(this);    //Pide loguear al repositorio y se registra como callback
    }

    @Override
    /**
     * Recibe resultado del login
     */
    public void onLogIn(boolean result,String msg)
    {
        if (mView!=null)
            mView.onLogIn(result,msg);
    }


}
