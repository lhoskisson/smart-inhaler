package com.ybeltagy.breathe.data;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Instant;

/**
 * Encapsulates local environmental data gathered from the user's smart pin/wearable
 * - uses wearableDataTimeStamp (from when it was collected) as its primary key
 * - currently only contains one actual field of data (temperature)
 * - the other placeholder fields will be used by future teams when additional sensors are added to
 * to the smart pin/wearable
 * <p>
 * TODO: write static WearableData createWearableData() to collect smart pin data
 * and create a WearableData object
 */
@Entity(tableName = "WearableData_table")
public class WearableData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Wearable_Data_UTC_ISO_8601_date_time")
    private Instant wearableDataTimeStamp; // when this wearableData was collected

    private float temperature; // The default null values should not make sense.
    private float humidity; //fixme: these variable names are asymmetric compared to weather data.

    private int   pm_count;
    private int   voc_data;
    private int   co2_data;

    /**
     * Added this constructor for our iteration.
     * Perhaps in the future, the smart pin will send a timestamp with its data but for our current
     * team, this is not necessary.
     */
    @SuppressLint("NewApi")
    @Ignore
    public WearableData() {

        this(Instant.now(),
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_INTEGER,
                DataFinals.DEFAULT_INTEGER,
                DataFinals.DEFAULT_INTEGER);

    }

    public WearableData(@NonNull Instant wearableDataTimeStamp) {
        this(wearableDataTimeStamp,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_INTEGER,
                DataFinals.DEFAULT_INTEGER,
                DataFinals.DEFAULT_INTEGER);
    }

    @Ignore
    public WearableData(@NonNull Instant wearableDataTimeStamp,
                        float temperature,
                        float humidity,
                        int pm_count,
                        int voc_data,
                        int co2_data){

        setWearableDataTimeStamp(wearableDataTimeStamp);
        setTemperature(temperature);
        setPm_count(pm_count);
        setHumidity(humidity);
        setVoc_data(voc_data);
        setCo2_data(co2_data);
    }

    /**
     * @return true if all the the data members are different from DataFinal default values.
     */
    public boolean isDataValid(){
        return isTemperatureValid() &&
                isHumidityValid() &&
                isPm_countValid() &&
                isVoc_dataValid() &&
                isCo2_dataValid();
    }

    public boolean isTemperatureValid(){
        return temperature != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isHumidityValid(){
        return humidity != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isPm_countValid(){return pm_count != DataFinals.DEFAULT_INTEGER;}

    public boolean isVoc_dataValid(){ return voc_data != DataFinals.DEFAULT_INTEGER; }

    public boolean isCo2_dataValid(){
        return co2_data != DataFinals.DEFAULT_INTEGER;
    }

    @NonNull
    public Instant getWearableDataTimeStamp() {
        return wearableDataTimeStamp;
    }

    public void setWearableDataTimeStamp(@NonNull Instant wearableTimeStamp) {
        this.wearableDataTimeStamp = wearableTimeStamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = DataUtilities.nanGuard(temperature);
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = DataUtilities.nanGuard(humidity);
    }

    public int getPm_count() { return pm_count; }

    public void setPm_count(int pm_count) {
        if(pm_count < 0) pm_count = DataFinals.DEFAULT_INTEGER; // invalid data.
        this.pm_count = pm_count;
    }

    public int getVoc_data() { return voc_data; }

    public void setVoc_data(int voc_data) {
        this.voc_data = voc_data;
    }

    public int getCo2_data() { return co2_data; }

    public void setCo2_data(int co2_data) {
        this.co2_data = co2_data;
    }
}
