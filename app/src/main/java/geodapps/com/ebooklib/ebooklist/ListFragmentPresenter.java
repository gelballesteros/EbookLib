package geodapps.com.ebooklib.ebooklist;

import geodapps.com.ebooklib.EBLApplication;
import geodapps.com.ebooklib.data.IEbookRepository;

/**
 * Clase presentadora del Fragment que contine la lista de ebooks
 */
public class ListFragmentPresenter implements IEbookListContract.Presenter,IEbookRepository.EbookRepositoryCallBack
{
    IEbookListContract.Fragment mView;
    IEbookRepository ebookRepository;

    ListFragmentPresenter ()
    {
        ebookRepository = EBLApplication.repository;
    }

    @Override
    public void atachView(IEbookListContract.Fragment view)
    {
        mView=view;
        //si ya tiene los libros (en una recreación), los muestra
        if (ebookRepository.getBooks()!=null)
            mView.createList(mView.createListAdapter(ebookRepository.getBooks()));
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
        mView.showProgressBar();
        ebookRepository.adquireEbooks(this);
    }


    @Override
    public void ordenaLista(int modo)
    {
        ebookRepository.ordenaLista(modo);
        if (mView!=null)
            mView.updateList();

    }

    @Override
    public void onBooksLoaded()
    {
        if (mView!=null)
        {
            mView.createList(mView.createListAdapter(ebookRepository.getBooks()));
            mView.hideProgressBar();
        }
    }

    @Override
    /**
     * Obtiene mensaje de error al pedir libros al repositorio
     */
    public void onErrorLoading(String msg)
    {
        if (mView!=null)
        {
            mView.showMessage(msg);
            mView.hideProgressBar();
        }
    }

    @Override
    public void onBookMetadata(int indx) {

    }


}
