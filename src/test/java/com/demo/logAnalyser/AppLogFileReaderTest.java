package com.demo.logAnalyser;

import com.demo.logAnalyser.entity.Event;
import com.demo.logAnalyser.utiltiy.AppLogFileReader;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

 public class AppLogFileReaderTest {

    private static final String INPUT_PATH_INVALID = "invalid_path";
    private static final String INPUT_PATH_EMPTY_FILE = "src/test/java/resources/empty.text";
    private static final String INPUT_PATH_SAMPLE_WORKING = "src/test/java/resources/input.txt";
    private static final String INPUT_PATH_MALFORMED = "src/test/java/resources/malformed-input";

    @Test
    public void readFromFile_noFile() {
        AppLogFileReader reader = new AppLogFileReader();
        assertThrows(IllegalArgumentException.class, () -> {reader.readFromFile(INPUT_PATH_INVALID);});
    }

    @Test
   public void readFromFile_emptyFile() {
        AppLogFileReader reader = new AppLogFileReader();
        Map<String, List<Event>> resultMap = reader.readFromFile(INPUT_PATH_EMPTY_FILE);
        Assertions.assertTrue(resultMap.isEmpty());
    }

    @Test
    public void readFromFile_sampleWorking() {
        AppLogFileReader reader = new AppLogFileReader();
        Map<String, List<Event>> resultMap = reader.readFromFile(INPUT_PATH_SAMPLE_WORKING);
        Assertions.assertEquals(resultMap.size(), 3);
        Assertions.assertEquals(resultMap.get("scsmbstgra").size(), 2);
        Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getTimestamp(), 1491377495212L);
        Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getType(), Optional.of("APPLICATION_LOG"));
        Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getHost(), Optional.of("12345"));
    }

    @Test
   public void readFromFile_sampleMalformed() {
        AppLogFileReader reader = new AppLogFileReader();
        assertThrows(IllegalArgumentException.class, () -> {reader.readFromFile(INPUT_PATH_MALFORMED);});
    }

}

