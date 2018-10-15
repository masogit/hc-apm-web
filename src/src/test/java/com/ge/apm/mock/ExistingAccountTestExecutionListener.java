/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ge.apm.mock;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ExistingAccountTestExecutionListener extends AbstractTestExecutionListener {

	private static final Log logger = LogFactory.getLog(ExistingAccountTestExecutionListener.class);

	@Override
	public final int getOrder() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		logger.info("prepareTestInstance()");
	}

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		logger.info("beforeTestClass()");


		//make private static method accessible
        FacesContext mockFacesContext = mock(FacesContext.class);
        Method method = FacesContext.class.getDeclaredMethod("setCurrentInstance", FacesContext.class);
        ReflectionUtils.makeAccessible(method);
        method.invoke(null, mockFacesContext);

        //mock instance
        Application application = mock(Application.class);
        ELContext elContext = mock(ELContext.class);
        ExpressionFactory expressionFactory = mock(ExpressionFactory.class);
        ValueExpression valueExpression = mock(ValueExpression.class);
        UserContextService userContextService = mock(UserContextService.class);
//        UserAccount userAccount = mock(UserAccount.class);
        ExternalContext externalContext = mock(ExternalContext.class);
        Map<String, Object> applicationMap = mock(Map.class);

        //mock return value
        when(mockFacesContext.getApplication()).thenReturn(application);
        when(mockFacesContext.getELContext()).thenReturn(elContext);
        when(application.getExpressionFactory()).thenReturn(expressionFactory);
        when(expressionFactory.createValueExpression(any(ELContext.class), anyString(), any())).thenReturn(valueExpression);
        when(valueExpression.getValue(any())).thenReturn(userContextService);
        when(mockFacesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getApplicationMap()).thenReturn(applicationMap);

        ApplicationContext context = testContext.getApplicationContext();
        when(applicationMap.get(eq(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))).thenReturn(context);

        if (context != null) {

            UserAccountRepository userAccountDao = context.getBean(UserAccountRepository.class);
            UserAccount userAccount = userAccountDao.getByLoginName("tq-clinicals");
            when(userContextService.getLoginUser()).thenReturn(userAccount);

        }


	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		logger.info("beforeTestMethod()");
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		logger.info("afterTestMethod()");
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		logger.info("afterTestClass()");
	}
}
