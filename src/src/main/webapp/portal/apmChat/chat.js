// APM小管家聊天应用核心逻辑

class APMChatApp {
    constructor() {
        this.messages = [];
        this.isTyping = false;
        this.origin = location.origin;
        this.init();
    }

    init() {
        this.bindEvents();
        this.renderMessages();
        this.scrollToBottom();
        marked.setOptions({
            gfm: true,
            breaks: false,
            smartLists: true,
            smartypants: false,
            highlight: (code, lang) => {
                try {
                    if (lang && hljs.getLanguage(lang)) {
                        return hljs.highlight(code, { language: lang }).value;
                    }
                    return hljs.highlightAuto(code).value;
                } catch {
                    return code;
                }
            }
        });
    }

    // 绑定事件监听器
    bindEvents() {
        // 发送按钮点击事件
        document.getElementById('sendButton').addEventListener('click', () => this.sendMessage());

        // 清空按钮点击事件
        document.getElementById('clearButton').addEventListener('click', () => this.clearChat());

        // 模拟对话按钮点击事件
        // document.getElementById('simulateButton').addEventListener('click', () => this.simulateChat());

        // 输入框键盘事件
        document.getElementById('newLogMessage').addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        // 监听窗口大小变化
        window.addEventListener('resize', () => this.handleResize());
    }

    // 发送消息
    sendMessage() {
        const input = document.getElementById('newLogMessage');
        const text = input.value.trim();

        if (!text) {
            this.showNotification('请输入消息内容', 'warning');
            return;
        }

        // 添加用户消息
        this.addMessage({
            type: 'user',
            userName: this.getUserName(),
            message: text,
            timestamp: new Date()
        });

        // 清空输入框
        input.value = '';

        // 模拟助手回复
        this.simulateAssistantResponse(text);
    }

    // 添加消息
    addMessage(message) {
        this.messages.push(message);
        this.renderMessages();
        this.scrollToBottom();
    }

    // 渲染所有消息
    renderMessages() {
        const container = document.getElementById('messagesList');
        container.innerHTML = '';

        // 中断上一次的打字
        if (this._typingAbort) this._typingAbort.aborted = true;
        this._typingAbort = { aborted: false };

        this.messages.forEach((msg, index) => {
            const element = this.createMessageElement(msg, index, this._typingAbort);
            container.appendChild(element);
        });
    }

    createMessageElement(message, index, abortFlag) {
        const isUser = message.type === 'user';
        const messageClass = isUser ? 'message-right' : 'message-left';

        const messageDiv = document.createElement('div');
        messageDiv.className = messageClass;
        messageDiv.innerHTML = this.getMessageHTML(message, isUser, index);

        const mdEl = messageDiv.querySelector('.message-markdown');

        const scrollToBottom = () => {
            const container = document.getElementById('messagesList');
            if (container) container.scrollTop = container.scrollHeight;
        };

        if (isUser) {
            // 用户消息：一次性把纯文本当作 Markdown 渲染也没问题
            renderMarkdownInto(mdEl, message.message ?? '');
        } else {
            const isLastAssistant = (index === this.messages.length - 1);
            if (isLastAssistant) {
                // 打字 + Markdown 流式
                typeMarkdown(mdEl, message.message ?? '', 12, abortFlag, scrollToBottom).then(() => {
                    const typing = messageDiv.querySelector('.typing-indicator');
                    if (typing) typing.remove();
                    scrollToBottom();
                });
            } else {
                // 历史消息：一次性渲染 Markdown
                renderMarkdownInto(mdEl, message.message ?? '');
                const typing = messageDiv.querySelector('.typing-indicator');
                if (typing) typing.remove();
            }
        }

        return messageDiv;
    }

    // 获取消息HTML
    getMessageHTML(message, isUser, index) {
        const iconClass = isUser ? 'fa fa-user-o' : 'fa fa-slideshare';
        const nameClass = isUser ? 'user-name' : 'user-name assistant-name';
        const color = isUser ? 'color: #0e77ca' : 'color: #7252bc';
        const nameText = isUser ? message.userName : 'APM小管家';
        const shouldShowTyping = !isUser && index === this.messages.length - 1;

        return `
            <div class="message-content">
              <div class="user-info">
                <i class="${iconClass}" style="${color}"></i>
                <span class="${nameClass}" style="${color}">${nameText}</span>
              </div>
              <!-- 用 div 容纳 Markdown -->
              <div class="message-text message-markdown"></div>
              ${shouldShowTyping ? this.getTypingIndicator() : ''}
            </div>
          `;
    }

    // 获取输入指示器HTML
    getTypingIndicator() {
        if (this.isTyping) {
            return `
            <div class="typing-indicator">
                <span>正在思考</span>
                <div class="typing-dots">
                    <div class="dot"></div>
                    <div class="dot"></div>
                    <div class="dot"></div>
                </div>
            </div>
        `;
        } else {
            return '';
        }
    }

    // 模拟助手回复
    simulateAssistantResponse(userMessage) {
        this.showTypingIndicator();
        // const select = document.getElementById('knowledgeBaseSelect');
        // const selectedText = select.options[select.selectedIndex].text;
        // console.log(selectedText);
        const userName = document.getElementById('userName').value;
        axios.post( this.origin+`/geapm/web/apmChat/chat`,{
            paramStr: userMessage,
            userName: userName
           // selectStorage: selectedText
        }).then(res => {
            if (res) {
                this.hideTypingIndicator();
                this.addMessage({
                    type: 'assistant',
                    message: res.data,
                    timestamp: new Date()
                });
            }
        });
    }

    // 显示输入指示器
    showTypingIndicator() {
        this.isTyping = true;
        const indicator = document.createElement('div');
        indicator.className = 'message-left';
        indicator.id = 'typingIndicator';
        indicator.innerHTML = `
            <div class="message-left">
                            <div class="message-content">
                                <div class="user-info">
                                    <i class="fa fa-slideshare" style="color: #7252bc"></i>
                                </div>
                            </div>
                            ${this.getTypingIndicator()}
                        </div>
        `;
        document.getElementById('messagesList').appendChild(indicator);
        this.scrollToBottom();
    }

    // 隐藏输入指示器
    hideTypingIndicator() {
        this.isTyping = false;
        const indicator = document.getElementById('typingIndicator');
        if (indicator) {
            indicator.remove();
        }
    }

    // 获取用户名
    getUserName() {
        var userName = document.getElementById('userName').value;
        return userName;
    }

    // 滚动到底部
    scrollToBottom() {
        const container = document.querySelector('.chat-container');
        if (container) {
            setTimeout(() => {
                container.scrollTop = container.scrollHeight;
            }, 50);
        }
    }

    // 清空聊天
    clearChat() {
        //if (confirm('确定要清空所有聊天记录吗？')) {
        this.messages = [];
        this.renderMessages();
        this.showNotification('聊天记录已清空', 'success');
      //  }
    }

    // 模拟对话
    simulateChat() {
        const simulatedMessages = [
            '请问APM系统的主要功能有哪些？',
            '我想了解如何配置APM监控参数',
            '系统出现性能问题应该如何处理？',
            '能给我一些优化建议吗？'
        ];

        let index = 0;

        const sendNext = () => {
            if (index < simulatedMessages.length) {
                this.addMessage({
                    type: 'user',
                    userName: '模拟用户',
                    message: simulatedMessages[index],
                    timestamp: new Date()
                });

                index++;
                setTimeout(sendNext, 2000 + Math.random() * 3000);
            }
        };
        sendNext();
    }

    // 显示通知
    showNotification(message, type = 'info') {
        // 实现通知显示逻辑
        console.log(`[${type.toUpperCase()}] ${message}`);
    }
    // 处理窗口大小变化
    handleResize() {
        this.scrollToBottom();
    }

    // 安全：避免把外部文本当作 HTML 注入
    createSafeP(text, className = 'message-text') {
        const p = document.createElement('p');
        p.className = className;
        p.textContent = text ?? '';
        return p;
    }
}
// 初始化应用
document.addEventListener('DOMContentLoaded', () => {
    new APMChatApp();
});

// 全局函数供HTML调用
function sendMessage() {
    if (window.chatApp) {
        window.chatApp.sendMessage();
    }
}

function clearChat() {
    if (window.chatApp) {
        window.chatApp.clearChat();
    }
}

function simulateChat() {
    if (window.chatApp) {
        window.chatApp.simulateChat();
    }
}

// 逐字输出（纯前端模拟）
async function typeInto(el, text, speed = 15, abortFlag) {
    // 先清空
    el.textContent = '';
    // 一次 append 文本节点，CSS 用 pre-wrap 处理换行与空格
    for (let i = 0; i < text.length; i++) {
        if (abortFlag?.aborted) return;
        el.textContent += text[i];
        // 控制速度
        await new Promise(r => setTimeout(r, speed));
    }
}

function renderMarkdownInto(el, mdText) {
    const rawHtml = marked.parse(mdText ?? '');
    const safeHtml = DOMPurify.sanitize(rawHtml, {
        ADD_ATTR: ['target', 'rel', 'class', 'id'],
        ADD_TAGS: ['span']
    });
    el.innerHTML = safeHtml;
}

async function typeMarkdown(el, fullText, speed = 12, abortFlag, onTick) {
    el.innerHTML = '';
    let buf = '';
    let lastRender = 0;

    const shouldRender = (now) => (now - lastRender) > 50; // 每 ~50ms 重渲染一次

    for (let i = 0; i < fullText.length; i++) {
        if (abortFlag?.aborted) return;

        buf += fullText[i];

        // 轻微“聪明停顿”：遇到标点/换行时更容易渲染一次
        const ch = fullText[i];
        const now = performance.now();
        const isPunct = (ch === '\n' || ch === '，' || ch === '。' || ch === ',' || ch === '.' || ch === '!' || ch === '?' || ch === '；' || ch === ';' );

        if (isPunct || shouldRender(now) || i === fullText.length - 1) {
            renderMarkdownInto(el, buf);

            // 仅在```成对闭合后再跑高亮，减少抖动
            const fencedCount = (buf.match(/```/g) || []).length;
            if (fencedCount % 2 === 0) {
                document.querySelectorAll('pre code').forEach(block => hljs.highlightElement(block));
            }

            lastRender = now;
            onTick && onTick();
        }

        await new Promise(r => setTimeout(r, speed));
    }
}
