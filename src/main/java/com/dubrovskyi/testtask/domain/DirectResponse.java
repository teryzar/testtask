package com.dubrovskyi.testtask.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class DirectResponse {

  @JsonProperty("dep_sid")
  private Integer departureStationId;
  @JsonProperty("arr_sid")
  private Integer arrivalStationId;
  @JsonProperty("direct_bus_route")
  private Boolean directBusRroute;
}
