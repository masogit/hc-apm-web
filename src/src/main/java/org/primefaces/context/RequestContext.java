package org.primefaces.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.component.datatable.TableState;
import org.primefaces.expression.ComponentNotFoundException;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.visit.ResetInputVisitCallback;
import org.springframework.context.ApplicationContext;

public class RequestContext{

    private static final Logger LOG = Logger.getLogger(RequestContext.class.getName());
    private final static String INSTANCE_KEY = RequestContext.class.getName();
    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("(\r\n|\n\r|\r|\n)");

    private WidgetBuilder widgetBuilder;
    private AjaxRequestBuilder ajaxRequestBuilder;
    private CSVBuilder csvBuilder;
    private FacesContext context;
    private ApplicationContext applicationContext;
    private Boolean ignoreAutoUpdate;
    private Boolean rtl;

    public static RequestContext getCurrentInstance() {
        return getCurrentInstance(FacesContext.getCurrentInstance());
    }
    
    public static RequestContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext != null) {
            RequestContext context = (RequestContext) facesContext.getAttributes().get(INSTANCE_KEY);
            return context;
        }

        return null;
    }

    public RequestContext(FacesContext context) {
        this.context = context;
    }

    public boolean isAjaxRequest() {
        return context.getPartialViewContext().isAjaxRequest();
    }

    public void addCallbackParam(String name, Object value) {
        getCallbackParams().put(name, value);
    }

    public void execute(String script) {
        getScriptsToExecute().add(script);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCallbackParams() {
        if (getAttributes().get(CALLBACK_PARAMS_KEY) == null) {
            getAttributes().put(CALLBACK_PARAMS_KEY, new HashMap<String, Object>());
        }
        return (Map<String, Object>) getAttributes().get(CALLBACK_PARAMS_KEY);
    }

        @SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        if (getAttributes().get(EXECUTE_SCRIPT_KEY) == null) {
            getAttributes().put(EXECUTE_SCRIPT_KEY, new ArrayList<String>());
        }
        return (List<String>) getAttributes().get(EXECUTE_SCRIPT_KEY);
    }

    public WidgetBuilder getWidgetBuilder() {
        if (this.widgetBuilder == null) {
            this.widgetBuilder = new WidgetBuilder(context, getApplicationContext().getConfig());
        }

        return widgetBuilder;
    }
    
    public AjaxRequestBuilder getAjaxRequestBuilder() {
        if (this.ajaxRequestBuilder == null) {
            this.ajaxRequestBuilder = new AjaxRequestBuilder(context);
        }

        return ajaxRequestBuilder;
    }
    
    public CSVBuilder getCSVBuilder() {
        if (this.csvBuilder == null) {
            this.csvBuilder = new CSVBuilder(context);
        }

        return csvBuilder;
    }
    
    public void scrollTo(String clientId) {
        this.execute("PrimeFaces.scrollTo('" + clientId + "');");
    }

    public void update(String clientId) {
        // call SEF to validate if a component with the clientId exists
        if (context.isProjectStage(ProjectStage.Development)) {
            try {
                SearchExpressionFacade.resolveClientId(context, context.getViewRoot(), clientId);
            }
            catch (ComponentNotFoundException e) {
                LOG.severe(e.getMessage());
            }
        }

        context.getPartialViewContext().getRenderIds().add(clientId);
    }

    public void update(Collection<String> clientIds) {

        // call SEF to validate if a component with the clientId exists
        if (context.isProjectStage(ProjectStage.Development)) {
            if (clientIds != null) {
                for (String clientId : clientIds) {
                    try {
                        SearchExpressionFacade.resolveClientId(context, context.getViewRoot(), clientId);
                    }
                    catch (ComponentNotFoundException e) {
                        LOG.severe(e.getMessage());
                    }
                }
            }
        }

        context.getPartialViewContext().getRenderIds().addAll(clientIds);
    }

    public void reset(Collection<String> expressions) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for (String expression : expressions) {
            reset(visitContext, expression);
        }
    }
    
    public void reset(String... expressions) {
        if (expressions == null) {
            return;
        }

        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for (String expression : expressions) {
            reset(visitContext, expression);
        }
    }

    public void reset(String expressions) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        reset(visitContext, expressions);
    }

    private void reset(VisitContext visitContext, String expressions) {
        UIViewRoot root = context.getViewRoot();

        List<UIComponent> components = SearchExpressionFacade.resolveComponents(context, root, expressions);
        for (UIComponent component : components) {
            component.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
        }
    }

    public void openDialog(String outcome) {
        context.getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);
    }

    public void openDialog(String outcome, Map<String, Object> options, Map<String, List<String>> params) {
        context.getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);

        if (options != null) {
            context.getAttributes().put(Constants.DIALOG_FRAMEWORK.OPTIONS, options);
        }

        if (params != null) {
            context.getAttributes().put(Constants.DIALOG_FRAMEWORK.PARAMS, params);
        }
    }

    public void closeDialog(Object data) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String pfdlgcid = params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);

        if (data != null) {
            Map<String, Object> session = context.getExternalContext().getSessionMap();
            session.put(pfdlgcid, data);
        }

        this.execute("PrimeFaces.closeDialog({pfdlgcid:'" + pfdlgcid + "'});");
    }

    public void showMessageInDialog(FacesMessage message) {

        String summary = ComponentUtils.escapeText(message.getSummary());
        summary = replaceNewLineWithHtml(summary);

        String detail = ComponentUtils.escapeText(message.getDetail());
        detail = replaceNewLineWithHtml(detail);

        this.execute("PrimeFaces.showMessageInDialog({severity:\"" + message.getSeverity()
                + "\",summary:\"" + summary
                + "\",detail:\"" + detail + "\"});");
    }

    public void release() {
        widgetBuilder = null;
        ajaxRequestBuilder = null;
        context = null;
        applicationContext = null;
    }
    
    public Map<Object, Object> getAttributes() {
        return context.getAttributes();
    }
    
    public PrimeApplicationContext getApplicationContext() {
        return PrimeRequestContext.getCurrentInstance().getApplicationContext();
    }

    public boolean isSecure() {
        Object request = context.getExternalContext().getRequest();

        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).isSecure();
        }
        else {
            try {
                Method method = request.getClass().getDeclaredMethod("isSecure", new Class[0]);
                return (Boolean) method.invoke(request, null);
            }
            catch (Exception e) {
                return false;
            }
        }
    }

    public boolean isIgnoreAutoUpdate() {
        if (ignoreAutoUpdate == null) {
            Object ignoreAutoUpdateObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.IGNORE_AUTO_UPDATE_PARAM);
            ignoreAutoUpdate = (null != ignoreAutoUpdateObject && "true".equals(ignoreAutoUpdateObject));
        }

        return ignoreAutoUpdate;
    }
    
    public boolean isRTL() {
        if (rtl == null) {
            String param = context.getExternalContext().getInitParameter(Constants.ContextParams.DIRECTION);
            if (param == null) {
                rtl = false;
            }
            else {
                ELContext elContext = context.getELContext();
                ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
                ValueExpression expression = expressionFactory.createValueExpression(elContext, param, String.class);
                String expressionValue = (String) expression.getValue(elContext);

                rtl = (expressionValue == null) ? false : expressionValue.equalsIgnoreCase("rtl");
            }
        }

        return rtl;
    }

    public void clearTableStates() {
        this.context.getExternalContext().getSessionMap().remove(Constants.TABLE_STATE);
    }
    
    public void clearTableState(String key) {
        Map<String, Object> sessionMap = this.context.getExternalContext().getSessionMap();
        Map<String, TableState> dtState = (Map) sessionMap.get(Constants.TABLE_STATE);
        if (dtState != null) {
            dtState.remove(key);
        }
    }

    public static String replaceNewLineWithHtml(String text) {
        if (text == null) {
            return null;
        }

        Matcher match = PATTERN_NEW_LINE.matcher(text);
        if (match.find()) {
            return match.replaceAll("<br/>");
        }

        return text;
    }
    
}
