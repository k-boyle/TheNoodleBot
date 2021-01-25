package casino.noodle.commands.framework;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import reactor.core.publisher.Mono;

public class CommandContext {
    private final BeanProvider beanProvider;
    private final Message message;
    private final GatewayDiscordClient gatewayDiscordClient;
    private final Mono<GuildMessageChannel> channel;
    private final Mono<Member> member;

    public CommandContext(BeanProvider beanProvider, Message message) {
        this.beanProvider = beanProvider;
        this.message = message;
        this.gatewayDiscordClient = message.getClient();
        this.channel = message.getChannel().cast(GuildMessageChannel.class);
        this.member = message.getAuthorAsMember();
    }

    public BeanProvider getBeanProvider() {
        return beanProvider;
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
