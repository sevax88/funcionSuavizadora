package Models;

/**
 * Created by Sebas on 03/10/2016.
 */
public class Poi {

    private String nombreEquipo;
    private String audio;

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Poi(String nombreEquipo, String audio) {
        this.nombreEquipo = nombreEquipo;
        this.audio = audio;
    }
}
