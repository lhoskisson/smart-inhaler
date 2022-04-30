package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Room;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ybeltagy.breathe.data.DataFinals;
import com.ybeltagy.breathe.data.Level;
import com.ybeltagy.breathe.data.BreatheDao;
import com.ybeltagy.breathe.collection.BreatheRoomDatabase;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.data.Tag;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.data.DiaryEntry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

// todo: as we add more testing methods, make helper methods to reduce code redundancy
//  and make the code maintainable.

// todo: consider reviewing/refactoring the tests if time allows

/**
 * Simple tests for updating entities in the DAO and querying between dates
 * <p>
 * Note: BreatheRepository is difficult to unit test (so this step has been skipped) as the
 * application must be passed to its constructor
 */
@RunWith(AndroidJUnit4.class)
public class BreatheDaoTest {
    private BreatheDao tBreatheDao;
    private BreatheRoomDatabase tBreatheRoomDatabase;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        tBreatheRoomDatabase = Room.inMemoryDatabaseBuilder(context, BreatheRoomDatabase.class).build();
        tBreatheDao = tBreatheRoomDatabase.breatheDao();
    }

    @After
    public void closeDB() {
        tBreatheDao.deleteAllIues(); // delete the records in the test database
        tBreatheRoomDatabase.close();
    }

    @Test
    public void updateInhalerUsageEventTest() {
        Instant rightNow = Instant.now();
        // original InhalerUsageEvent
        InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(rightNow);
        tBreatheDao.insert(tInhalerUsageEvent);

        // updated InhalerUsageEvent

        // simulating we gathered the wearableData 3 minutes later
        WearableData wearableData = new WearableData(rightNow.plus(3, ChronoUnit.MINUTES));
        float testTemp = 100;
        wearableData.setTemperature(testTemp);
        tInhalerUsageEvent.setWearableData(wearableData);
        tBreatheDao.updateInhalerUsageEvent(tInhalerUsageEvent);

        List<InhalerUsageEvent> allEvents = tBreatheDao.getAllIUEsTest();
        assert allEvents != null;
        assertEquals(allEvents.get(0).getWearableData().getTemperature(),
                testTemp,
                0);

        tBreatheDao.deleteAllIues();
    }

    @Test
    public void updateTwiceInhalerUsageEventTest() {
        Instant rightNow = Instant.now();
        // original InhalerUsageEvent
        InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(rightNow);
        tBreatheDao.insert(tInhalerUsageEvent);

        // simulating we gathered the wearableData 3 minutes later
        WearableData wearableData = new WearableData(rightNow.plus(3, ChronoUnit.MINUTES));
        float testTemp = 100;
        wearableData.setTemperature(testTemp);
        tInhalerUsageEvent.setWearableData(wearableData);
        tBreatheDao.updateInhalerUsageEvent(tInhalerUsageEvent);

        // now add diary entry activity with a new InhalerUsageEvent object with the same timestamp
        // (primary key) as the record we want to update
        DiaryEntry diaryEntry = new DiaryEntry(Tag.PREVENTATIVE, "Test Test");
        tInhalerUsageEvent.setDiaryEntry(diaryEntry);
        tBreatheDao.updateInhalerUsageEvent(tInhalerUsageEvent);

        List<InhalerUsageEvent> allEvents = tBreatheDao.getAllIUEsTest();
        assert allEvents != null;
        assertEquals(allEvents.size(), 1);
        // was the wearable data preserved when we updated the diary entry?
        assertEquals(allEvents.get(0).getWearableData().getTemperature(),
                testTemp,
                0);
        assertEquals(allEvents.get(0).getDiaryEntry().getTag(), Tag.PREVENTATIVE);
        tBreatheDao.deleteAllIues();
    }

    @Test
    public void updateDiaryEntryTest() {
        Instant rightNow = Instant.now();
        // original InhalerUsageEvent
        InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(rightNow);
        tBreatheDao.insert(tInhalerUsageEvent);

        // update only the diary entry using only the timestamp string of the Instant
        tBreatheDao.updateDiaryEntry(rightNow, Tag.PREVENTATIVE, "test test");

        // simulating we gathered the wearableData 3 minutes later
        float testTemp = 100;
        float testHumidity = 50;
        int testPM = 150;
        char testChar1 = 'A';
        char testChar2 = '2';
        tBreatheDao.updateWearableData(
                rightNow, rightNow.plusSeconds(180), testTemp, testHumidity, testPM, testChar1, testChar2);

        List<InhalerUsageEvent> allEvents = tBreatheDao.getAllIUEsTest();
        assert allEvents != null;
        assertEquals(allEvents.size(), 1);

        // was the wearable data preserved when we updated the diary entry?
        assertEquals(
                allEvents.get(0).getWearableData().getWearableDataTimeStamp(),
                rightNow.plusSeconds(180));
        assertEquals(allEvents.get(0).getWearableData().getTemperature(),
                testTemp,
                0);
        assertEquals(allEvents.get(0).getWearableData().getHumidity(),
                testHumidity,
                0);
        assertEquals(allEvents.get(0).getWearableData().getPm_count(), testPM);
        assertEquals(allEvents.get(0).getWearableData().getVoc_data(),
                testChar1);
        assertEquals(allEvents.get(0).getWearableData().getCo2_data(),
                testChar2);

        // was the diary entry data preserved?
        assertEquals(allEvents.get(0).getDiaryEntry().getTag(), Tag.PREVENTATIVE);
        assertEquals(allEvents.get(0).getDiaryEntry().getMessage(), "test test");
        tBreatheDao.deleteAllIues();
    }

    @Test
    public void updateWeatherDataTest(){

        Instant rightNow = Instant.now();
        // original InhalerUsageEvent
        InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(rightNow);
        tBreatheDao.insert(tInhalerUsageEvent);

        List<InhalerUsageEvent> allEvents = tBreatheDao.getAllIUEsTest();
        assert allEvents != null;
        assertEquals(allEvents.size(), 1);

        assertNotNull(allEvents.get(0).getWeatherData());
        assertEquals(allEvents.get(0).getWeatherData().getWeatherTemperature(),
                DataFinals.DEFAULT_FLOAT,0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherHumidity(),
                DataFinals.DEFAULT_FLOAT,0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherPrecipitationIntensity(),
                DataFinals.DEFAULT_FLOAT,0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherTreeIndex(),
                DataFinals.DEFAULT_LEVEL);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherGrassIndex(),
                DataFinals.DEFAULT_LEVEL);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherEPAIndex(),
                DataFinals.DEFAULT_INTEGER);

        float temp = 10;
        float humidity = 20;
        float precipitation = (float) .03;
        Level treeLevel = Level.HIGH;
        Level grassLevel = Level.VERY_LOW;
        int epaIndex = 200;

        tBreatheDao.updateWeatherData(tInhalerUsageEvent.getInhalerUsageEventTimeStamp(), temp,
                humidity, precipitation, treeLevel, grassLevel, epaIndex);

        allEvents = tBreatheDao.getAllIUEsTest();
        assert allEvents != null;
        assertEquals(allEvents.size(), 1);

        // was the wearable data preserved when we updated the diary entry?
        assertEquals(allEvents.get(0).getWeatherData().getWeatherTemperature(), temp, 0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherHumidity(), humidity, 0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherPrecipitationIntensity(),
                precipitation, 0);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherTreeIndex(), treeLevel);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherGrassIndex(), grassLevel);
        assertEquals(allEvents.get(0).getWeatherData().getWeatherEPAIndex(), epaIndex);
    }

    @Test
    public void getAnySingleInhalerUsageEventTest() {
        tBreatheDao.deleteAllIues();
        assertEquals(tBreatheDao.getAnySingleInhalerUsageEvent().size(), 0);

        Instant now = Instant.now();
        // create some inhaler usage events
        for (int i = 0; i < 10; i++) {
            Instant aTime = now.plus(i, ChronoUnit.DAYS);
            InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(aTime, null, new WearableData(aTime.plus(3, ChronoUnit.MINUTES)), null
            );
            tBreatheDao.insert(tInhalerUsageEvent);
        }
        assertEquals(tBreatheDao.getAnySingleInhalerUsageEvent().size(), 1);
    }

    @Test
    public void eventsBetweenDatesTest() {
        Instant now = Instant.now();
        // create some inhaler usage events
        for (int i = 0; i < 10; i++) {
            Instant aTime = now.minus(i, ChronoUnit.DAYS);
            InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent
                    (aTime, null, new WearableData(
                    aTime.plus(3, ChronoUnit.MINUTES)), null);
            tBreatheDao.insert(tInhalerUsageEvent);
        }

        // query between dates
        int week = 7;
        List<InhalerUsageEvent> thisPastWeek =
                tBreatheDao.loadAllInhalerUsageEventsBetweenDatesTest(
                        now.minus(week, ChronoUnit.DAYS), now);
        assert thisPastWeek != null;
        assertEquals(thisPastWeek.size(),
                week + 1); // note: the range is inclusive

        tBreatheDao.deleteAllIues();
    }
}
