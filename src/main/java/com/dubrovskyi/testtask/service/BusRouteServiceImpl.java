package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import gnu.trove.TDecorators;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Set;
import java.util.stream.StreamSupport;


@Service
public class BusRouteServiceImpl implements BusRouteService {

  private static final String                        SPLIT_PATTERN__WHITESPACE = " ";
  private static final int                           FIRST_POSITION            = 0;
  private static final Logger                        log                       = LoggerFactory.getLogger(BusRouteServiceImpl.class);
  private              SetMultimap<Integer, Integer> routemap;

  @Autowired
  private ApplicationArguments appArgs;

  @Value("${split.pattern}")
  private String splitPattern;

  @PostConstruct
  @Override
  public void initializeRoutes() {
    log.info("Start initializing routes");
    long timeStart = System.currentTimeMillis();
    routemap = createRouteMap();
    boolean routesConsistant = true;
    if (appArgs.getSourceArgs().length == 0) {
      log.error("No parameter specified, no routes will be initialized");
      return;
    }
    String filePath = appArgs.getSourceArgs()[FIRST_POSITION];
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        routesConsistant = fillRoute(line);
      }
    }
    catch (IOException e) {
      log.error("File {} not found or can not be read, no routes will be initialized", filePath);
    }
    if (!routesConsistant) {
      log.error("File {} has wrong format. Some routes may not be initialized", filePath);
    }
    else {
      log.info("All routes were initialized in {} ms", (System.currentTimeMillis() - timeStart));
    }
  }

  @Override
  public DirectResponse isDirect(int depSid, int arrSid) {
    Set<Integer> departureRoutes = routemap.get(depSid);
    Set<Integer> arrivalRoutes = routemap.get(arrSid);
    departureRoutes.retainAll(arrivalRoutes);
    return new DirectResponse(depSid, arrSid, !departureRoutes.isEmpty());
  }

  //tried some in-memoty DBs but for 100000 routes with 1000 stops is enough
  private SetMultimap<Integer, Integer> createRouteMap() {
    return Multimaps.newSetMultimap(
        TDecorators.wrap(new TIntObjectHashMap<>()),
        () -> TDecorators.wrap(new TIntHashSet()));
  }

  private boolean fillRoute(String line) {
    if (splitPattern == null) {
      splitPattern = SPLIT_PATTERN__WHITESPACE;
    }
    Iterable<String> strings = Splitter.on(splitPattern).split(line);

    try {
      int routeId = Integer.parseInt(strings.iterator().next());

      StreamSupport.stream(strings.spliterator(), false)
        .skip(1)
        .filter(s -> !s.isEmpty())
        .map(Integer::parseInt)
        .forEach(x -> routemap.put(x, routeId));
    }
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  //---------------Just for testing purposes------------------------------
  protected SetMultimap<Integer, Integer> getRoutemap() {
    return routemap;
  }

  protected void setTestMap(SetMultimap<Integer, Integer> routemap) {
    this.routemap = routemap;
  }
}
