package geodapps.com.ebooklib.ebookdetail;

import geodapps.com.ebooklib.EBLApplication;
import geodapps.com.ebooklib.data.Ebook;
import geodapps.com.ebooklib.data.IEbookRepository;
import geodapps.com.ebooklib.ebookdetail.IEbookDetailContract.Presenter;

/**
 * Presenter de la vista detalle de un ebook
 */
public class EbookDetailPresenter implements Presenter, IEbookRepository.EbookRepositoryCallBack
{
    IEbookDetailContract.View mView;
    IEbookRepository ebookRepository;
    public int ebookShowing = -1;//índice al libro que se muestra

    EbookDetailPresenter ()
    {
        ebookRepository = EBLApplication.repository;
    }


    @Override
    public void attachView(IEbookDetailContract.View view)
    {
        mView = view;
        //si ya tiene los libros (en una recreación), los muestra
        if (ebookShowing>=0)
            onBookMetadata(ebookShowing);

    }

    @Override
    public void detachView()
    {
        mView = null;
    }

    @Override
    public void showDetail(int indx)
    {
        if (ebookRepository.getBooks().get(indx).frontPage==null) //No ha descargado datos
        {
            mView.showProgressBar();
            ebookRepository.getEbookData(indx, this);
        }
        else
            onBookMetadata(indx);
    }

    @Override
    public void onBooksLoaded() {

    }

    @Override
    public void onErrorLoading(String msg) {

    }

    @Override
    public void onBookMetadata(int indx)
    {

        if (mView!=null)
        {
            mView.setImage(ebookRepository.getBooks().get(indx).frontPage);
            mView.setTitle(ebookRepository.getBooks().get(indx).title);
            mView.hideProgressBar();
            ebookShowing=indx;
        }
    }
}
