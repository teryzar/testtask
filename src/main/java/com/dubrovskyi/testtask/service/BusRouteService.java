package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public interface BusRouteService {

  void initializeRoutes();

  DirectResponse isDirect(int depSid, int arrSid);
}
