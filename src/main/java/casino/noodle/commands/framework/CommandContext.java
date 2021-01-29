package casino.noodle.commands.framework;

import discord4j.core.object.entity.Message;

public class CommandContext {
    private final BeanProvider beanProvider;
//    private final Message message;
//    private final GatewayDiscordClient gatewayDiscordClient;
//    private final Mono<GuildMessageChannel> channel;
//    private final Mono<Member> member;

    public CommandContext(BeanProvider beanProvider, Message message) {
        this.beanProvider = beanProvider;
//        this.message = message;
//        this.gatewayDiscordClient = message.getClient();
//        this.channel = message.getChannel().cast(GuildMessageChannel.class);
//        this.member = message.getAuthorAsMember();
    }

    public BeanProvider beanProvider() {
        return beanProvider;
    }

//    public Message message() {
//        return message;
//    }
//
//    public GatewayDiscordClient gatewayDiscordClient() {
//        return gatewayDiscordClient;
//    }
//
//    public Mono<GuildMessageChannel> channel() {
//        return channel;
//    }
//
//    public Mono<Member> member() {
//        return member;
//    }
}
