package loadManager.networkManagment;

import CF.sendable.ISendable;

public class MessageContent {
    private ISendable content;
    private EBuildCategory buildCategory;

    public MessageContent(ISendable content, EBuildCategory buildCategory) {
        this.content = content;
        this.buildCategory = buildCategory;
    }

    public ISendable getContent() {
        return content;
    }

    public EBuildCategory getBuildCategory() {
        return buildCategory;
    }
}
