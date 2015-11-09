package geodapps.com.ebooklib.ebooklist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import geodapps.com.ebooklib.R;
import geodapps.com.ebooklib.data.Ebook;

/**
 * Adaptador del RecyclerView para mostrar la lista de ebooks
 */
public class EbookListAdapter extends RecyclerView.Adapter<EbookListAdapter.EbookViewHolder> implements View.OnClickListener
{
    List<Ebook> mEbookList;
    private View.OnClickListener listener;

    public EbookListAdapter(List<Ebook> ebookList)
    {
        mEbookList=ebookList;
    }

    @Override
    public EbookViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ebook_list_item, parent, false);
        itemView.setOnClickListener(this);
        return new EbookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EbookViewHolder holder, int position)
    {
        holder.bindEbook(mEbookList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mEbookList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public static class EbookViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView imgFrontPage;
        private TextView txtTitle;

        public EbookViewHolder(View itemView)
        {
            super(itemView);
            imgFrontPage = (ImageView) itemView.findViewById(R.id.imgFrontTitle);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }

        public void bindEbook(Ebook ebook)
        {
            //imgFrontPage
            txtTitle.setText(ebook.title);
        }
    }
}
