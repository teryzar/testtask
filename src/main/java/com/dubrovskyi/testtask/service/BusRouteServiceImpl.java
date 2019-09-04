package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class BusRouteServiceImpl implements BusRouteService {

  private static final String LOG_MESSAGE__NO_FILEPATH        = "No filepath specified, no routes will be initialized";
  private static final String LOG_MESSAGE__WRONG_FILE         = "File %s not found or can not be read, no routes will be initialized";
  private static final String LOG_MESSAGE__WRONG_FILE_FORMAT  = "File %s has wrong format. Some routes may not be initialized";
  private static final String LOG_MESSAGE__ROUTES_INITIALIZED = "All routes were initialized";
  private static final int    FIRST_POSITION                  = 0;
  private static final String NON_NUMERIC_REGEX_PATTERN       = "\\D";
  private final Logger        log                             = LoggerFactory.getLogger(BusRouteServiceImpl.class);

  @Autowired
  private ApplicationArguments appArgs;

  @PostConstruct
  @Override
  public void initializeRoutes() {
    String line;
    boolean routesConsistant = true;
    if (appArgs.getSourceArgs().length == 0) {
      log.error(LOG_MESSAGE__NO_FILEPATH);
      return;
    }
    String filePath = appArgs.getSourceArgs()[FIRST_POSITION];
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        routesConsistant = fillRoute(line);
      }
    } catch (IOException e) {
      log.error(String.format(LOG_MESSAGE__WRONG_FILE, filePath));
    }
    if (!routesConsistant) {
      log.error(LOG_MESSAGE__WRONG_FILE_FORMAT, filePath);
    } else {
      log.info(LOG_MESSAGE__ROUTES_INITIALIZED);
    }
  }

  @Override
  public DirectResponse isDirect(int depSid, int arrSid) {
    boolean isDirect = false;
    Set<Integer> departureRoutes = ROUTEMAP.get(depSid);
    Set<Integer> arrivalRoutes = ROUTEMAP.get(arrSid);
    if (departureRoutes != null && arrivalRoutes != null) {
      departureRoutes.retainAll(arrivalRoutes);
      isDirect = !departureRoutes.isEmpty();
    }
    return new DirectResponse(depSid, arrSid, isDirect);
  }


  private boolean fillRoute(String line) {
    String[] split = line.split(NON_NUMERIC_REGEX_PATTERN);

    try {
      int routeId = Integer.parseInt(split[FIRST_POSITION]);

      if (split.length > 1) {
        for (int i = 1; i < split.length; i++) {
          int stopId = Integer.parseInt(split[i]);

          if (ROUTEMAP.containsKey(stopId)) {
            ROUTEMAP.get(stopId).add(routeId);
          } else {
            Set routeSet = new HashSet();
            routeSet.add(routeId);
            ROUTEMAP.put(stopId, routeSet);
          }
        }
      }
    }
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}
