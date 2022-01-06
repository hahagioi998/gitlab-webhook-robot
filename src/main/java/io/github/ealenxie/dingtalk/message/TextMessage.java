package io.github.ealenxie.dingtalk.message;

import io.github.ealenxie.dingtalk.dto.Text;

/**
 * Created by EalenXie on 2021/12/27 10:58
 */
public class TextMessage extends DingRobotMessage {

    private static final String MSG_TYPE = "text";

    @Override
    public String getMsgType() {
        return MSG_TYPE;
    }

    private Text text;

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}