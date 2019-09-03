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
public class RouteInitializerImpl implements RouteInitializer {

  private static final String LOG_MESSAGE__WRONG_FILE = "Something wrong with file ";
  private final Logger log = LoggerFactory.getLogger(RouteInitializerImpl.class);

  @Autowired
  ApplicationArguments appArgs;

  @PostConstruct
  @Override
  public void initializeRoutes() {
    BufferedReader reader;
    String line;
    String filePath = appArgs.getSourceArgs()[0];
    try {
      reader = new BufferedReader(new FileReader(filePath));
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        fillRouteMap(line);
      }
      System.out.println(1);
    } catch (IOException e) {
      log.error(LOG_MESSAGE__WRONG_FILE + filePath);
    }
  }

  @Override
  public DirectResponse isDirect(int depSid, int arrSid) {
    Set<Integer> departureRoutes = ROUTEMAP.get(depSid);
    Set<Integer> arrivalRoutes = ROUTEMAP.get(arrSid);
    departureRoutes.retainAll(arrivalRoutes);
    return new DirectResponse(depSid, arrSid, !departureRoutes.isEmpty());
  }


  private void fillRouteMap(String line) {
    String[] split = line.split("\\D");

    int routeId = Integer.parseInt(split[0]);

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
}
