package casino.noodle.commands.framework;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

public class CommandContext {
    private final ApplicationContext applicationContext;
    private final Message message;
    private final GatewayDiscordClient gatewayDiscordClient;
    private final Mono<GuildMessageChannel> channel;
    private final Mono<Member> member;

    public CommandContext(ApplicationContext applicationContext, Message message) {
        this.applicationContext = applicationContext;
        this.message = message;
        this.gatewayDiscordClient = message.getClient();
        this.channel = message.getChannel().cast(GuildMessageChannel.class);
        this.member = message.getAuthorAsMember();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Message getMessage() {
        return message;
    }

    public GatewayDiscordClient getGatewayDiscordClient() {
        return gatewayDiscordClient;
    }

    public Mono<GuildMessageChannel> getChannel() {
        return channel;
    }

    public Mono<Member> getMember() {
        return member;
    }
}
