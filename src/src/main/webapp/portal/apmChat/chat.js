
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

        // 中断上一次正在进行的打字
        if (this._typingAbort) this._typingAbort.aborted = true;
        this._typingAbort = { aborted: false };

        this.messages.forEach((msg, index) => {
            const element = this.createMessageElement(msg, index, this._typingAbort);
            container.appendChild(element);
        });
    }
    // 创建单个消息元素
    createMessageElement(message, index, abortFlag) {
        const isUser = message.type === 'user';
        const messageClass = isUser ? 'message-right' : 'message-left';

        const messageDiv = document.createElement('div');
        messageDiv.className = messageClass;

        // 用模板渲染外壳（包含头像/名字/typing指示器容器）
        messageDiv.innerHTML = this.getMessageHTML(message, isUser, index);

        // 找到文字容器：我们接下来只操作 <p class="message-text">
        const p = messageDiv.querySelector('.message-text');

        if (isUser) {
            // 用户消息：一次性渲染（安全：textContent）
            p.textContent = message.message ?? '';
        } else {
            const isLastAssistant = (index === this.messages.length - 1);
            if (isLastAssistant) {
                // 助手最后一条：打字机效果
                // 先确保有 typing 指示器（你已有 getTypingIndicator）
                // 逐字渲染
                typeInto(p, message.message ?? '', 15, abortFlag).then(() => {
                    // 结束后移除 typing 指示器
                    const typing = messageDiv.querySelector('.typing-indicator');
                    if (typing) typing.remove();
                });
            } else {
                // 历史助手消息：一次性渲染
                p.textContent = message.message ?? '';
                // 历史消息不应显示 typing 指示器，保守移除一下
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

        // 注意：这里保留 message-text 的占位 <p>，但不在这里塞 innerHTML
        // 文本在 createMessageElement 里用 textContent/打字函数渲染，避免 XSS
        // typing 指示器只给“最后一条助手消息”显示
        const shouldShowTyping = !isUser && index === this.messages.length - 1;

        return `
    <div class="message-content">
      <div class="user-info">
        <i class="${iconClass}" style="${color}"></i>
        <span class="${nameClass}" style="${color};white-space: pre-wrap;">${nameText}</span>
      </div>
      <p class="message-text"></p>
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
        const select = document.getElementById('knowledgeBaseSelect');
        const selectedText = select.options[select.selectedIndex].text;
        console.log(selectedText);
        const userName = document.getElementById('userName').value;
        axios.post( this.origin+`/geapm/web/apmChat/chat`,{
            paramStr: userMessage,
            userName: userName,
            selectStorage: selectedText
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

    // 生成智能回复
    generateSmartResponse(userMessage) {
        const responses = {
            greeting: [
                '您好！很高兴见到您。我是APM小管家，随时为您提供帮助和支持！',
                '欢迎使用APM小管家服务！有什么我可以为您做的吗？',
                '你好！我是您的智能助手APM小管家，很高兴为您服务！'
            ],
            help: [
                '我可以为您提供技术咨询、问题解答、学习指导等服务。请告诉我您具体需要什么帮助？',
                '请详细描述您遇到的问题，我会尽力为您提供最佳的解决方案！'
            ],
            thanks: [
                '不客气！能够帮助您是我的荣幸。如果您还有其他问题，请随时提问！',
                '很高兴为您服务！有什么需要随时联系我！'
            ],
            default: [
                '这是一个很好的问题，让我为您详细解答...',
                '根据您的需求，我建议采用以下方案...',
                '这个问题涉及到多个方面，我将逐一为您分析...',
                '感谢您的提问，这是一个常见的技术问题...',
                '基于您之前的对话记录，我为您提供更深入的解答...',
                '我理解您的困惑，让我用更简单的方式解释...'
            ]
        };

        // 关键词匹配
        if (userMessage.includes('你好') || userMessage.includes('您好')) {
            return this.getRandomResponse(responses.greeting);
        } else if (userMessage.includes('帮助') || userMessage.includes('支持')) {
            return this.getRandomResponse(responses.help);
        } else if (userMessage.includes('谢谢') || userMessage.includes('感谢')) {
            return this.getRandomResponse(responses.thanks);
        } else {
            return this.getRandomResponse(responses.default);
        }
    }

    // 获取随机回复
    getRandomResponse(responseArray) {
        const index = Math.floor(Math.random() * responseArray.length);
        return responseArray[index];
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
// function handleKnowledgeBaseChange(value) {
//     const select = document.getElementById('knowledgeBaseSelect');
//     const selectedText = select.options[select.selectedIndex].text;
//     document.getElementById('selectedText').textContent = selectedText;
//
//     // 添加视觉反馈
//     const selectionDiv = document.getElementById('currentSelection');
//     selectionDiv.classList.add('animate-pulse');
//     setTimeout(() => {
//         selectionDiv.classList.remove('animate-pulse');
//     }, 300);
//
//     // 保存选择到本地存储
//     localStorage.setItem('selectedKnowledgeBase', value);
//     localStorage.setItem('selectedKnowledgeBaseText', selectedText);
//
//     console.log(`切换到知识库: ${selectedText} (${value})`);
// }
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
