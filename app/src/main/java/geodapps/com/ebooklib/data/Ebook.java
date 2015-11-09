package geodapps.com.ebooklib.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Clase modelo ebook.
 */
public class Ebook implements Parcelable
{
    public String path;             //Ruta del ebook (en este caso ruta en el WS)
    public String title;            //Título del ebook
    public Long creationDate;       //Fecha de creación en milisegundos
    public byte[] frontPage;        //Almacena la portada del ebook en byte[]

    public Ebook (Parcel p)
    {
        readFromParcel(p);
    }

    public Ebook ()
    {

    }

    public static final Creator<Ebook> CREATOR = new Creator<Ebook>() {
        @Override
        public Ebook createFromParcel(Parcel in) {
            return new Ebook(in);
        }

        @Override
        public Ebook[] newArray(int size) {
            return new Ebook[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeLong(creationDate);
    }

    private void readFromParcel(Parcel in)
    {
        path = in.readString();
        title = in.readString();
        creationDate = in.readLong();
    }
}
