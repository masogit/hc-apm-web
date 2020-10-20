package com.ge.apm.view.wo;

import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.service.wo.AssetLifecycleEventService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetLifecycleEventServiceTest {

    @Test
    public void testStatus() {

        AssetLifecycleEvent alce = new AssetLifecycleEvent();
        AssetLifecycleEventService controller = new AssetLifecycleEventService();

        String x = "{\"messages\":[\"状态：Closed\"],\"title\":\"12345\",\"status\":\"Closed\"}";
        alce.setDescription(x);
        assertEquals("Closed", controller.getMsStatus(alce));

        alce.setDescription("{\"message\":\"bad json\"");
        assertNull(controller.getMsStatus(alce));
    }

}