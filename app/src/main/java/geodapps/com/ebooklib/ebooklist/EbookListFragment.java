package geodapps.com.ebooklib.ebooklist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import geodapps.com.ebooklib.R;
import geodapps.com.ebooklib.data.Ebook;
import geodapps.com.ebooklib.data.IEbookRepository;

public class EbookListFragment extends Fragment implements IEbookListContract.Fragment
{
    ProgressDialog pd;
    private IEbookListContract.Presenter presenter;
    private IEbookListContract.Activity activity;
    private RecyclerView recView;
    private TextView emptyView;
    private static final int RECYCLER_MODE_LIST = 0;
    private int recyclerMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ListFragmentPresenter();
        presenter.attachView(this);
        presenter.askForBooks();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_ebook_list, container, false);
        //Inicialización RecyclerView
        recView = (RecyclerView) v.findViewById(R.id.rec_view);
        emptyView = (TextView) v.findViewById(R.id.empty_view);
        recView.setHasFixedSize(true);      //De momento el tamaño de la lista va a ser fija, por lo que lo indica para optimizar rendiminto
        recView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));//De inicio modo lista
        //Muesta menú
        setHasOptionsMenu(true);
        //Indica al presenter que el fragment está presente
        presenter.attachView(this);
        //Guarda referencia a la Activity
        try {
            this.activity = (IEbookListContract.Activity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IEbookListContract.Activity");
        }
        return v;
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        this.activity = null;
        presenter.detachView();
    }

    @Override
    public EbookListAdapter createListAdapter(List<Ebook> books)
    {
        if (recView==null)
            return null;
        if (books.size()==0)
        {
            recView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return null;
        }
        else
        {
            recView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            return new EbookListAdapter(books);
        }
    }

    @Override
    public void createList(EbookListAdapter adapter)
    {
        if (adapter!=null)
        {
            recView.setAdapter(adapter);
            adapter.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activity.showDetail(recView.getChildAdapterPosition(v));
                }
            });
        }
    }

    @Override
    public void showProgressBar()
    {
        pd = new ProgressDialog(getActivity());
        pd.setTitle(getString(R.string.db_progress_title));
        pd.setMessage(getString(R.string.db_progress_download_msg));
        pd.setIndeterminate(true);
        pd.show();
    }

    @Override
    public void hideProgressBar()
    {
        if (pd!=null && pd.isShowing())
            pd.hide();
    }

    @Override
    public void showMessage(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void updateList()
    {
      if (recView.getAdapter()!=null)
          recView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getActivity().getMenuInflater().inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_change_recycler:
                if (recyclerMode == RECYCLER_MODE_LIST)
                    recView.setLayoutManager(new GridLayoutManager(getActivity(),2));    //TODO: Ajustar número de columnas dinámicamente
                else
                    recView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                recyclerMode=++recyclerMode%2;
                break;
            case R.id.action_order_by_date:
                presenter.ordenaLista(IEbookRepository.MODO_ORDENA_LISTA_FECHA);
                break;
            case R.id.action_order_by_title:
                presenter.ordenaLista(IEbookRepository.MODO_ORDENA_LISTA_TITULO);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
