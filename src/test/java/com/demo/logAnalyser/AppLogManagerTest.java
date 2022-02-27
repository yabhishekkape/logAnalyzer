package com.demo.logAnalyser;

import com.demo.logAnalyser.utiltiy.AppLogFileReader;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

 public class AppLogManagerTest {



     private static final String INPUT_PATH_EMPTY_FILE = "src/test/java/resources/empty.text";
     private static final String INPUT_PATH_SAMPLE_WORKING = "src/test/java/resources/input.txt";
    @Test
     public void createFlaggedEvents_Empty() {
        AppLogManager AppLogManager = new AppLogManager(INPUT_PATH_EMPTY_FILE);
        AppLogFileReader reader = new AppLogFileReader();
        AppLogManager.setLinesMap(reader.readFromFile(INPUT_PATH_EMPTY_FILE));
        AppLogManager.createFlaggedEvents();
        assertTrue(AppLogManager.getFlaggedEventList().isEmpty());
    }

    @Test
   public  void createFlaggedEvents_Working() {
        AppLogManager AppLogManager = new AppLogManager(INPUT_PATH_SAMPLE_WORKING);
        AppLogFileReader reader = new AppLogFileReader();
        AppLogManager.setLinesMap(reader.readFromFile(INPUT_PATH_SAMPLE_WORKING));
        AppLogManager.createFlaggedEvents();
        assertEquals(AppLogManager.getFlaggedEventList().size(), 2);
        Assertions.assertEquals(AppLogManager.getFlaggedEventList().get(0).getId(), "scsmbstgra");
        Assertions.assertEquals(AppLogManager.getFlaggedEventList().get(0).getDuration(), 5L);
        Assertions.assertEquals(AppLogManager.getFlaggedEventList().get(0).getType(), Optional.of("APPLICATION_LOG"));
        Assertions.assertEquals(AppLogManager.getFlaggedEventList().get(0).getHost(), Optional.of("12345"));
    }

}