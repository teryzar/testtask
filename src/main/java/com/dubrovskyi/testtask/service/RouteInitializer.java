package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface RouteInitializer {

  Map<Integer, Set<Integer>> ROUTEMAP = new HashMap<>();

  void initializeRoutes();

  DirectResponse isDirect(int depSid, int arrSid);
}
