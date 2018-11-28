package org.primefaces.context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.WidgetBuilder;
import org.springframework.context.ApplicationContext;

/**
 * A RequestContext is a helper class consisting of several utilities.
 * RequestContext is thread-safe and scope is same as FacesContext.
 * Current instance can be retrieved as;
 * <blockquote>
 *  RequestContext.getCurrentInstance(context);
 * </blockquote>
 */
public abstract class RequestContext {

    private static final String INSTANCE_KEY = RequestContext.class.getName();

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

    public static void setCurrentInstance(RequestContext context, FacesContext facesContext) {
        if (context == null) {
            if (facesContext != null) {
                facesContext.getAttributes().remove(INSTANCE_KEY);
            }
        }
        else {
            facesContext.getAttributes().put(INSTANCE_KEY, context);
        }
    }

    /**
     * @return
     * @deprecated Use {@link PrimeFaces#isAjaxRequest()} instead
     */
    @Deprecated
    public abstract boolean isAjaxRequest();

    /**
     * @deprecated Use PrimeFaces.ajax().addCallbackParam instead
     */
    @Deprecated
    public abstract void addCallbackParam(String name, Object value);

    /**
     * @return all callback parameters added in the current request.
     */
    public abstract Map<String, Object> getCallbackParams();

    /**
     * @return all scripts added in the current request.
     */
    public abstract List<String> getScriptsToExecute();

    /**
     * Use {@link PrimeFaces#executeScript(java.lang.String)}
     * 
     * @param script
     * @deprecated  Use {@link PrimeFaces#executeScript(java.lang.String)}
     */
    @Deprecated
    public abstract void execute(String script);

    /**
     * @param clientId
     * @deprecated Use {@link PrimeFaces#scrollTo(java.lang.String)} instead
     */
    @Deprecated
    public abstract void scrollTo(String clientId);

    /**
     * @deprecated Use PrimeFaces.ajax().update() instead
     */
    @Deprecated
    public abstract void update(String name);

    /**
     * @deprecated Use PrimeFaces.ajax().update() instead
     */
    @Deprecated
    public abstract void update(Collection<String> collection);

    /**
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)} instead
     */
    @Deprecated
    public abstract void reset(String expressions);

    /**
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)} instead
     */
    @Deprecated
    public abstract void reset(Collection<String> expressions);

    /**
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)} instead
     */
    @Deprecated
    public abstract void reset(String... expressions);

    /**
     * @return Shared WidgetBuilder instance of the current request
     */
    public abstract WidgetBuilder getWidgetBuilder();

    /**
     * @return Shared AjaxRequestBuilder instance of the current request
     */
    public abstract AjaxRequestBuilder getAjaxRequestBuilder();

    /**
     * @return Shared Client Side Validation builder instance of the current request
     */
    public abstract CSVBuilder getCSVBuilder();

    /**
     * Use FacesContext#getAttributes()
     */
    @Deprecated
    public abstract Map<Object, Object> getAttributes();

    /**
     * @deprecated Use PrimeFaces.dialog().openDynamic() instead
     */
    @Deprecated
    public abstract void openDialog(String outcome);

    /**
     * @deprecated Use PrimeFaces.dialog().openDynamic() instead
     */
    @Deprecated
    public abstract void openDialog(String outcome, Map<String, Object> options, Map<String, List<String>> params);

    /**
     * @deprecated Use PrimeFaces.dialog().closeDynamic instead
     */
    @Deprecated
    public abstract void closeDialog(Object data);

    /**
     * @deprecated Use PrimeFaces.dialog().showMessageDynamic instead
     */
    @Deprecated
    public abstract void showMessageInDialog(FacesMessage message);

    /**
     * @return ApplicationContext instance.
     */
    public abstract ApplicationContext getApplicationContext();


    /**
     * Clear resources.
     */
    public abstract void release();

    /**
     * Returns a boolean indicating whether this request was made using a secure channel, such as HTTPS.
     */
    public abstract boolean isSecure();

    /**
     * @return <code>true</code> if {@link AutoUpdatable} components should not be updated automatically in this request.
     */
    public abstract boolean isIgnoreAutoUpdate();

    public abstract boolean isRTL();

    /**
     * @deprecated  Use {@link PrimeFaces#clearTableState()} instead
     */
    @Deprecated
    public abstract void clearTableStates();

    /**
     * @param clientId
     * @deprecated Use {@link PrimeFaces#clearTableState(java.lang.String)} instead
     */
    @Deprecated
    public abstract void clearTableState(String clientId);
}
