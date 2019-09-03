package com.dubrovskyi.testtask.web;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.dubrovskyi.testtask.service.RouteInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BusRouteController {

  private final RouteInitializer routeInitializer;

  public BusRouteController(RouteInitializer routeInitializer) {
    this.routeInitializer = routeInitializer;
  }

  @GetMapping("/direct")
  public ResponseEntity<DirectResponse> direct(@RequestParam(name = "dep_sid") int depSid,
                                               @RequestParam(name = "arr_sid") int arrSid ) {

    DirectResponse direct = routeInitializer.isDirect(depSid, arrSid);
    return new ResponseEntity<>(direct, HttpStatus.OK);
  }
}
