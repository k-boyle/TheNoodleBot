package casino.noodle.commands.framework;

import com.google.common.base.MoreObjects;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import reactor.core.publisher.Mono;

public class CommandContext {
    private final Message message;
    private final GatewayDiscordClient gatewayDiscordClient;
    private final Mono<GuildMessageChannel> channel;
    private final Mono<Member> member;

    public CommandContext(Message message) {
        this.message = message;
        this.gatewayDiscordClient = message.getClient();
        this.channel = message.getChannel().cast(GuildMessageChannel.class);
        this.member = message.getAuthorAsMember();
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("message", message)
            .add("gatewayDiscordClient", gatewayDiscordClient)
            .add("channel", channel)
            .add("member", member)
            .toString();
    }
}
