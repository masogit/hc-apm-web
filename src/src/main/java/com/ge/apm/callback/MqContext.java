package com.ge.apm.callback;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Data
public class MqContext implements Serializable {
    private Date time;
    private String type;
    private HashMap context;
    private String routingKey;
    private String exChange;
}
