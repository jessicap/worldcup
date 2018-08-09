package src.com.pojo;

/**
 * Created by jessica on 2018/6/15.
 */
public class Ticket {
    // 获取的ticket
    private String ticket;
    // 有效时间，单位为秒，最大不超过1800
    private int expires_in;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getExpireSeconds() {
        return expires_in;
    }

    public void setExpireSeconds(int expires_in) {
        this.expires_in = expires_in;
    }
}
