package com.ge.apm.view.wo;

import com.ge.apm.domain.AssetLifecycleEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetLifecycleEventControllerTest {

    @Test
    public void testStatus() {

        AssetLifecycleEvent alce = new AssetLifecycleEvent();
        AssetLifecycleEventController controller = new AssetLifecycleEventController();

        String x = "{\"messages\":[\"状态：Closed\"],\"title\":\"12345\",\"status\":\"Closed\"}";
        alce.setDescription(x);
        assertEquals("Closed", controller.getMsStatus(alce));

        alce.setDescription("{\"message\":\"bad json\"");
        assertNull(controller.getMsStatus(alce));
    }

}