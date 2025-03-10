package com.ge.apm.service.asset;

import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class PageBreakPluginFactory extends AbstractRenderPolicy<Object> {

    private int itemCount;
    private String pageBreak;

    private int currentIndex = 0;

    public PageBreakPluginFactory(int itemCount, String pageBreak){
        this.itemCount=itemCount;
        this.pageBreak=pageBreak;
    }

    @Override
    public void doRender(RenderContext<Object> renderContext) throws Exception {
        XWPFRun run = renderContext.getRun();
        currentIndex++;
        if (currentIndex < itemCount) {
            run.addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);
        }

        // 移除模板中的标记
        clearPlaceholder(renderContext, true);


    }

    protected void clearPlaceholder(RenderContext<?> renderContext, boolean someFlag) {
        XWPFRun run = renderContext.getRun();
        String text = run.getText(0);
        if (text != null) {
            // 假设占位符是 {{pageBreak}}，这里根据实际占位符修改
            text = text.replace(pageBreak, "");
            run.setText(text, 0);
        }
    }

}
