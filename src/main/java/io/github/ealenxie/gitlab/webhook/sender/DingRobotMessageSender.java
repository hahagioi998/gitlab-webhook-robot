package io.github.ealenxie.gitlab.webhook.sender;

import io.github.ealenxie.dingtalk.DingRobotClient;
import io.github.ealenxie.dingtalk.message.DingRobotAt;
import io.github.ealenxie.dingtalk.message.MarkdownMessage;
import io.github.ealenxie.gitlab.GitlabClient;
import io.github.ealenxie.gitlab.GitlabUserFactory;
import io.github.ealenxie.gitlab.config.GitlabConfig;
import io.github.ealenxie.gitlab.webhook.conf.WebHookConfig;
import io.github.ealenxie.gitlab.webhook.dto.MarkDownMsg;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EalenXie on 2022/2/11 16:29
 */
@Component
@ConditionalOnProperty(prefix = WebHookConfig.PREFIX, value = "way", havingValue = "ding")
public class DingRobotMessageSender implements MessageSender<MarkDownMsg, String> {

    private final DingRobotClient dingRobotClient;
    private final String accessToken;
    private final String signKey;
    private final GitlabUserFactory userFactory;


    public DingRobotMessageSender(WebHookConfig webHookConfig, RestTemplate httpClientRestTemplate, GitlabConfig gitlabConfig) {
        this.dingRobotClient = new DingRobotClient(httpClientRestTemplate);
        this.accessToken = webHookConfig.getDing().getAccessToken();
        this.signKey = webHookConfig.getDing().getSignKey();
        if (gitlabConfig.getHost() != null && gitlabConfig.getPrivateToken() != null) {
            this.userFactory = new GitlabUserFactory(new GitlabClient(gitlabConfig.getHost(), gitlabConfig.getPrivateToken()));
        } else {
            this.userFactory = null;
        }
    }


    @Override
    public ResponseEntity<String> sendMessage(MarkDownMsg markDownMsg) {
        MarkdownMessage actionCardMessage = new MarkdownMessage();
        StringBuilder sb = new StringBuilder();
        if (!markDownMsg.notifier().isEmpty()) {
            List<String> atMobiles = new ArrayList<>();
            if (userFactory != null) {
                List<String> notifier = markDownMsg.notifier();
                for (String s : notifier) {
                    String skype = userFactory.getUserSkype(Long.parseLong(s));
                    if (skype != null) {
                        sb.append("@").append(skype);
                        atMobiles.add(skype);
                    }
                }
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
            }
            DingRobotAt at = new DingRobotAt();
            at.setAtMobiles(atMobiles);
            actionCardMessage.setAt(at);
        }
        sb.append(markDownMsg.getMarkdown());
        MarkdownMessage.Markdown markdown = new MarkdownMessage.Markdown(markDownMsg.getTitle(), sb.toString());
        actionCardMessage.setMarkdown(markdown);
        return dingRobotClient.sendMessage(actionCardMessage, accessToken, signKey);
    }
}
