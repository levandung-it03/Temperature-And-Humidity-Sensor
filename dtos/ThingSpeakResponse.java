package dtos;

public class ThingSpeakResponse {
    private Channel channel;
    private Feed[] feeds;

    public ThingSpeakResponse() {super();}

    public ThingSpeakResponse(Channel channel, Feed[] feeds) {
        this.channel = channel;
        this.feeds = feeds;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Feed[] getFeeds() {
        return feeds;
    }

    public void setFeeds(Feed[] feeds) {
        this.feeds = feeds;
    }
}
