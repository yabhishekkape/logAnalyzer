package com.demo.logAnalyser;

import com.demo.logAnalyser.entity.LogEvent;
import com.demo.logAnalyser.utiltiy.DataBaseManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DataBaseManagerTest {

    @Test
    public void insertAndReadEvents_OK() {

        String expectedOutput="ID:0,  LOGID:1,  DURATION:2,  HOST:4,  TYPE:3\n";

        DataBaseManager DataBaseManager = new DataBaseManager();
        DataBaseManager.startHSQLDB();
        DataBaseManager.dropHSQLDBLTable();
        DataBaseManager.createHSQLDBTable();
        LogEvent e = new LogEvent("1", 2L, Optional.of("3"), Optional.of("4"));
        List<LogEvent> list = new ArrayList<>();
        list.add(e);
        DataBaseManager.insertEvents(list);
        String result = DataBaseManager.readEvents();
        assertEquals(result, expectedOutput);
        DataBaseManager.stopHSQLDB();
    }

    @Test
   public  void   insertAndReadEvents_insertNullList() {
        DataBaseManager DataBaseManager = new DataBaseManager();
        DataBaseManager.startHSQLDB();
        DataBaseManager.dropHSQLDBLTable();
        DataBaseManager.createHSQLDBTable();
        List<LogEvent> list = new ArrayList<>();
        DataBaseManager.insertEvents(list);
        String result = DataBaseManager.readEvents();
        assertEquals(result, "");
        DataBaseManager.stopHSQLDB();
    }

}

