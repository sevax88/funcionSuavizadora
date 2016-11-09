package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sebas on 08/11/2016.
 */

public class ServiceResponse implements Parcelable {

    private Poi[] listminor;
    private Audio[] audios;

    public Poi[] getListminor() {
        return listminor;
    }

    public void setListminor(Poi[] listminor) {
        this.listminor = listminor;
    }

    public Audio[] getAudios() {
        return audios;
    }

    public void setAudios(Audio[] audios) {
        this.audios = audios;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(this.listminor, 0);
        dest.writeParcelableArray(this.audios, 0);
    }

    public ServiceResponse() {
    }

    protected ServiceResponse(Parcel in) {
        this.listminor = (Poi[]) in.readParcelableArray(Poi.class.getClassLoader());
        this.audios = (Audio[]) in.readParcelableArray(Audio.class.getClassLoader());
    }

    public static final Parcelable.Creator<ServiceResponse> CREATOR = new Parcelable.Creator<ServiceResponse>() {
        public ServiceResponse createFromParcel(Parcel source) {
            return new ServiceResponse(source);
        }

        public ServiceResponse[] newArray(int size) {
            return new ServiceResponse[size];
        }
    };
}
