package com.inha.stickyonpage.db;

import java.util.Date;

/**
 * Geunho Khim
 * Date: 10/14/13
 * Time: 2:14 PM
 */
public class Sticky {
  private String url;
  private String userID;
  private String memo;
  private Date created;
  private int like;

  public Sticky() {
  }
  public Sticky(String url, String userID, String memo, Date created, int like) {
    this.url = url;
    this.userID = userID;
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
    return getURL() + ", " + getUserID() + ", " + getMemo() + ", " + getTimestamp();
  }
}
