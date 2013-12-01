package com.inha.stickyonpage.db;

import java.io.Serializable;
import java.util.Date;

/**
 * @author  Geunho Khim
 * @created 10/14/13, 2:14 PM
 * @updated 11/27/13
 *
 *  default comparable property is timestamp.
 */
public class Sticky implements Comparable<Sticky>, Serializable {
  private String url;
  private String userID;
  private String userName;
  private String memo;
  private Date created;
  private int like;

  public Sticky() {
  }
  public Sticky(String url, String userID, String userName, String memo, Date created, int like) {
    this.url = url;
    this.userID = userID;
    this.userName = userName;
    this.memo = memo;
    this.created = created;
    this.like = like;
  }

  public void setURL(String url) {
    this.url = url;
  }
  public void setUser(String userID) {
    this.userID = userID;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public void setMemo(String memo) {
    this.memo = memo;
  }
  public void setTimestamp(Date ts) {
    this.created = ts;
  }
  public void setLike(int like) {
    this.like = like;
  }

  public String getURL() {
    return this.url;
  }
  public String getUserID() {
    return this.userID;
  }
  public String getUserName() {
    return this.userName;
  }
  public String getMemo() {
    return this.memo;
  }
  public Date getTimestamp() {
    return this.created;
  }
  public int getLike() {
    return this.like;
  }

  @Override
  public String toString() {
    return "[" + getURL() + ", " + getUserID() + ", " + getUserName() + ", " + getMemo() + ", " + getTimestamp() + "]";
  }

  // compare class by timestamp
  @Override
  public int compareTo(Sticky sticky) {
    long a = this.getTimestamp().getTime();
    long b = sticky.getTimestamp().getTime();

    return a < b ? -1
         : a > b ? 1
         : 0;
  }
}
