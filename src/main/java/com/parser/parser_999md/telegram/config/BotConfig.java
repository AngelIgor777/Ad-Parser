package com.parser.parser_999md.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {
    @Value("${bot.name}")
    String botName;

    @Value("${bot.key}")
    String botKey;

}
