package com.ge.apm.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;

public class GlobalExceptionHandler extends ExceptionHandlerWrapper {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private ExceptionHandler wrapped;

    public GlobalExceptionHandler(ExceptionHandler exceptionHandler) {
        this.wrapped = exceptionHandler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
        while (events.hasNext()) {
            ExceptionQueuedEvent event = events.next();
            ExceptionQueuedEventContext context = event.getContext();
            Throwable throwable = context.getException();
            // 移除该事件（避免重复处理）
            events.remove();
            try {
                FacesContext fc = context.getContext();
                if (fc == null || fc.getResponseComplete()) {
                    continue;
                }
                //对应接口已经设置了提示信息
                if( fc.getMessages().hasNext()){
                    continue;
                }
                String summary = "处理失败,请联系管理员！";
                String detail = "系统遇到意外错误，请稍后重试或联系管理员。";
                // 如果是业务异常，可提取具体信息
                if (throwable instanceof BusinessException) {
                    detail = throwable.getMessage();
                }
                if (throwable instanceof SystemException) {
                    detail = throwable.getMessage();
                }
                logger.error(detail, throwable);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
                fc.addMessage(null, msg);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        // 继续处理其他异常（如果有）
        getWrapped().handle();
    }
}
