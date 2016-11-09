package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sebas on 08/11/2016.
 */

public class Audio implements Parcelable {

    private String nombre;
    private String audio;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.audio);
    }

    public Audio() {
    }

    protected Audio(Parcel in) {
        this.nombre = in.readString();
        this.audio = in.readString();
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
}
