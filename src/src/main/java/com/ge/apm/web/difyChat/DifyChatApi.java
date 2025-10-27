package com.ge.apm.web.difyChat;

import com.ge.apm.service.difyHttp.DifyStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.faces.context.FacesContext;

@RestController
@RequestMapping("/apmChat")
public class DifyChatApi {
  private static final Logger log = LoggerFactory.getLogger(DifyChatApi.class);
  @Autowired
  private DifyStreamService difyStreamService;

  @RequestMapping(value = "chat", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> handleRequest(@RequestBody DiFyChatRequest request) {
    if (request.getParamStr() != null && !request.getParamStr().trim().isEmpty()) {
      try {
        String result = difyStreamService.processDifyStream(request);
        return ResponseEntity.ok(result);
      } catch (Exception e) {
        e.printStackTrace();
        log.info(e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null,
                new javax.faces.application.FacesMessage(
                        javax.faces.application.FacesMessage.SEVERITY_INFO,
                        e.getMessage(),
                        null
                ));
      }
    }
    return (ResponseEntity<String>) ResponseEntity.badRequest();
  }

}
