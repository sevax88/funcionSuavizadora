package Models;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Sebas on 08/11/2016.
 */

public class ServiceResponse implements Parcelable {

    private List<Poi> listminor;
    private List<Audio> audios;
    private Integer umbralPi,umbralPasillo;

    public List<Poi> getListminor() {
        return listminor;
    }

    public void setListminor(List<Poi> listminor) {
        this.listminor = listminor;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public Integer getUmbralPi() {
        return umbralPi;
    }

    public void setUmbralPi(Integer umbralPi) {
        this.umbralPi = umbralPi;
    }

    public Integer getUmbralPasillo() {
        return umbralPasillo;
    }

    public void setUmbralPasillo(Integer umbralPasillo) {
        this.umbralPasillo = umbralPasillo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(listminor);
        dest.writeTypedList(audios);
        dest.writeValue(this.umbralPi);
        dest.writeValue(this.umbralPasillo);
    }

    public ServiceResponse() {
    }

    protected ServiceResponse(Parcel in) {
        this.listminor = in.createTypedArrayList(Poi.CREATOR);
        this.audios = in.createTypedArrayList(Audio.CREATOR);
        this.umbralPi = (Integer) in.readValue(Integer.class.getClassLoader());
        this.umbralPasillo = (Integer) in.readValue(Integer.class.getClassLoader());
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
