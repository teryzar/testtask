package com.dubrovskyi.testtask.web;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.dubrovskyi.testtask.service.BusRouteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@RunWith(MockitoJUnitRunner.class)
@WebMvcTest
public class BusRouteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private BusRouteService busRouteService;

  private BusRouteController busRouteController;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    busRouteController = new BusRouteController(busRouteService);
    this.mockMvc = MockMvcBuilders.standaloneSetup(busRouteController).build();

  }



  @Test
  public void direct() throws Exception {
    given(busRouteService.isDirect(3, 6)).willReturn(new DirectResponse(3, 6, true));

    mockMvc.perform(get("/api/direct?dep_sid=3&arr_sid=6"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"dep_sid\":3,\"arr_sid\":6,\"direct_bus_route\":true}"));
  }
}