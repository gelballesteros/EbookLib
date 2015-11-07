package geodapps.com.ebooklib.ebooklist;


import java.io.InputStream;

import geodapps.com.ebooklib.data.IEbookRepository;

/**
 * Clase presentadora del Fragment que contine la lista de ebooks
 */
public class ListFragmentPresenter implements IEbookListContract.Presenter,IEbookRepository.EbookRepositoryCallBack
{
    IEbookListContract.View mView;
    IEbookRepository ebookRepository;

    ListFragmentPresenter (IEbookRepository repo)
    {
        ebookRepository = repo;
    }

    @Override
    public void atachView(IEbookListContract.View view)
    {
        mView=view;
    }

    @Override
    public void detachView()
    {
        mView=null;
    }

    @Override
    /**
     * Llama al repositorio a obtener libros y se registra como callBack de la operación
     */
    public void askForBooks()
    {
        ebookRepository.adquireEbooks(this);
    }

    @Override
    public InputStream getImageInputStream() {
        return null;
    }

    @Override
    public void onBooksLoaded()
    {

    }

    @Override
    /**
     * Obtiene mensaje de error al pedir libros al repositorio
     */
    public void onErrorLoading(String msg)
    {

    }
}
