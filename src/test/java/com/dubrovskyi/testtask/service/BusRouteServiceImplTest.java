package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
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

import static com.dubrovskyi.testtask.service.BusRouteService.ROUTEMAP;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class BusRouteServiceImplTest {

  @InjectMocks
  private BusRouteService busRouteService = new BusRouteServiceImpl();
  @Mock ApplicationArguments appArgs;

  @Before
  public void setup() throws Exception {
    createTestFile();
    MockitoAnnotations.initMocks(this);

  }

  @BeforeMethod
  public void initMocks(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void initializeRoutesNoArgument() throws Exception {
    ROUTEMAP.clear();
    String[] strings = { };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (new HashMap<>(), ROUTEMAP);
  }

  @Test
  public void initializeRoutesMissingFile() throws Exception {
    ROUTEMAP.clear();
    String[] strings = { "wrongfile" };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (new HashMap<>(), ROUTEMAP);
  }


  @Test
  public void initializeRoutesOk() throws Exception {
    String[] strings = { "test.txt" };
    when(appArgs.getSourceArgs()).thenReturn(strings);
    busRouteService.initializeRoutes();
    assertEquals (getCorrectRoutemap(), ROUTEMAP);
  }


  @Test
  public void isDirectTrue() {
    ROUTEMAP.putAll(getCorrectRoutemap());
    DirectResponse direct = busRouteService.isDirect(3, 6);
    assertEquals(3, direct.getDepSid());
    assertEquals(6, direct.getArrSid());
    assertTrue(direct.isDirectBusRroute());
  }

  @Test
  public void isDirectFalse() {
    ROUTEMAP.putAll(getCorrectRoutemap());
    DirectResponse direct = busRouteService.isDirect(0, 5);
    assertEquals(0, direct.getDepSid());
    assertEquals(5, direct.getArrSid());
    assertFalse(direct.isDirectBusRroute());
  }

  @After
  public void tearDown() throws IOException {
    Files.deleteIfExists(Paths.get("test.txt"));
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

  private Map<Integer, Set<Integer>> getCorrectRoutemap() {
    Map<Integer, Set<Integer>> correctMap = new HashMap<>();
    Set<Integer> set = new HashSet<>();
    set.add(0);
    set.add(2);
    correctMap.put(0, set);

    set = new HashSet<>();
    set.add(0);
    set.add(1);
    correctMap.put(1, set);

    set = new HashSet<>();
    set.add(0);
    correctMap.put(2, set);

    set = new HashSet<>();
    set.add(0);
    set.add(1);
    correctMap.put(3, set);

    set = new HashSet<>();
    set.add(0);
    set.add(2);
    correctMap.put(4, set);

    set = new HashSet<>();
    set.add(1);
    correctMap.put(5, set);

    set = new HashSet<>();
    set.add(1);
    set.add(2);
    correctMap.put(6, set);

    return correctMap;
  }

}