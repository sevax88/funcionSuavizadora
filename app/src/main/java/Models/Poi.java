package Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sebas on 03/10/2016.
 */
public class Poi implements Parcelable {
    @SerializedName("minor")
    private int minor;
    @SerializedName("message")
    private String message;

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.minor);
        dest.writeString(this.message);
    }

    public Poi() {
    }

    protected Poi(Parcel in) {
        this.minor = in.readInt();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<Poi> CREATOR = new Parcelable.Creator<Poi>() {
        public Poi createFromParcel(Parcel source) {
            return new Poi(source);
        }

        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
}
