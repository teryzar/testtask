package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import gnu.trove.TDecorators;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.ApplicationArguments;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class BusRouteServiceImplTest {

  @InjectMocks
  private BusRouteServiceImpl busRouteService = new BusRouteServiceImpl();
  @Mock ApplicationArguments appArgs;

  @Before
  public void setup() throws Exception {
    createTestFile();
    createInconsistentTestFile();
    MockitoAnnotations.initMocks(this);

  }

  @BeforeMethod
  public void initMocks(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void initializeRoutesNoArgument() throws Exception {
    String[] strings = { };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (createEmptyRouteMap(), busRouteService.getRoutemap());
  }

  @Test
  public void initializeRoutesMissingFile() throws Exception {
    String[] strings = { "wrongfile" };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (createEmptyRouteMap(), busRouteService.getRoutemap());
  }

  @Test
  public void initializeRouteInconsistaentFile() throws Exception {
    String[] strings = { "testInconsistent" };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (createEmptyRouteMap(), busRouteService.getRoutemap());
  }


  @Test
  public void initializeRoutesOk() throws Exception {
    busRouteService.setTestMap(createEmptyRouteMap());
    String[] strings = { "test.txt" };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (getCorrectRoutemap(), busRouteService.getRoutemap());
  }


  @Test
  public void isDirectTrue() {
    busRouteService.setTestMap(getCorrectRoutemap());
    DirectResponse direct = busRouteService.isDirect(3, 6);
    assertEquals(3, direct.getDepSid());
    assertEquals(6, direct.getArrSid());
    assertTrue(direct.isDirectBusRroute());
  }

  @Test
  public void isDirectFalse() {
    busRouteService.setTestMap(getCorrectRoutemap());
    DirectResponse direct = busRouteService.isDirect(0, 5);
    assertEquals(0, direct.getDepSid());
    assertEquals(5, direct.getArrSid());
    assertFalse(direct.isDirectBusRroute());
  }

  @After
  public void tearDown() throws IOException {
    Files.deleteIfExists(Paths.get("test.txt"));
    Files.deleteIfExists(Paths.get("testInconsistent.txt"));
  }

  private void createTestFile() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("3\n");
    sb.append("0 0 1 2 3 4\n");
    sb.append("1 3 1 6 5\n");
    sb.append("2 0 6 4\n");
    BufferedWriter bwr =  new BufferedWriter(new FileWriter(new File("test.txt")));
    bwr.write(sb.toString());
    bwr.flush();
    bwr.close();
  }

  private void createInconsistentTestFile() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("3\n");
    sb.append("0 0 k 2 3 4\n");
    sb.append("1 3 1 6 5\n");
    sb.append("2 0 6 4\n");
    BufferedWriter bwr =  new BufferedWriter(new FileWriter(new File("testInconsistent.txt")));
    bwr.write(sb.toString());
    bwr.flush();
    bwr.close();
  }

  private SetMultimap<Integer, Integer> getCorrectRoutemap() {
    SetMultimap<Integer, Integer> correctMap = createEmptyRouteMap();

    correctMap.put(0, 0);
    correctMap.put(0, 2);

    correctMap.put(1, 0);
    correctMap.put(1, 1);

    correctMap.put(2, 0);

    correctMap.put(3, 0);
    correctMap.put(3, 1);

    correctMap.put(4, 0);
    correctMap.put(4, 2);

    correctMap.put(5, 1);

    correctMap.put(6, 1);
    correctMap.put(6, 2);

    return correctMap;
  }

  private SetMultimap<Integer,Integer> createEmptyRouteMap() {
    return Multimaps.newSetMultimap(
        TDecorators.wrap(new TIntObjectHashMap<>()),
        () -> TDecorators.wrap(new TIntHashSet()));
  }

}